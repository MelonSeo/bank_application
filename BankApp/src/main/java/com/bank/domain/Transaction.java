package com.bank.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    private int transactionId;
    private Timestamp transactionTime;
    private long amount;
    private String transactionType;
    private int fromAccountId;
    private int toAccountId;

    private Integer deposId;
    private Integer withdId;
}
