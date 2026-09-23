package com.bankofcli.service;

import java.math.BigDecimal;

import com.bankofcli.domain.Account;

/*Banking rules like withdraw and deposit */

public interface BankService {
    void createAccount(Account account);
    Account login(int accountId, int pin);
    Account deposit(int accountId, BigDecimal amount);
    Account withdraw(int accountId, BigDecimal amount);
}