package com.bank.repository;

import com.bank.config.DBConnection;
import com.bank.domain.Transaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepository {

    public void save(Transaction transaction) {
        // 입금, 출금, 이체 모두 처리하는 쿼리
        String sql = "INSERT INTO transaction (transaction_type, amount, transaction_time, depos_id, withd_id) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, transaction.getTransactionType());
            preparedStatement.setLong(2, transaction.getAmount());
            preparedStatement.setTimestamp(3, transaction.getTransactionTime());

            // deposId가 null이면 setNull 처리 (입금 시엔 값 있음, 출금 시엔 null)
            if (transaction.getDeposId() != null) {
                preparedStatement.setInt(4, transaction.getDeposId());
            } else {
                preparedStatement.setNull(4, java.sql.Types.INTEGER);
            }

            // withdId가 null이면 setNull 처리 (입금 시엔 null, 출금 시엔 값 있음)
            if (transaction.getWithdId() != null) {
                preparedStatement.setInt(5, transaction.getWithdId());
            } else {
                preparedStatement.setNull(5, java.sql.Types.INTEGER);
            }

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // 특정 계좌의 거래 내역 조회 (입금 + 출금)
    public List<Transaction> findAllByAccountId(int accountId) {
        List<Transaction> transactions = new ArrayList<>();
        // 내 계좌가 입금이거나 출금인 경우 모두 조회
        String sql = "SELECT * FROM Transaction WHERE depos_id = ? OR withd_id = ? ORDER BY transaction_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setInt(1, accountId);
            preparedStatement.setInt(2, accountId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    transactions.add(mapRowToTransaction(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    // 전체 거래 내역 조회
    public List<Transaction> findAll() {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM Transaction ORDER BY transaction_time DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return transactions;
    }
    // ResultSet -> Transaction 매핑
    private Transaction mapRowToTransaction(ResultSet rs) throws SQLException {
        int deposId = rs.getInt("depos_id");
        if (rs.wasNull()) deposId = 0; // NULL이면 0 처리

        int withdId = rs.getInt("withd_id");
        if (rs.wasNull()) withdId = 0;

        return Transaction.builder()
                .transactionId(rs.getInt("transaction_id"))
                .transactionType(rs.getString("transaction_type"))
                .amount(rs.getLong("amount"))
                .transactionTime(rs.getTimestamp("transaction_time"))
                // 0이면 null로 변환
                .deposId(deposId == 0 ? null : deposId)
                .withdId(withdId == 0 ? null : withdId)
                .build();
    }
}