package com.bank.controller;

import com.bank.domain.User;
import com.bank.service.UserService;

import java.sql.Timestamp;
import java.util.Scanner;

public class UserController {

    private final UserService userService;
    private final Scanner scanner;

    public UserController(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }

    public void showSignUpMenu() {
        System.out.println("\n--- [회원가입] 정보를 입력해주세요. ---");

        System.out.print("아이디(ID): ");
        String userId = scanner.nextLine();

        System.out.print("비밀번호: ");
        String password = scanner.nextLine();

        System.out.print("이름(First Name): ");
        String firstName = scanner.nextLine();

        System.out.print("성(Last Name): ");
        String lastName = scanner.nextLine();

        System.out.print("연락처: ");
        String phoneNumber = scanner.nextLine();

        System.out.print("주소: ");
        String address = scanner.nextLine();

        System.out.print("주민번호(SSN): ");
        String ssn = scanner.nextLine();

        // User 객체 생성
        User newUser = User.builder()
                .userId(userId)
                .password(password)
                .firstName(firstName)
                .lastName(lastName)
                .phoneNumber(phoneNumber)
                .address(address)
                .ssn(ssn)
                .signUpDate(new Timestamp(System.currentTimeMillis())) // 가입일은 현재 시간
                .build();

        try {
            userService.signUp(newUser);
            System.out.println("[성공] 회원가입이 완료되었습니다. 메인 메뉴로 이동합니다.");
        } catch (IllegalArgumentException e) {
            // 서비스에서 발생한 유효성 검사 오류를 잡아서 메시지 출력
            System.out.println(e.getMessage());
        }
    }

}