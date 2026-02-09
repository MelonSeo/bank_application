package com.bank.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Administrator {
    private String adminId;
    private String phoneNumber;
    private String firstName;
    private String lastName;
    private String address;
    private String ssn;
    private String password;
}
