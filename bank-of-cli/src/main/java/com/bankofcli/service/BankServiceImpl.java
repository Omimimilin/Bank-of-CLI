package com.bankofcli.service;

import java.math.BigDecimal;

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

    @Override
    public Account deposit(int accountId, BigDecimal amount){
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        if (bankRepository.findAccountById(accountId) == null) {
            throw new IllegalArgumentException("Account does not exist.");
        }

        return bankRepository.deposit(accountId, amount);
    }

    @Override 
    public Account withdraw(int accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException(
                "Withdrawal amount must be greater than zero.");
        }

        Account account = bankRepository.findAccountById(accountId);

        if (account == null) {
            throw new IllegalArgumentException("Account does not exist.");
        }

        if (amount.compareTo(account.getBalance()) > 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        return bankRepository.withdraw(accountId, amount);
    }
}
