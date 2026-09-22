package com.bankofcli.service;

import com.bankofcli.domain.Account;
import com.bankofcli.persistence.BankRepository;

public class BankServiceImpl implements BankService {
    private final BankRepository bankRepository;

    public BankServiceImpl(BankRepository bankRepository){
        this.bankRepository = bankRepository;
    }

    @Override
    public void createAccount(Account account) {
        if (bankRepository.findAccountById(account.getAccountId()) != null) {
            throw new IllegalArgumentException("Account ID already exists.");
        }

        bankRepository.createAccount(account);
    }

    @Override
    public Account login(int accountId, int pin) {
        Account account = bankRepository.findAccountById(accountId);

        if (account == null || account.getPin() != pin) {
            throw new IllegalArgumentException("Invalid account ID or PIN.");
        }

        return account;
    }
}
