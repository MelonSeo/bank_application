package com.bank.repository;

import com.bank.config.DBConnection;
import com.bank.domain.Account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {

    public void save(Account account) {
        String sql = "INSERT INTO Account (account_id, account_status, balance, transfer_limit, opening_date, owner_id, account_password) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, account.getAccountId());
            preparedStatement.setString(2, account.getAccountStatus());
            preparedStatement.setLong(3, account.getBalance());
            preparedStatement.setLong(4, account.getTransferLimit());
            preparedStatement.setTimestamp(5, account.getOpeningDate());
            preparedStatement.setString(6, account.getOwnerId()); // 계좌 소유 FK
            preparedStatement.setString(7, account.getAccountPassword());

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("[성공] 계좌가 개설되었습니다. 계좌번호: " + account.getAccountId());
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 내 계좌 목록
    public List<Account> findAllByOwnerId(String ownerId) {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM account WHERE owner_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, ownerId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    Account account = mapRowToAccount(resultSet);
                    accounts.add(account);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }
    // 잔액 변경 (입금/출금)
    public void updateBalance(int accountId, long amount) {
        // 현재 잔액에 + amount (출금 시 음수)
        String sql = "UPDATE account SET balance = balance + ? WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setLong(1, amount);
            preparedStatement.setInt(2, accountId);

            int result = preparedStatement.executeUpdate();
            if (result == 0) {
                System.out.println("[오류] 계좌번호를 찾을 수 없습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 계좌 ID로 상세 정보 조회 (비밀번호, 잔액 확인)
    public Account findById(int accountId) {
        String sql = "SELECT * FROM account WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, accountId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return mapRowToAccount(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 찾는 계좌 없음
    }

    // 계좌 이체 (트랜잭션: 출금 -> 입금 -> 기록)
    public boolean transfer(int fromId, int toId, long amount) {
        Connection conn = null;
        PreparedStatement preparedStatement = null;

        // 이체 쿼리
        String withdrawSql = "UPDATE account SET balance = balance - ? WHERE account_id = ?"; // 보내는 사람
        String depositSql = "UPDATE account SET balance = balance + ? WHERE account_id = ?";  // 받는 사람
        // 이체 시 depos_id와 withd_id 모두
        String logSql = "INSERT INTO transaction (transaction_type, amount, transaction_time, depos_id, withd_id) VALUES ('TRANSFER', ?, ?, ?, ?)";

        try {
            conn = DBConnection.getConnection();

            // 자동 커밋 끄기 (트랜잭션 시작, rollback 적용)
            conn.setAutoCommit(false);

            // 출금 (보내는 사람 감소)
            preparedStatement = conn.prepareStatement(withdrawSql);
            preparedStatement.setLong(1, amount);
            preparedStatement.setInt(2, fromId);
            int withdrawResult = preparedStatement.executeUpdate();
            preparedStatement.close();

            if (withdrawResult == 0) throw new SQLException("출금 실패");

            // 입금 (받는 사람 증가)
            preparedStatement = conn.prepareStatement(depositSql);
            preparedStatement.setLong(1, amount);
            preparedStatement.setInt(2, toId);
            int depositResult = preparedStatement.executeUpdate();
            preparedStatement.close();

            if (depositResult == 0) throw new SQLException("입금 실패 (존재하지 않는 계좌)");

            // 거래 내역 기록
            preparedStatement = conn.prepareStatement(logSql);
            preparedStatement.setLong(1, amount);
            preparedStatement.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis()));
            preparedStatement.setInt(3, toId);   // 입금 계좌
            preparedStatement.setInt(4, fromId); // 출금 계좌
            preparedStatement.executeUpdate();

            // **모든 단계 성공 시 커밋
            conn.commit();
            System.out.println("[DB 알림] 이체 처리가 완료되었습니다.");
            return true;

        } catch (SQLException e) {
            // 하나라도 실패하면 rollback
            try {
                if (conn != null) conn.rollback();
                System.out.println("[DB 알림] 오류 발생으로 롤백되었습니다.");
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            return false;
        } finally {
            // 자원 해제
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // 원래대로 복구
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    // 계좌 상태 변경
    public void updateAccountStatus(int accountId, String newStatus) {
        String sql = "UPDATE Account SET account_status = ? WHERE account_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, accountId);

            int result = preparedStatement.executeUpdate();
            if (result > 0) {
                System.out.println("[성공] 계좌(" + accountId + ") 상태가 '" + newStatus + "'로 변경되었습니다.");
            } else {
                System.out.println("[오류] 해당 계좌를 찾을 수 없습니다.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 모든 계좌 목록
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "SELECT * FROM Account ORDER BY account_id ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Account account = mapRowToAccount(resultSet);
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return accounts;
    }

    // account 매핑 메서드
    private Account mapRowToAccount(ResultSet rs) throws SQLException {
        return Account.builder()
                .accountId(rs.getInt("account_id"))
                .accountStatus(rs.getString("account_status"))
                .balance(rs.getLong("balance"))
                .transferLimit(rs.getLong("transfer_limit"))
                .openingDate(rs.getTimestamp("opening_date"))
                .ownerId(rs.getString("owner_id"))
                .accountPassword(rs.getString("account_password"))
                // [NEW] 아까 추가한 비밀번호 오류 횟수 매핑
                .wrongPwCount(rs.getInt("wrong_pw_count"))
                .build();
    }
    // 비밀번호 오류 1 증가
    public void increaseWrongPwCount(int accountId) {
        String sql = "UPDATE Account SET wrong_pw_count = wrong_pw_count + 1 WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 비밀번호 오류 횟수 초기화
    public void resetWrongPwCount(int accountId) {
        String sql = "UPDATE Account SET wrong_pw_count = 0 WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setInt(1, accountId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 계좌 비밀번호 변경
    public void updateAccountPassword(int accountId, String newPassword) {
        String sql = "UPDATE Account SET account_password = ? WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, newPassword);
            preparedStatement.setInt(2, accountId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 이체 한도 변경
    public void updateTransferLimit(int accountId, long newLimit) {
        String sql = "UPDATE Account SET transfer_limit = ? WHERE account_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setLong(1, newLimit);
            preparedStatement.setInt(2, accountId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}