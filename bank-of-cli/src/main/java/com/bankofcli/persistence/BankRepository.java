package com.bankofcli.persistence;

import java.math.BigDecimal;

import com.bankofcli.domain.Account;

public interface BankRepository {
    void createAccount(Account account);
    Account findAccountById(int accountId);
    Account deposit(int accountId, BigDecimal amount);
    Account withdraw(int accountId, BigDecimal amount);
    void transfer(int fromAccountId, int toAccountId, BigDecimal amount);
}
