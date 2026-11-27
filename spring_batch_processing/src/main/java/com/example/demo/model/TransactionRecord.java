package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRecord {
    private String transactionId;
    private String accountNumber;
    private double amount;
    private String status;
}

