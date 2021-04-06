package br.com.legion.economy.transactions;

import lombok.Getter;

import java.util.Date;

@Getter
public class Transaction {

    private final Integer sourceId;
    private final TransactionType type;
    private final Date transactedAt;
    private final double amount;

    public Transaction(Integer sourceId, TransactionType type, Date transactedAt, double amount) {
        this.sourceId = sourceId;
        this.type = type;
        this.transactedAt = transactedAt;
        this.amount = amount;
    }
}
