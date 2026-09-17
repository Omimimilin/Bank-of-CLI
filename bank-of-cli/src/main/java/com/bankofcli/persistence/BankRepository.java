package com.bankofcli.persistence;

import com.bankofcli.domain.Account;

public interface BankRepository {
    Account findAccountById(int id);
    void save(Account account);
}
