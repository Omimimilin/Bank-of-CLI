package com.bankofcli.service;

import java.math.BigDecimal;
import java.util.List;

import com.bankofcli.domain.Account;
import com.bankofcli.domain.Transaction;

/*Banking rules like withdraw and deposit */

public interface BankService {
    void createAccount(Account account);
    Account login(int accountId, int pin);
    Account deposit(int accountId, BigDecimal amount);
    Account withdraw(int accountId, BigDecimal amount);
    Account transfer(int fromAccountId, int toAccountId, BigDecimal amount);
    List<Transaction> getTransactionHistory(int accountId);
}