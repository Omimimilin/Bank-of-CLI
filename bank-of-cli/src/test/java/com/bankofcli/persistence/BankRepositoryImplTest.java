package com.bankofcli.persistence;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.bankofcli.domain.Account;
import com.bankofcli.domain.Transaction;

public class BankRepositoryImplTest {

        private final BankRepository repository = new BankRepositoryImpl();

        @AfterEach
        void cleanup() {

                String deleteTransactions = """
                        DELETE FROM transactions
                        WHERE account_id IN (?, ?)
                        OR related_account_id IN (?, ?)
                        """;

                String deleteAccounts = """
                        DELETE FROM accounts
                        WHERE account_id IN (?, ?)
                        """;

                try (Connection connection = ConnectionFactory
                        .getConnectionFactory()
                        .getConnection();
                PreparedStatement transactionStatement =
                        connection.prepareStatement(deleteTransactions);
                PreparedStatement accountStatement =
                        connection.prepareStatement(deleteAccounts)) {

                transactionStatement.setInt(1, 1001);
                transactionStatement.setInt(2, 1002);
                transactionStatement.setInt(3, 1001);
                transactionStatement.setInt(4, 1002);

                transactionStatement.executeUpdate();

                accountStatement.setInt(1, 1001);
                accountStatement.setInt(2, 1002);

                accountStatement.executeUpdate();

                } catch (SQLException e) {
                throw new RuntimeException(e);
                }
        }

        @Test
        void createAccount() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                repository.createAccount(account);

                Account result = repository.findAccountById(1001);

                assertNotNull(result);
        }

        @Test
        void createAccount_FailAccountAlreadyExists() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                repository.createAccount(account);

                Account duplicateAccount = new Account(
                        1001,
                        5678,
                        new BigDecimal("200.00")
                );

                assertThrows(
                        IllegalStateException.class,
                        () -> repository.createAccount(duplicateAccount)
                );
        }

        @Test
        void findAccountById_ReturnAccount() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                repository.createAccount(account);

                Account result = repository.findAccountById(1001);

                assertNotNull(result);
                assertEquals(1001, result.getAccountId());
        }

        @Test
        void findAccountById_ReturnNullWhenAccountDoesNotExist() {

                Account result = repository.findAccountById(9999);

                assertNull(result);
        }

        @Test
        void deposit_IncreaseBalance() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                repository.createAccount(account);

                Account result = repository.deposit(
                        1001,
                        new BigDecimal("50.00")
                );

                assertEquals(
                        new BigDecimal("150.00"),
                        result.getBalance()
                );
        }

        @Test
        void deposit_FailWhenAccountDoesNotExist() {

                assertThrows(
                        IllegalArgumentException.class,
                        () -> repository.deposit(
                                9999,
                                new BigDecimal("50.00")
                        )
                );
        }

        @Test
        void withdraw_DecreaseBalance() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                repository.createAccount(account);

                Account result = repository.withdraw(
                        1001,
                        new BigDecimal("40.00")
                );

                assertEquals(
                        new BigDecimal("60.00"),
                        result.getBalance()
                );
        }

        @Test
        void withdraw_FailWhenAccountDoesNotExist() {

                assertThrows(
                        IllegalArgumentException.class,
                        () -> repository.withdraw(
                                9999,
                                new BigDecimal("40.00")
                        )
                );
        }

        @Test
        void transferMovesMoney() {

                Account sender = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                Account receiver = new Account(
                        1002,
                        5678,
                        new BigDecimal("50.00")
                );

                repository.createAccount(sender);
                repository.createAccount(receiver);

                repository.transfer(
                        1001,
                        1002,
                        new BigDecimal("30.00")
                );

                Account updatedSender = repository.findAccountById(1001);
                Account updatedReceiver = repository.findAccountById(1002);

                assertEquals(
                        new BigDecimal("70.00"),
                        updatedSender.getBalance()
                );

                assertEquals(
                        new BigDecimal("80.00"),
                        updatedReceiver.getBalance()
                );
        }

        @Test
        void transferFailsWhenAccountDoesNotExist() {

                Account sender = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                repository.createAccount(sender);

                assertThrows(
                        IllegalStateException.class,
                        () -> repository.transfer(
                                1001,
                                9999,
                                new BigDecimal("30.00")
                        )
                );
        }

        @Test
        void findTransactionsReturnsTransactions() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                repository.createAccount(account);

                Transaction transaction = new Transaction(
                        0,
                        1001,
                        "DEPOSIT",
                        new BigDecimal("50.00"),
                        null,
                        null
                );

                repository.createTransaction(transaction);

                List<Transaction> result =
                        repository.findTransactionsByAccountId(1001);

                assertEquals(1, result.size());
                assertEquals("DEPOSIT", result.get(0).getTransactionType());
                assertEquals(
                        new BigDecimal("50.00"),
                        result.get(0).getAmount()
                );
        }

        @Test
        void findTransactionsReturnsEmptyListForMissingAccount() {

                List<Transaction> result =
                        repository.findTransactionsByAccountId(9999);

                assertTrue(result.isEmpty());
        }

        @Test
        void createTransactionSavesTransaction() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                repository.createAccount(account);

                Transaction transaction = new Transaction(
                        0,
                        1001,
                        "DEPOSIT",
                        new BigDecimal("50.00"),
                        null,
                        null
                );

                repository.createTransaction(transaction);

                List<Transaction> result =
                        repository.findTransactionsByAccountId(1001);

                assertEquals(1, result.size());
                assertEquals(
                        "DEPOSIT",
                        result.get(0).getTransactionType()
                );
        }

        @Test
        void createTransactionFailsForMissingAccount() {

                Transaction transaction = new Transaction(
                        0,
                        9999,
                        "DEPOSIT",
                        new BigDecimal("50.00"),
                        null,
                        null
                );

                assertThrows(
                        IllegalStateException.class,
                        () -> repository.createTransaction(transaction)
                );
        }
}