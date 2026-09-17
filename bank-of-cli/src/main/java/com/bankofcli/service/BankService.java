package com.bankofcli.service;

import java.util.List;

import com.bankofcli.domain.Account;

/*Banking rules lik withdraw and deposit */

public interface BankService {
    void addAccount(Account account);
    List<Account> findAllAccount();
}