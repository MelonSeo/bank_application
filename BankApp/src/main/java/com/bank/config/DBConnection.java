package com.bank.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // DB 접속 정보 설정
    // localhost:3306 : (MySQL 기본 포트)
    // /bank_db : 데이터베이스 이름
    // ?serverTimezone=UTC : 시차 문제로 에러 나는 것을 방지
    private static final String URL = "jdbc:mysql://localhost:3306/bank_db?serverTimezone=Asia/Seoul&useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";     // MySQL 아이디
    private static final String PASSWORD = "[password]"; // MySQL 비밀번호

    // 연결 객체 메서드
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // 드라이버 로드 (Gradle에서 mysql-connector-j 라이브러리)
            Class.forName("com.mysql.cj.jdbc.Driver");

            // DB 연결
            conn = DriverManager.getConnection(URL, USER, PASSWORD);

        } catch (ClassNotFoundException e) {
            System.err.println("JDBC 드라이버를 찾을 수 없습니다.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("DB 연결 실패 URL, ID, Password 확인");
            e.printStackTrace();
        }
        return conn;
    }
}