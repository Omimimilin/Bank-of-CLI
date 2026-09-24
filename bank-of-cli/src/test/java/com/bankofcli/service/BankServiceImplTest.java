package com.bankofcli.service;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.bankofcli.domain.Account;
import com.bankofcli.domain.Transaction;
import com.bankofcli.persistence.BankRepository;

public class BankServiceImplTest {

        private BankRepository repository;
        private BankServiceImpl service;

        @BeforeEach
        void setup() {
                repository = mock(BankRepository.class);
                service = new BankServiceImpl(repository);
        }

        @Test
        void createAccountCreatesAccount() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("0.00")
                );

                when(repository.findAccountById(1001))
                        .thenReturn(null);

                assertDoesNotThrow(() ->
                        service.createAccount(account)
                );

                verify(repository).createAccount(account);
        }

        @Test
        void createAccountFailsWhenAccountExists() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("0.00")
                );

                when(repository.findAccountById(1001))
                        .thenReturn(account);

                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.createAccount(account)
                );

                verify(repository, never()).createAccount(account);
        }

        @Test
        void loginReturnsAccount() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                when(repository.findAccountById(1001))
                        .thenReturn(account);

                Account result = service.login(1001, 1234);

                assertNotNull(result);
                assertEquals(1001, result.getAccountId());
                assertEquals(1234, result.getPin());
        }

        @Test
        void loginFailsWithInvalidCredentials() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                when(repository.findAccountById(1001))
                        .thenReturn(account);

                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.login(1001, 9999)
                );
        }

        @Test
        void depositIncreasesBalance() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                Account updatedAccount = new Account(
                        1001,
                        1234,
                        new BigDecimal("150.00")
                );

                when(repository.findAccountById(1001))
                        .thenReturn(account);

                when(repository.deposit(1001, new BigDecimal("50.00")))
                        .thenReturn(updatedAccount);

                Account result = service.deposit(
                        1001,
                        new BigDecimal("50.00")
                );

                assertEquals(
                        new BigDecimal("150.00"),
                        result.getBalance()
                );

                verify(repository).deposit(
                        1001,
                        new BigDecimal("50.00")
                );

                verify(repository).createTransaction(any());
        }

        @Test
        void depositFailsWithInvalidAmount() {

                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.deposit(
                                1001,
                                BigDecimal.ZERO
                        )
                );

                verify(repository, never()).deposit(
                        anyInt(),
                        any(BigDecimal.class)
                );
        }

        @Test
        void withdrawDecreasesBalance() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                Account updatedAccount = new Account(
                        1001,
                        1234,
                        new BigDecimal("60.00")
                );

                when(repository.findAccountById(1001))
                        .thenReturn(account);

                when(repository.withdraw(1001, new BigDecimal("40.00")))
                        .thenReturn(updatedAccount);

                Account result = service.withdraw(
                        1001,
                        new BigDecimal("40.00")
                );

                assertEquals(
                        new BigDecimal("60.00"),
                        result.getBalance()
                );

                verify(repository).withdraw(
                        1001,
                        new BigDecimal("40.00")
                );

                verify(repository).createTransaction(any());
        }

        @Test
        void withdrawFailsWithInsufficientFunds() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("50.00")
                );

                when(repository.findAccountById(1001))
                        .thenReturn(account);

                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.withdraw(
                                1001,
                                new BigDecimal("100.00")
                        )
                );

                verify(repository, never()).withdraw(
                        anyInt(),
                        any(BigDecimal.class)
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

                Account updatedSender = new Account(
                        1001,
                        1234,
                        new BigDecimal("60.00")
                );

                when(repository.findAccountById(1001))
                        .thenReturn(sender, updatedSender);

                when(repository.findAccountById(1002))
                        .thenReturn(receiver);

                assertDoesNotThrow(() ->
                        service.transfer(
                                1001,
                                1002,
                                new BigDecimal("40.00")
                        )
                );

                verify(repository).transfer(
                        1001,
                        1002,
                        new BigDecimal("40.00")
                );

                verify(repository, times(2)).createTransaction(any());
        }

        @Test
        void transferFailsWithInsufficientFunds() {

                Account sender = new Account(
                        1001,
                        1234,
                        new BigDecimal("50.00")
                );

                Account receiver = new Account(
                        1002,
                        5678,
                        new BigDecimal("100.00")
                );

                when(repository.findAccountById(1001))
                        .thenReturn(sender);

                when(repository.findAccountById(1002))
                        .thenReturn(receiver);

                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.transfer(
                                1001,
                                1002,
                                new BigDecimal("100.00")
                        )
                );

                verify(repository, never()).transfer(
                        anyInt(),
                        anyInt(),
                        any(BigDecimal.class)
                );
        }

        @Test
        void getTransactionHistoryReturnsTransactions() {

                Account account = new Account(
                        1001,
                        1234,
                        new BigDecimal("100.00")
                );

                Transaction transaction = new Transaction(
                        1,
                        1001,
                        "DEPOSIT",
                        new BigDecimal("50.00"),
                        null,
                        null
                );

                when(repository.findAccountById(1001))
                        .thenReturn(account);

                when(repository.findTransactionsByAccountId(1001))
                        .thenReturn(List.of(transaction));

                List<Transaction> result =
                        service.getTransactionHistory(1001);

                assertEquals(1, result.size());
                assertEquals("DEPOSIT", result.get(0).getTransactionType());

                verify(repository, times(2)).findTransactionsByAccountId(1001);
        }

        @Test
        void getTransactionHistoryFailsForMissingAccount() {

                when(repository.findAccountById(1001))
                        .thenReturn(null);

                assertThrows(
                        IllegalArgumentException.class,
                        () -> service.getTransactionHistory(1001)
                );

                verify(repository, never())
                        .findTransactionsByAccountId(1001);
        }
}