package com.bank.repository;

import com.bank.config.DBConnection;
import com.bank.domain.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    public void save(User user) {

        String sql = "INSERT INTO user (user_id, phone_number, first_name, last_name, address, ssn, password, signup_date) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
            PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, user.getUserId());
            preparedStatement.setString(2, user.getPhoneNumber());
            preparedStatement.setString(3, user.getFirstName());
            preparedStatement.setString(4, user.getLastName());
            preparedStatement.setString(5, user.getAddress());
            preparedStatement.setString(6, user.getSsn());
            preparedStatement.setString(7, user.getPassword());
            preparedStatement.setTimestamp(8, user.getSignUpDate());

            int result = preparedStatement.executeUpdate();

            if (result > 0) {
                System.out.println("User saved successfully: " + user.getUserId());
            } else {
                System.out.println("Failed to save user: " + user.getUserId());
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062) {
                System.err.println("Error: Duplicate entry for user ID " + user.getUserId());
            } else {
                System.err.println("SQL Error Code: " + e.getErrorCode());
                e.printStackTrace();
            }
        }
    }

    public User findByUserIdAndPassword(String userId, String password) {
        String sql = "SELECT * FROM User WHERE user_id = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, userId);
            preparedStatement.setString(2, password);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    // 일치하는 유저가 있으면 객체로 매핑해서 리턴
                    return User.builder()
                            .userId(rs.getString("user_id"))
                            .password(rs.getString("password"))
                            .firstName(rs.getString("first_name"))
                            .lastName(rs.getString("last_name"))
                            .phoneNumber(rs.getString("phone_number"))
                            .address(rs.getString("address"))
                            .ssn(rs.getString("ssn"))
                            .signUpDate(rs.getTimestamp("signup_date"))
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 해당하는 유저 없음
    }

    public User findByUserId(String userId) {
        String sql = "SELECT * FROM User WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, userId);

            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    // 일치하는 유저가 있으면 객체로 매핑해서 리턴
                    return User.builder()
                            .userId(rs.getString("user_id"))
                            .password(rs.getString("password"))
                            .firstName(rs.getString("first_name"))
                            .lastName(rs.getString("last_name"))
                            .phoneNumber(rs.getString("phone_number"))
                            .address(rs.getString("address"))
                            .ssn(rs.getString("ssn"))
                            .signUpDate(rs.getTimestamp("signup_date"))
                            .build();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 해당하는 유저 없음
    }

    public void deleteById(String userId) {
        String sql = "DELETE FROM User WHERE user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql)) {

            preparedStatement.setString(1, userId);
            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("User deleted successfully: " + userId);
            } else {
                System.out.println("Failed to delete user or user not found: " + userId);
            }

        } catch (SQLException e) {
            System.err.println("Error deleting user " + userId + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM User ORDER BY signup_date DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement preparedStatement = conn.prepareStatement(sql);
             ResultSet rs = preparedStatement.executeQuery()) {

            while (rs.next()) {
                User user = User.builder()
                        .userId(rs.getString("user_id"))
                        .password(rs.getString("password"))
                        .firstName(rs.getString("first_name"))
                        .lastName(rs.getString("last_name"))
                        .phoneNumber(rs.getString("phone_number"))
                        .address(rs.getString("address"))
                        .ssn(rs.getString("ssn"))
                        .signUpDate(rs.getTimestamp("signup_date"))
                        .build();
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }
}
