package com.bankofcli.persistence;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

import com.bankofcli.domain.Account;

public class BankRepositoryImplTest {

    private final BankRepository repository = new BankRepositoryImpl();

    @Test
    void createAccount_shouldCreateAccount() {

        Account account = new Account(
                1001,
                1234,
                new BigDecimal("100.00")
        );

        repository.createAccount(account);

        Account result = repository.findAccountById(1001);

        assertNotNull(result);
    }
}