package com.bankofcli.persistence;

import java.math.BigDecimal;
import java.util.List;

import com.bankofcli.domain.Account;
import com.bankofcli.domain.Transaction;

public interface BankRepository {
    void createAccount(Account account);
    Account findAccountById(int accountId);
    Account deposit(int accountId, BigDecimal amount);
    Account withdraw(int accountId, BigDecimal amount);
    void transfer(int fromAccountId, int toAccountId, BigDecimal amount);
    List<Transaction> findTransactionsByAccountId(int accountId);
    void createTransaction(Transaction transaction);
}
