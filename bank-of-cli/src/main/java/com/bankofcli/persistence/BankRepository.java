package com.bankofcli.persistence;

import com.bankofcli.domain.Account;

public interface BankRepository {
    void createAccount(Account account);
    Account findAccountById(int accountId);
}
