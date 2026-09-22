package com.bankofcli.service;

import com.bankofcli.domain.Account;

/*Banking rules like withdraw and deposit */

public interface BankService {
    void createAccount(Account account);
    Account login(int accountId, int pin);
}