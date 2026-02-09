package com.bank;

import com.bank.config.DBConnection;
import com.bank.controller.AccountController;
import com.bank.controller.AdminController;
import com.bank.controller.UserController;
import com.bank.domain.Administrator;
import com.bank.domain.User;
import com.bank.repository.*;
import com.bank.service.AccountService;
import com.bank.service.AdminService;
import com.bank.service.UserService;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try { // UTF-8 출력 설정
            System.setOut(new PrintStream(System.out, true, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        UserRepository userRepository = new UserRepository();
        AccessLogRepository accessLogRepository = new AccessLogRepository();
        AccountRepository accountRepository = new AccountRepository();
        TransactionRepository transactionRepository = new TransactionRepository();
        AdminRepository adminRepository = new AdminRepository();

        UserService userService = new UserService(userRepository, accessLogRepository);
        AccountService accountService = new AccountService(accountRepository, transactionRepository);
        AdminService adminService = new AdminService(adminRepository, accountRepository, accessLogRepository, transactionRepository, userRepository);

        Scanner scanner = new Scanner(System.in);
        UserController userController = new UserController(userService, scanner);
        AccountController accountController = new AccountController(accountService, scanner);
        AdminController adminController = new AdminController(adminService, scanner);

        while (true) {
            System.out.println("1. 로그인 (Login)");
            System.out.println("2. 회원가입 (Register)");
            System.out.println("0. 종료 (Exit)");
            System.out.print("선택 >> ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n--- [통합 로그인] ---");
                    System.out.print("ID: ");
                    String id = scanner.nextLine();
                    System.out.print("Password: ");
                    String pw = scanner.nextLine();

                    // 1. 일반 유저 로그인 시도
                    User user = userService.login(id, pw);
                    if (user != null) {
                        System.out.println(">> 사용자 로그인 성공");
                        showCustomerMenu(scanner, user, accountController);
                        break;
                    }

                    // 2. 유저 실패 시 관리자 로그인 시도
                    Administrator admin = adminService.login(id, pw);
                    if (admin != null) {
                        System.out.println(">> 관리자 로그인 성공");
                        // 관리자 메뉴
                        adminController.showAdminMenu(admin);
                        break;
                    }

                    System.out.println("[실패] 아이디 또는 비밀번호를 확인하세요.");
                    break;
                case "2":
                    userController.showSignUpMenu();
                    break;
                case "0":
                    System.out.println("---프로그램을 종료합니다.---");
                    return;
                default:
                    System.out.println("잘못된 선택입니다. 다시 시도하세요.");
            }
        }
    }

    private static void showCustomerMenu(Scanner scanner, User user, AccountController accountController) {
        while (true) {
            System.out.println("\n--- User Menu (" + user.getFirstName() + ") ---");
            System.out.println("1. 계좌 개설 (Open Account)");
            System.out.println("2. 내 계좌 조회 (My Accounts)");
            System.out.println("3. 입금 (Deposit)");
            System.out.println("4. 출금 (Withdraw)");
            System.out.println("5. 이체 (Transfer)");
            System.out.println("6. 계좌 상태 변경 (Change Account Status)");
            System.out.println("7. 거래 내역 조회 (Transaction History)");
            System.out.println("8. 비밀번호 변경 (Change Password)");
            System.out.println("9. 이체 한도 변경 (Change Transfer Limit)");
            System.out.println("0. 로그아웃 (Logout)");
            System.out.print("선택 >> ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    accountController.createAccountMenu(user);
                    break;
                case "2":
                    accountController.showMyAccounts(user);
                    break;
                case "3":
                    accountController.showDepositMenu(user);
                    break;
                case "4":
                    accountController.showWithdrawMenu(user);
                    break;
                case "5":
                    accountController.showTransferMenu(user);
                    break;
                case "6":
                    accountController.showChangeStatusMenu(user);
                    break;
                case "7":
                    accountController.showTransactionHistoryMenu(user);
                    break;
                case "8":
                    accountController.showChangePasswordMenu(user);
                    break;
                case "9":
                    accountController.showChangeLimitMenu(user);
                    break;
                case "0": // 로그아웃
                    System.out.println("로그아웃 되었습니다.");
                    return;
                default:
                    System.out.println("잘못된 입력입니다.");
            }
        }
    }
}