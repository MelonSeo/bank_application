package com.bank.repository;

import com.bank.config.DBConnection;
import com.bank.domain.AccessLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessLogRepository {

    public void save(AccessLog log) {
        String sql = "INSERT INTO access_log (access_date, success_or_not, user_id, admin_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setTimestamp(1, log.getAccessDate());
            preparedStatement.setBoolean(2, log.isSuccessOrNot());
            // User 로그인이면 userId 넣고, 아니면 NULL
            preparedStatement.setObject(3, log.getUserId());
            // Admin 로그인이면 adminId 넣고, 아니면 NULL
            preparedStatement.setObject(4, log.getAdminId());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 전체 접속 로그 조회
    public void printAllLogs() {
        String sql = "SELECT * FROM Access_Log ORDER BY access_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            System.out.println("\n--- [시스템 전체 접속 로그] ---");
            System.out.printf("%-5s %-20s %-10s %-10s %-10s\n", "ID", "시간", "성공여부", "User", "Admin");
            System.out.println("-------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-5d %-20s %-10s %-10s %-10s\n",
                        rs.getInt("log_id"),
                        rs.getTimestamp("access_date"),
                        rs.getBoolean("success_or_not") ? "성공" : "실패",
                        rs.getString("user_id") == null ? "-" : rs.getString("user_id"),
                        rs.getString("admin_id") == null ? "-" : rs.getString("admin_id")
                );
            }
            System.out.println("-------------------------------------------------------------");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}