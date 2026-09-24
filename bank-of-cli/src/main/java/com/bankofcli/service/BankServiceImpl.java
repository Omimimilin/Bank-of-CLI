package com.bankofcli.service;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bankofcli.domain.Account;
import com.bankofcli.domain.Transaction;
import com.bankofcli.persistence.BankRepository;

public class BankServiceImpl implements BankService {
    private final BankRepository bankRepository;
    private static Logger logger = LoggerFactory.getLogger(BankServiceImpl.class);

    public BankServiceImpl(BankRepository bankRepository){
        this.bankRepository = bankRepository;
    }

    @Override
    public void createAccount(Account account) {
        if (bankRepository.findAccountById(account.getAccountId()) != null) {
            throw new IllegalArgumentException("Account ID already exists.");
        }

        bankRepository.createAccount(account);
        logger.info("Account {} successfully created.", account.getAccountId());
    }

    @Override
    public Account login(int accountId, int pin) {
        Account account = bankRepository.findAccountById(accountId);

        if (account == null || account.getPin() != pin) {
            throw new IllegalArgumentException("Invalid account ID or PIN.");
        }

        logger.info("Successful login for account {}", accountId);

        return account;
    }

    @Override
    public Account deposit(int accountId, BigDecimal amount){
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Deposit failed for account {}. Invalid amount: {}", accountId, amount);
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        if (bankRepository.findAccountById(accountId) == null) {
            logger.error("Deposit failed. Account {} does not exist.", accountId);
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
        logger.info("Deposit successful for account {}. Amount: {}", accountId, amount);

        return updatedAccount;
    }

    @Override
    public Account withdraw(int accountId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error("Withdraw failed for account {}. Invalid amount: {}", accountId, amount);
        throw new IllegalArgumentException(
                "Withdrawal amount must be greater than zero.");
        }

        Account account = bankRepository.findAccountById(accountId);

        if (account == null) {
            logger.error("Withdraw failed. Account {} does not exist.", accountId);
            throw new IllegalArgumentException("Account does not exist.");
        }

        if (amount.compareTo(account.getBalance()) > 0) {
            logger.error("Withdraw failed for account {}. Insufficient funds. Requested {}, Balance {}", accountId, amount, account.getBalance());
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
        logger.info("Withdraw successful for account {}. Amount {}", accountId, amount);

        return updatedAccount;
    }

    @Override
    public Account transfer(int fromAccountId, int toAccountId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            logger.error(
                "Transfer failed from account {}. Invalid amount: {}",
                fromAccountId,
                amount);
            throw new IllegalArgumentException(
                    "Transfer amount must be greater than zero.");
        }

        if (fromAccountId == toAccountId) {
            logger.error(
                "Transfer failed. Account {} attempted to transfer to itself.",
                fromAccountId
        );
            throw new IllegalArgumentException(
                    "You cannot transfer money to the same account.");
        }

        Account sender = bankRepository.findAccountById(fromAccountId);

        if (sender == null) {
            logger.error(
                "Transfer failed. Sender account {} does not exist.",
                fromAccountId
        );
            throw new IllegalArgumentException(
                    "Sender account does not exist.");
        }

        Account receiver = bankRepository.findAccountById(toAccountId);

        if (receiver == null) {
            logger.error(
                "Transfer failed. Recipient account {} does not exist.",
                toAccountId
        );
            throw new IllegalArgumentException(
                    "Recipient account does not exist.");
        }

        if (amount.compareTo(sender.getBalance()) > 0) {
            logger.error(
                "Transfer failed from account {} to account {}. Insufficient funds. Requested: {}, Balance: {}",
                fromAccountId,
                toAccountId,
                amount,
                sender.getBalance()
        );
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

        logger.info(
            "Transfer successful from account {} to account {}. Amount: {}",
            fromAccountId,
            toAccountId,
            amount
        );

        return bankRepository.findAccountById(fromAccountId);
    }

    @Override
    public List<Transaction> getTransactionHistory(int accountId) {

        if (bankRepository.findAccountById(accountId) == null) {
            logger.error(
                "Transaction history request failed. Account {} does not exist.",
                accountId
        );
            throw new IllegalArgumentException(
                    "Account does not exist.");
        }

        List<Transaction> transactions =
            bankRepository.findTransactionsByAccountId(accountId);

        logger.info(
            "Transaction history retrieved for account {}. {} transactions found.",
            accountId, transactions.size());

        return bankRepository.findTransactionsByAccountId(accountId);
    }
}
