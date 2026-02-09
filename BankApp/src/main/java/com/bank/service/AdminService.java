package com.bank.service;

import com.bank.domain.*;
import com.bank.repository.*;

import java.sql.Timestamp;
import java.util.List;

public class AdminService {

    private final AdminRepository adminRepository;
    private final AccountRepository accountRepository;
    private final AccessLogRepository accessLogRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public AdminService
            (AdminRepository adminRepository, AccountRepository accountRepository, AccessLogRepository accessLogRepository, TransactionRepository transactionRepository, UserRepository userRepository) { // [수정]
        this.adminRepository = adminRepository;
        this.accountRepository = accountRepository;
        this.accessLogRepository = accessLogRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    // 관리자 로그인
    public Administrator login(String adminId, String password) {
        // ID로 관리자가 존재 확인
        Administrator existingAdmin = adminRepository.findByAdminId(adminId);

        // 관리자가 존재하지 않으면, 로그를 남기지 않고 로그인 실패 처리
        if (existingAdmin == null) {
            return null;
        }

        // 관리자가 존재하면, 로그를 남김
        Administrator admin = adminRepository.findByAdminIdAndPassword(adminId, password);
        boolean isSuccess = (admin != null);

        AccessLog log = AccessLog.builder()
                .accessDate(new Timestamp(System.currentTimeMillis()))
                .successOrNot(isSuccess)
                .userId(null)       // 유저는 NULL
                .adminId(adminId)   // 존재하는 관리자 ID
                .build();

        accessLogRepository.save(log);

        return admin;
    }

    // 관리자 권한 유저 삭제
    public void deleteUser(String userId) {
        // 유저 존재 여부 확인
        User userToDelete = userRepository.findByUserId(userId);
        if (userToDelete == null) {
            System.out.println("[오류] 삭제하려는 유저 (" + userId + ")를 찾을 수 없습니다.");
            return;
        }

        // 유저 삭제 (DB ON DELETE CASCADE)
        try {
            userRepository.deleteById(userId);
            System.out.println("[성공] 유저 (" + userId + ")가 삭제되었습니다. 관련 계좌도 함께 삭제되었습니다.");
        } catch (Exception e) {
            System.err.println("[오류] 유저 (" + userId + ") 삭제 중 오류 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // 전체 로그 조회
    public void viewAllAccessLogs() {
        accessLogRepository.printAllLogs();
    }

    // 계좌 상태 변경
    public void changeAccountStatus(int accountId, String status) {
        // 유효한 상태값 확인
        if (!status.equals("ACTIVE") && !status.equals("LOCKED") && !status.equals("DORMANT") && !status.equals("CLOSED")) {
            System.out.println("[오류] 유효하지 않은 상태입니다. (ACTIVE, LOCKED, DORMANT, CLOSED 중 선택)");
            return;
        }
        accountRepository.updateAccountStatus(accountId, status);
        if (status.equals("ACTIVE")) {
            accountRepository.resetWrongPwCount(accountId);
            System.out.println("   ㄴ 계좌의 비밀번호 오류 횟수가 0으로 초기화되었습니다.");
        }
    }
    // 모든 계좌 목록
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }


    // 전체 유저 목록
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // 전체 거래 내역
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}