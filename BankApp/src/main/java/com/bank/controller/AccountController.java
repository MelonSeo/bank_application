package com.bank.controller;

import com.bank.domain.Account;
import com.bank.domain.Transaction;
import com.bank.domain.User;
import com.bank.service.AccountService;

import java.util.List;
import java.util.Scanner;

public class AccountController {

    private final AccountService accountService;
    private final Scanner scanner;

    public AccountController(AccountService accountService, Scanner scanner) {
        this.accountService = accountService;
        this.scanner = scanner;
    }

    public void createAccountMenu(User loggedInUser) {
        System.out.println("\n--- [계좌 개설] ---");
        System.out.println("고객님(" + loggedInUser.getFirstName() + ") 명의의 새 계좌를 개설합니다.");

        System.out.print("사용하실 계좌 비밀번호(4자리)를 입력하세요: ");
        String password = scanner.nextLine();

        if (password.length() != 4) {
            System.out.println("[오류] 비밀번호는 4자리 숫자로 설정해야 합니다.");
            return;
        }


        System.out.print("설정할 1회 이체 한도를 입력하세요 (예: 1000000): ");
        try {
            long limit = Long.parseLong(scanner.nextLine());

            // 로그인한 유저의 ID 넘김
            accountService.createAccount(loggedInUser.getUserId(), limit, password);

        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력해주세요.");
        }
    }

    // 내 계좌 목록
    public void showMyAccounts(User loggedInUser) {
        System.out.println("\n--- [내 계좌 목록] ---");

        List<Account> myAccounts = accountService.getMyAccounts(loggedInUser.getUserId());

        if (myAccounts.isEmpty()) {
            System.out.println("보유하신 계좌가 없습니다.");
        } else {
            // 헤더
            System.out.printf("%-15s %-10s %-15s %-15s\n", "계좌번호", "상태", "잔액", "이체한도");
            System.out.println("---------------------------------------------------------");

            for (Account acc : myAccounts) {
                System.out.printf("%-15d %-10s %-15d %-15d\n",
                        acc.getAccountId(),
                        acc.getAccountStatus(),
                        acc.getBalance(),
                        acc.getTransferLimit());
            }
            System.out.println("---------------------------------------------------------");
        }
    }
    // 입금 메뉴
    public void showDepositMenu(User loggedInUser) {
        System.out.println("\n--- [입금] ---");

        // 계좌 목록
        showMyAccounts(loggedInUser);

        System.out.print("입금할 계좌번호 입력: ");
        try {
            int accountId = Integer.parseInt(scanner.nextLine());

            System.out.print("입금액 입력: ");
            long amount = Long.parseLong(scanner.nextLine());

            accountService.deposit(accountId, amount);

        } catch (NumberFormatException e) {
            System.out.println("[오류] 숫자로 입력해주세요.");
        }
    }
    // 출금 메뉴
    public void showWithdrawMenu(User loggedInUser) {
        System.out.println("\n--- [출금] ---");
        showMyAccounts(loggedInUser);

        try {
            System.out.print("출금할 계좌번호 입력: ");
            int accountId = Integer.parseInt(scanner.nextLine());

            System.out.print("출금액 입력: ");
            long amount = Long.parseLong(scanner.nextLine());

            System.out.print("계좌 비밀번호(4자리) 입력: ");
            String password = scanner.nextLine();

            accountService.withdraw(loggedInUser.getUserId() ,accountId, amount, password);

        } catch (NumberFormatException e) {
            System.out.println("[오류] 숫자로 입력해주세요.");
        }
    }
    // 이체 메뉴
    public void showTransferMenu(User loggedInUser) {
        System.out.println("\n--- [계좌 이체] ---");
        showMyAccounts(loggedInUser); // 내 계좌 목록

        try {
            System.out.print("내 출금 계좌번호: ");
            int fromId = Integer.parseInt(scanner.nextLine());

            System.out.print("계좌 비밀번호(4자리): ");
            String password = scanner.nextLine();

            System.out.print("받을 계좌번호(상대방): ");
            int toId = Integer.parseInt(scanner.nextLine());

            System.out.print("이체할 금액: ");
            long amount = Long.parseLong(scanner.nextLine());

            accountService.transfer(loggedInUser.getUserId(),fromId, toId, amount, password);

        } catch (NumberFormatException e) {
            System.out.println("[오류] 숫자로 입력해주세요.");
        }
    }

