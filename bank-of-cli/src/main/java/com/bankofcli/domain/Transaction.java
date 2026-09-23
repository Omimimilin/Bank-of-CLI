package com.bankofcli.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {

    private int transactionId;
    private int accountId;
    private String transactionType;
    private BigDecimal amount;
    private Integer relatedAccountId;
    private LocalDateTime createdAt;

    public Transaction(
            int transactionId,
            int accountId,
            String transactionType,
            BigDecimal amount,
            Integer relatedAccountId,
            LocalDateTime createdAt) {

        this.transactionId = transactionId;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.relatedAccountId = relatedAccountId;
        this.createdAt = createdAt;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getAccountId() {
        return accountId;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getRelatedAccountId() {
        return relatedAccountId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}