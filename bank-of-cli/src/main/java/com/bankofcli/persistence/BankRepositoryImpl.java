package com.bankofcli.persistence;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.bankofcli.domain.Account;

public class BankRepositoryImpl implements BankRepository{

    @Override
    public void createAccount(Account account){
        String sql = """
                INSERT INTO accounts (account_id, pin, balance)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            
            System.out.println("Database: " + connection.getCatalog());
            System.out.println("Schema: " + connection.getSchema());
            
            statement.setInt(1, account.getAccountId());
            statement.setInt(2, account.getPin());
            statement.setBigDecimal(3, account.getBalance());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException("Could not create account", e);
        }
    }

    @Override
    public Account findAccountById(int accountId){
        String sql = """
            SELECT account_id, pin, balance
            FROM accounts
            WHERE account_id = ?
            """;

        try (Connection connection = ConnectionFactory
                .getConnectionFactory()
                .getConnection();

            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {

                    int id = resultSet.getInt("account_id");
                    int pin = resultSet.getInt("pin");
                    BigDecimal balance = resultSet.getBigDecimal("balance");

                    return new Account(id, pin, balance);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not find account", e);
        }
    }
}
