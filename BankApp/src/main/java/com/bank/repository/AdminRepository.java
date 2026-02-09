package com.bank.repository;

import com.bank.config.DBConnection;
import com.bank.domain.Administrator;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminRepository {

    // 관리자 로그인 검증
    public Administrator findByAdminIdAndPassword(String adminId, String password) {
        String sql = "SELECT * FROM Administrator WHERE admin_id = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, adminId);
            preparedStatement.setString(2, password);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return Administrator.builder()
                            .adminId(rs.getString("admin_id"))
                            .password(rs.getString("password")) // 비번 확인용
                            .firstName(rs.getString("first_name"))
                            .lastName(rs.getString("last_name"))
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Administrator findByAdminId(String adminId) {
        String sql = "SELECT * FROM Administrator WHERE admin_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, adminId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return Administrator.builder()
                            .adminId(rs.getString("admin_id"))
                            .password(rs.getString("password"))
                            .firstName(rs.getString("first_name"))
                            .lastName(rs.getString("last_name"))
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}