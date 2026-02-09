package com.bank.service;

import com.bank.domain.AccessLog;
import com.bank.domain.User;
import com.bank.repository.AccessLogRepository;
import com.bank.repository.UserRepository;

import java.sql.Timestamp;

public class UserService {

    private final UserRepository userRepository;
    private final AccessLogRepository accessLogRepository;

    public UserService(UserRepository userRepository, AccessLogRepository accessLogRepository) {
        this.userRepository = userRepository;
        this.accessLogRepository = accessLogRepository;
    }

    // 회원가입 처리
    public void signUp(User user) {
        // 유효성 검사
        validateUser(user);
        userRepository.save(user);
    }

    private void validateUser(User user) {
        // ID 중복 검사
        if (userRepository.findByUserId(user.getUserId()) != null) {
            throw new IllegalArgumentException("[오류] 이미 존재하는 아이디입니다: " + user.getUserId());
        }

        // 필수 값 검사
        if (user.getUserId() == null || user.getUserId().trim().isEmpty()) {
            throw new IllegalArgumentException("[오류] 아이디는 필수 입력 항목입니다.");
        }
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("[오류] 비밀번호는 필수 입력 항목입니다.");
        }
        if (user.getFirstName() == null || user.getFirstName().trim().isEmpty() ||
            user.getLastName() == null || user.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("[오류] 이름은 필수 입력 항목입니다.");
        }
        if (user.getSsn() == null || user.getSsn().trim().isEmpty()) {
            throw new IllegalArgumentException("[오류] 주민등록번호는 필수 입력 항목입니다.");
        }
        if (user.getPhoneNumber() == null || user.getPhoneNumber().trim().isEmpty()) {
            throw new IllegalArgumentException("[오류] 전화번호는 필수 입력 항목입니다.");
        }
        if (user.getAddress() == null || user.getAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("[오류] 주소는 필수 입력 항목입니다.");
        }
    }

    public User login(String userId, String password) {
        // ID로 사용자가 확인
        User existingUser = userRepository.findByUserId(userId);

        // 존재하지 않으면, 로그 남기지 않고 로그인 실패 처리
        if (existingUser == null) {
            return null;
        }

        // 존재하면, 로그를 남김
        User user = userRepository.findByUserIdAndPassword(userId, password);
        boolean isSuccess = (user != null);

        AccessLog log = AccessLog.builder()
                .accessDate(new Timestamp(System.currentTimeMillis()))
                .successOrNot(isSuccess)
                .userId(userId)
                .adminId(null)
                .build();

        accessLogRepository.save(log);

        return user; //user or null
    }
}