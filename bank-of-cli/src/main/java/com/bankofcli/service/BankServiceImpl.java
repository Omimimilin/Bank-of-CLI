package com.bankofcli.service;

import java.math.BigDecimal;
import java.util.List;

import com.bankofcli.domain.Account;
import com.bankofcli.domain.Transaction;
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

        Account updatedAccount = bankRepository.deposit(accountId, amount);

        Transaction transaction = new Transaction(
                0,
                accountId,
                "DEPOSIT",
                amount,
                null,
                null
        );

        bankRepository.createTransaction(transaction);

        return updatedAccount;
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

        Account updatedAccount = bankRepository.withdraw(accountId, amount);

        Transaction transaction = new Transaction(
                0,
                accountId,
                "WITHDRAW",
                amount,
                null,
                null
        );

        bankRepository.createTransaction(transaction);

        return updatedAccount;
    }

    @Override
    public Account transfer(int fromAccountId, int toAccountId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero.");
        }

        if (fromAccountId == toAccountId) {
            throw new IllegalArgumentException(
                    "You cannot transfer money to the same account.");
        }

        Account sender = bankRepository.findAccountById(fromAccountId);

        if (sender == null) {
            throw new IllegalArgumentException(
                    "Sender account does not exist.");
        }

        Account receiver = bankRepository.findAccountById(toAccountId);

        if (receiver == null) {
            throw new IllegalArgumentException(
                    "Recipient account does not exist.");
        }

        if (amount.compareTo(sender.getBalance()) > 0) {
            throw new IllegalArgumentException(
                    "Insufficient funds.");
        }

        bankRepository.transfer(fromAccountId, toAccountId, amount);

        Transaction senderTransaction = new Transaction(
                0,
                fromAccountId,
                "TRANSFER_OUT",
                amount,
                toAccountId,
                null
        );

        Transaction receiverTransaction = new Transaction(
                0,
                toAccountId,
                "TRANSFER_IN",
                amount,
                fromAccountId,
                null
        );

        bankRepository.createTransaction(senderTransaction);
        bankRepository.createTransaction(receiverTransaction);

        return bankRepository.findAccountById(fromAccountId);
    }

    @Override
    public List<Transaction> getTransactionHistory(int accountId) {

        if (bankRepository.findAccountById(accountId) == null) {
            throw new IllegalArgumentException(
                    "Account does not exist.");
        }

        return bankRepository.findTransactionsByAccountId(accountId);
    }
}
