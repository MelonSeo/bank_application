package com.bank.controller;

import com.bank.domain.Account;
import com.bank.domain.Administrator;
import com.bank.domain.Transaction;
import com.bank.domain.User;
import com.bank.service.AdminService;

import java.util.List;
import java.util.Scanner;

public class AdminController {

    private final AdminService adminService;
    private final Scanner scanner;

    public AdminController(AdminService adminService, Scanner scanner) {
        this.adminService = adminService;
        this.scanner = scanner;
    }

    // 관리자 메인 메뉴
    public void showAdminMenu(Administrator admin) {
        while (true) {
            System.out.println("\n--- [관리자 모드] " + admin.getFirstName() + " ---");
            System.out.println("1. 전체 접속 로그 조회 (Access Logs)");
            System.out.println("2. 계좌 상태 변경 (Manage Account Status)");
            System.out.println("3. 전체 거래 내역 조회 (Audit Transactions)");
            System.out.println("4. 유저 삭제 (Delete User)");
            System.out.println("9. 로그아웃 (Logout)");
            System.out.print("선택 >> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    adminService.viewAllAccessLogs();
                    break;
                case "2":
                    // 상태 변경 전 전체 목록 출력
                    showAllAccountsWithStatus();

                    System.out.println("\n[관리자 권한] 계좌 상태를 변경합니다.");
                    System.out.print("대상 계좌번호: ");
                    try {
                        int accId = Integer.parseInt(scanner.nextLine());
                        System.out.print("변경할 상태 (ACTIVE / LOCKED / DORMANT / CLOSED): ");
                        String status = scanner.nextLine().toUpperCase();

                        adminService.changeAccountStatus(accId, status);

                    } catch (NumberFormatException e) {
                        System.out.println("[오류] 숫자를 입력해주세요.");
                    }
                    break;
                case "3":
                    // 전체 거래 내역 조회
                    showAllTransactions();
                    break;
                case "4": // 유저 삭제 메뉴 호출
                    showDeleteUserMenu();
                    break;
                case "9":
                    System.out.println("관리자 로그아웃.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }
    // 유저 삭제 메뉴
    private void showDeleteUserMenu() {
        // 전체 유저 목록
        List<User> users = adminService.getAllUsers();

        System.out.println("\n--- [전체 유저 목록] ---");
        System.out.printf("%-15s %-15s %-15s %-15s\n", "User ID", "이름", "전화번호", "가입일");
        System.out.println("------------------------------------------------------------------");
        for (User user : users) {
            System.out.printf("%-15s %-15s %-15s %-15s\n",
                    user.getUserId(),
                    user.getLastName() + user.getFirstName(),
                    user.getPhoneNumber(),
                    user.getSignUpDate().toString().substring(0, 10));
        }
        System.out.println("------------------------------------------------------------------");

        // 삭제할 유저 ID
        System.out.print("삭제할 유저의 ID를 입력하세요: ");
        String userIdToDelete = scanner.nextLine();

        // 삭제 요청
        adminService.deleteUser(userIdToDelete);
    }

    // 전체 계좌 출력 메서드
    private void showAllAccountsWithStatus() {
        List<Account> accounts = adminService.getAllAccounts();

        System.out.println("\n--- [전체 계좌 현황] ---");
        System.out.printf("%-10s %-10s %-10s %-15s %-10s %-10s\n",
                "계좌번호", "소유자", "상태", "잔액", "오류횟수", "개설일");
        System.out.println("--------------------------------------------------------------------------");

        for (Account acc : accounts) {
            System.out.printf("%-10d %-10s %-10s %-15d %-10d %-10s\n",
                    acc.getAccountId(),
                    acc.getOwnerId(),
                    acc.getAccountStatus(),
                    acc.getBalance(),
                    acc.getWrongPwCount(),
                    acc.getOpeningDate().toString().substring(0, 10));
        }
        System.out.println("--------------------------------------------------------------------------");
    }

    // 전체 거래 내역
    private void showAllTransactions() {
        List<Transaction> transactions = adminService.getAllTransactions();
        System.out.println("\n--- [전체 거래 내역] ---");
        if (transactions.isEmpty()) {
            System.out.println("거래 내역이 없거나 조회에 실패했습니다.");
        } else {
            System.out.println("\n[거래 내역]");
            System.out.printf("%-20s %-10s %-15s %-10s %-10s\n", "시간", "유형", "금액", "보낸분", "받은분");
            System.out.println("-----------------------------------------------------------------------");
            for (Transaction t : transactions) {
                System.out.printf("%-20s %-10s %-15d %-10s %-10s\n",
                        t.getTransactionTime().toString().substring(0, 19),
                        t.getTransactionType(),
                        t.getAmount(),
                        t.getWithdId() == null ? "-" : t.getWithdId().toString(),
                        t.getDeposId() == null ? "-" : t.getDeposId().toString()
                );
            }
            System.out.println("-----------------------------------------------------------------------");
        }
    }
}