    // 상태 변경 메뉴
    public void showChangeStatusMenu(User loggedInUser) {
        System.out.println("\n--- [계좌 상태 변경] ---");
        showMyAccounts(loggedInUser); // 목록 보여주기

        try {
            System.out.print("상태를 변경할 계좌번호: ");
            int accId = Integer.parseInt(scanner.nextLine());

            System.out.print("계좌 비밀번호: ");
            String password = scanner.nextLine();

            System.out.println("변경할 상태를 선택하세요:");
            System.out.println("1. 정상 (ACTIVE) - 입출금 가능");
            System.out.println("2. 휴면 (DORMANT) - 거래 제한");
            System.out.println("3. 해지 (CLOSED) - [주의] 복구 불가");
            System.out.print("선택 >> ");
            String choice = scanner.nextLine();

            String newStatus = "";
            switch (choice) {
                case "1": newStatus = "ACTIVE"; break;
                case "2": newStatus = "DORMANT"; break;
                case "3":
                    // 해지 시 재확인
                    System.out.print("정말로 해지하시겠습니까? 해지 후에는 복구가 불가능합니다. (Y/N): ");
                    String confirm = scanner.nextLine();
                    if (!confirm.equalsIgnoreCase("Y")) {
                        System.out.println("취소되었습니다.");
                        return;
                    }
                    newStatus = "CLOSED";
                    break;
                default:
                    System.out.println("잘못된 선택입니다.");
                    return;
            }

            accountService.changeStatusByUser(loggedInUser.getUserId(), accId, newStatus, password);

        } catch (NumberFormatException e) {
            System.out.println("[오류] 숫자로 입력해주세요.");
        }
    }
    // 거래 내역 조회 메뉴
    public void showTransactionHistoryMenu(User loggedInUser) {
        System.out.println("\n--- [거래 내역 조회] ---");
        showMyAccounts(loggedInUser);

        try {
            System.out.print("조회할 계좌번호: ");
            int accId = Integer.parseInt(scanner.nextLine());
            System.out.print("계좌 비밀번호: ");
            String pw = scanner.nextLine();

            List<Transaction> history = accountService.getTransactionHistory(loggedInUser.getUserId(), accId, pw);

            if (history.isEmpty()) {
                System.out.println("거래 내역이 없거나 조회에 실패했습니다.");
            } else {
                System.out.println("\n[거래 내역]");
                System.out.printf("%-20s %-10s %-15s %-10s %-10s\n", "시간", "유형", "금액", "보낸분", "받은분");
                System.out.println("-----------------------------------------------------------------------");
                for (Transaction t : history) {
                    System.out.printf("%-20s %-10s %-15d %-10s %-10s\n",
                            t.getTransactionTime().toString().substring(0, 19),
                            t.getTransactionType(),
                            t.getAmount(),
                            t.getWithdId() == null ? "-" : t.getWithdId().toString(), // 출금계좌
                            t.getDeposId() == null ? "-" : t.getDeposId().toString()  // 입금계좌
                    );
                }
                System.out.println("-----------------------------------------------------------------------");
            }

        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력하세요.");
        }
    }

    // 비밀번호 변경 메뉴
    public void showChangePasswordMenu(User loggedInUser) {
        System.out.println("\n--- [계좌 비밀번호 변경] ---");
        showMyAccounts(loggedInUser);

        try {
            System.out.print("대상 계좌번호: ");
            int accId = Integer.parseInt(scanner.nextLine());

            System.out.print("현재 비밀번호: ");
            String oldPw = scanner.nextLine();

            System.out.print("새로운 비밀번호(4자리): ");
            String newPw = scanner.nextLine();

            accountService.changeAccountPassword(loggedInUser.getUserId(), accId, oldPw, newPw);

        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력하세요.");
        }
    }

    // 이체 한도 변경 메뉴
    public void showChangeLimitMenu(User loggedInUser) {
        System.out.println("\n--- [이체 한도 변경] ---");
        showMyAccounts(loggedInUser);

        try {
            System.out.print("대상 계좌번호: ");
            int accId = Integer.parseInt(scanner.nextLine());

            System.out.print("계좌 비밀번호: ");
            String pw = scanner.nextLine();

            System.out.print("변경할 1회 한도 금액: ");
            long newLimit = Long.parseLong(scanner.nextLine());

            accountService.changeTransferLimit(loggedInUser.getUserId(), accId, newLimit, pw);

        } catch (NumberFormatException e) {
            System.out.println("숫자만 입력하세요.");
        }
    }
}