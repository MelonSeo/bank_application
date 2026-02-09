package com.bank.domain;

import lombok.*;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private String userId;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String address;
    private String ssn;
    private String password;
    private Timestamp signUpDate;
}
