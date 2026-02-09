package com.bank.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private int accountId;
    private String accountStatus;
    private long balance;
    private long transferLimit;
    private Timestamp openingDate;
    private String ownerId;
    //추가 필드
    private String accountPassword;
    private int wrongPwCount;
}
