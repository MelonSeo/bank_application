package com.bank.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AccessLog {
    private int logId;
    private Timestamp accessDate;
    private boolean successOrNot;

    private String userId;
    private String adminId;
}
