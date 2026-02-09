package com.bank.service;

import com.bank.domain.Account;
import com.bank.domain.Transaction;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final Random random = new Random();

    public AccountService(AccountRepository accountRepository, TransactionRepository transactionRepository) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
    }

    // 계좌 개설
    public void createAccount(String userId, long transferLimit, String password) {
        int newAccountId;
        // 고유한 계좌번호 생성 (중복 체크)
        do {
            newAccountId = 1000 + random.nextInt(9000); // 1000 ~ 9999
        } while (checkIfAccountIdExists(newAccountId));


        // 계좌 객체 기본값
        Account newAccount = Account.builder()
                .accountId(newAccountId)
                .accountStatus("ACTIVE")      // 초기 상태: 정상
                .balance(0L)                  // 초기 잔액: 0원
                .transferLimit(transferLimit) // 이체 한도 (사용자 설정)
                .openingDate(new Timestamp(System.currentTimeMillis()))
                .ownerId(userId)              // FK: 로그인한 사용자 ID
                .accountPassword(password)
                .build();

        // 저장 요청
        accountRepository.save(newAccount);
    }

    // 계좌번호 존재 확인
    private boolean checkIfAccountIdExists(int accountId) {
        return accountRepository.findById(accountId) != null;
    }

    // 내 계좌 목록 가져오기
    public List<Account> getMyAccounts(String userId) {
        return accountRepository.findAllByOwnerId(userId);
    }
    // 입금
    public void deposit(int accountId, long amount) {
        // 금액 체크
        if (amount <= 0) {
            System.out.println("[오류] 입금액은 0원보다 커야 합니다.");
            return;
        }

        // 계좌 정보 조회 (상태 확인)
        Account account = accountRepository.findById(accountId);

        // 계좌 존재 여부
        if (account == null) {
            System.out.println("[오류] 존재하지 않는 계좌번호입니다.");
            return;
        }

        // 계좌 상태에 따른 입금 제한
        String status = account.getAccountStatus();

        if (status.equals("LOCKED")) {
            System.out.println("[거부] 잠금(LOCKED)된 계좌입니다. 입금이 불가능합니다.");
            return;
        }
        if (status.equals("DORMANT")) {
            System.out.println("[거부] 휴면(DORMANT) 계좌입니다. 상태를 '정상'으로 변경 후 이용하세요.");
            return;
        }
        if (status.equals("CLOSED")) {
            System.out.println("[거부] 해지(CLOSED)된 계좌입니다. 입금이 불가능합니다.");
            return;
        }

        // 모든 검증 통과 후 입금
        accountRepository.updateBalance(accountId, amount);

        // 거래 내역
        Transaction transaction = Transaction.builder()
                .transactionType("DEPOSIT")
                .amount(amount)
                .transactionTime(new java.sql.Timestamp(System.currentTimeMillis()))
                .deposId(accountId)
                .withdId(null)
                .build();

        transactionRepository.save(transaction);

        System.out.println("[성공] " + amount + "원이 입금되었습니다.");
    }
    // 출금
    public void withdraw(String currentUserId, int accountId, long amount, String password) {
        // 계좌 존재 여부 및 정보
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            System.out.println("[오류] 존재하지 않는 계좌입니다.");
            return;
        }
        // 본인 계좌 확인
        if (!account.getOwnerId().equals(currentUserId)) {
            System.out.println("[거부] 본인의 계좌에서만 출금할 수 있습니다.");
            return;
        }

        // 비밀번호 검증
        if (!checkPasswordAndHandleStatus(account, password)) {
            return; // 검증 실패 시 중단
        }

        // 거래 가능 상태인지 확인
        String status = account.getAccountStatus();
        if (status.equals("DORMANT")) {
            System.out.println("[거부] 휴면(DORMANT) 계좌입니다. 상태를 '정상'으로 변경 후 이용하세요.");
            return;
        }
        if (status.equals("CLOSED")) {
            System.out.println("[거부] 해지(CLOSED)된 계좌입니다.");
            return;
        }

        // 잔액 검증
        if (account.getBalance() < amount) {
            System.out.println("[오류] 잔액이 부족합니다. (현재 잔액: " + account.getBalance() + "원)");
            return;
        }

        // 출금 실행
        accountRepository.updateBalance(accountId, -amount);

        // 거래 내역 기록
        Transaction transaction = Transaction.builder()
                .transactionType("WITHDRAW")
                .amount(amount)
                .transactionTime(new Timestamp(System.currentTimeMillis()))
                .deposId(null)       // 입금 계좌 없음
                .withdId(accountId)  // 출금 계좌 기록
                .build();

        transactionRepository.save(transaction);

        System.out.println("[성공] " + amount + "원이 출금되었습니다.");
    }
    // 이체
    public void transfer(String currentUserId, int fromAccountId, int toAccountId, long amount, String password) {
        Account fromAccount = accountRepository.findById(fromAccountId);
        // 보내는 사람 계좌 정보 확인
        if (fromAccount == null) {
            System.out.println("[오류] 보내는 계좌가 존재하지 않습니다.");
            return;
        }
        // 자기 자신에게 이체 불가
        if (fromAccountId == toAccountId) {
            System.out.println("[오류] 출금 계좌와 입금 계좌가 같습니다.");
            return;
        }
        // 본인 계좌 확인
        if (!fromAccount.getOwnerId().equals(currentUserId)) {
            System.out.println("[거부] 본인의 계좌에서만 출금할 수 있습니다.");
            return;
        }

        // 금액 체크
        if (amount <= 0) {
            System.out.println("[오류] 이체 금액은 0원보다 커야 합니다.");
            return;
        }

        // 비밀번호 검증
        if (!checkPasswordAndHandleStatus(fromAccount, password)) {
            return; // 검증 실패 시 중단
        }

        // 거래 가능 상태인지 확인
        String status = fromAccount.getAccountStatus();
        if (status.equals("DORMANT")) {
            System.out.println("[거부] 휴면(DORMANT) 계좌입니다. 상태를 '정상'으로 변경 후 이용하세요.");
            return;
        }
        if (status.equals("CLOSED")) {
            System.out.println("[거부] 해지(CLOSED)된 계좌입니다.");
            return;
        }

        // 잔액 검증
        if (fromAccount.getBalance() < amount) {
            System.out.println("[오류] 잔액이 부족합니다.");
            return;
        }

        // 이체 한도 검증
        if (amount > fromAccount.getTransferLimit()) {
            System.out.println("[오류] 1회 이체 한도를 초과했습니다. (한도: " + fromAccount.getTransferLimit() + "원)");
            return;
        }

        // 받는 사람 계좌 존재 확인
        Account toAccount = accountRepository.findById(toAccountId);
        if (toAccount == null) {
            System.out.println("[오류] 받는 사람의 계좌번호가 존재하지 않습니다.");
            return;
        }

        // 받는 사람 계좌 상태 확인
        String toStatus = toAccount.getAccountStatus();
        if (toStatus.equals("LOCKED")) {
            System.out.println("[거부] 상대방 계좌가 잠금(LOCKED) 상태라 이체할 수 없습니다.");
            return;
        }
        if (toStatus.equals("DORMANT")) {
            System.out.println("[거부] 상대방 계좌가 휴면(DORMANT) 상태라 이체할 수 없습니다.");
            return;
        }
        if (toStatus.equals("CLOSED")) {
            System.out.println("[거부] 상대방 계좌는 해지(CLOSED)된 계좌입니다.");
            return;
        }

        // 모든 검증 통과 - 트랜잭션 실행
        boolean success = accountRepository.transfer(fromAccountId, toAccountId, amount);

        if (success) {
            System.out.println("[성공] " + amount + "원을 이체했습니다.");
            System.out.println("받는 분: " + toAccount.getOwnerId() + " (계좌: " + toAccountId + ")");
        } else {
            System.out.println("[실패] 시스템 오류로 이체가 취소되었습니다.");
        }
    }
    // 유저가 직접 상태 변경
    public void changeStatusByUser(String currentUserId, int accountId, String newStatus, String password) {
        // 계좌 확인
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            System.out.println("[오류] 존재하지 않는 계좌입니다.");
            return;
        }
        // 본인 계좌 확인
        if (!account.getOwnerId().equals(currentUserId)) {
            System.out.println("[거부] 본인 소유의 계좌가 아닙니다.");
            return;
        }

        // 비밀번호 검증
        if (!checkPasswordAndHandleStatus(account, password)) {
            return;
        }

        // 현재 상태 체크
        String currentStatus = account.getAccountStatus();

        if (currentStatus.equals("LOCKED")) {
            System.out.println("[거부] 해당 계좌는 잠금 상태입니다. 관리자에게 문의하여 해제하세요.");
            return;
        }

        if (currentStatus.equals("CLOSED")) {
            System.out.println("[거부] 이미 해지된 계좌는 상태를 변경할 수 없습니다.");
            return;
        }

        // 입력된 상태값 검증
        // 유저는 LOCKED 불가
        if (!newStatus.equals("ACTIVE") && !newStatus.equals("DORMANT") && !newStatus.equals("CLOSED")) {
            System.out.println("[오류] 변경 가능한 상태가 아닙니다. (ACTIVE, DORMANT, CLOSED 만 가능)");
            return;
        }

        // 상태 업데이트
        accountRepository.updateAccountStatus(accountId, newStatus);
        //System.out.println("[성공] 계좌 상태가 '" + newStatus + "'(으)로 변경되었습니다.");
    }
    // 비밀번호 검증 및 상태 관리 메서드
    // 리턴: true/false
    private boolean checkPasswordAndHandleStatus(Account account, String inputPassword) {
        // 이미 잠긴 계좌인지 먼저 확인
        if (account.getAccountStatus().equals("LOCKED")) {
            System.out.println("[거부] 비밀번호 3회 오류로 잠긴 계좌입니다. 관리자에게 문의하세요.");
            return false;
        }

        // 비밀번호 일치 확인
        if (account.getAccountPassword().equals(inputPassword)) {
            // 맞았으면 카운트 초기화
            accountRepository.resetWrongPwCount(account.getAccountId());
            return true;
        } else {
            // 틀렸으면 카운트 증가
            accountRepository.increaseWrongPwCount(account.getAccountId());

            // +1
            int newCount = account.getWrongPwCount() + 1;
            System.out.println("[오류] 비밀번호가 일치하지 않습니다. (오류 횟수: " + newCount + "/3)");

            // 3회 도달 시 잠금 처리
            if (newCount >= 3) {
                accountRepository.updateAccountStatus(account.getAccountId(), "LOCKED");
                System.out.println("!!! [경고] 비밀번호 3회 연속 오류로 계좌가 잠금(LOCKED) 처리되었습니다. !!!");
            }
            return false;
        }
    }
    // 내 계좌 거래내역
    public List<Transaction> getTransactionHistory(String currentUserId, int accountId, String password) {
        // 계좌 확인
        Account account = accountRepository.findById(accountId);
        if (account == null) {
            System.out.println("[오류] 존재하지 않는 계좌입니다.");
            return new ArrayList<>();
        }

        // 본인 확인
        if (!account.getOwnerId().equals(currentUserId)) {
            System.out.println("[거부] 본인의 계좌 내역만 조회할 수 있습니다.");
            return new ArrayList<>();
        }

        // 비밀번호 확인
        if (!checkPasswordAndHandleStatus(account, password)) {
            return new ArrayList<>();
        }

        // 조회 실행
        return transactionRepository.findAllByAccountId(accountId);
    }
    // 계좌 비밀번호 변경
    public void changeAccountPassword(String currentUserId, int accountId, String oldPassword, String newPassword) {
        Account account = accountRepository.findById(accountId);
        if (account == null) { System.out.println("[오류] 계좌 없음"); return; }
        if (!account.getOwnerId().equals(currentUserId)) { System.out.println("[거부] 본인 계좌 아님"); return; }

        // 기존 비밀번호 검증, 틀리면 카운트 증가
        if (!checkPasswordAndHandleStatus(account, oldPassword)) {
            return;
        }

        // 새 비밀번호 유효성 검사
        if (newPassword.length() != 4) {
            System.out.println("[오류] 비밀번호는 4자리 숫자여야 합니다.");
            return;
        }

        accountRepository.updateAccountPassword(accountId, newPassword);
        System.out.println("[성공] 계좌 비밀번호가 변경되었습니다.");
    }

    // 이체 한도 변경
    public void changeTransferLimit(String currentUserId, int accountId, long newLimit, String password) {
        Account account = accountRepository.findById(accountId);
        if (account == null) { System.out.println("[오류] 계좌 없음"); return; }
        if (!account.getOwnerId().equals(currentUserId)) { System.out.println("[거부] 본인 계좌 아님"); return; }

        // 비밀번호 검증
        if (!checkPasswordAndHandleStatus(account, password)) {
            return;
        }

        // 한도 값 검증
        if (newLimit < 0) {
            System.out.println("[오류] 한도는 0원 이상이어야 합니다.");
            return;
        }

        accountRepository.updateTransferLimit(accountId, newLimit);
        System.out.println("[성공] 이체 한도가 " + newLimit + "원으로 변경되었습니다.");
    }
}