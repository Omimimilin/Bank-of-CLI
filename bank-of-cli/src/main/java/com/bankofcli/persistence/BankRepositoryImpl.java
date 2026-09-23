package com.bankofcli.persistence;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bankofcli.domain.Account;
import com.bankofcli.domain.Transaction;

public class BankRepositoryImpl implements BankRepository{

    @Override
    public void createAccount(Account account){
        String sql = """
                INSERT INTO accounts (account_id, pin, balance)
                VALUES (?, ?, ?)
                """;

        try (Connection connection = ConnectionFactory.getConnectionFactory().getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {
            
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

    @Override
    public Account deposit(int accountId, BigDecimal amount){
        String sql = """
            UPDATE accounts
            SET balance = balance + ?
            WHERE account_id = ?
            RETURNING account_id, pin, balance
            """;

        try (Connection connection = ConnectionFactory
                .getConnectionFactory()
                .getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, amount);
            statement.setInt(2, accountId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return new Account(
                            resultSet.getInt("account_id"),
                            resultSet.getInt("pin"),
                            resultSet.getBigDecimal("balance")
                    );
                }

                throw new IllegalArgumentException("Account does not exist.");
            }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not deposit", e);
        }
    }

    @Override
    public Account withdraw(int accountId, BigDecimal amount){
        String sql = """
                UPDATE accounts
                SET balance = balance - ?
                WHERE account_id = ?
                RETURNING account_id, pin, balance
                """;
        
        try (Connection connection = ConnectionFactory
            .getConnectionFactory()
            .getConnection();
        PreparedStatement statement = connection.prepareStatement(sql)) {

        statement.setBigDecimal(1, amount);
        statement.setInt(2, accountId);

        try (ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return new Account(
                        resultSet.getInt("account_id"),
                        resultSet.getInt("pin"),
                        resultSet.getBigDecimal("balance")
                );
            }

            throw new IllegalArgumentException("Account does not exist.");
        }

        } catch (SQLException e) {
            throw new IllegalStateException("Could not withdraw", e);
        }
    }

    @Override
    public void transfer(int fromAccountId, int toAccountId, BigDecimal amount) {

        String withdrawSql = """
                UPDATE accounts
                SET balance = balance - ?
                WHERE account_id = ?
                """;

        String depositSql = """
                UPDATE accounts
                SET balance = balance + ?
                WHERE account_id = ?
                """;

        try (Connection connection = ConnectionFactory
            .getConnectionFactory()
            .getConnection();
            PreparedStatement withdrawStatement =
                    connection.prepareStatement(withdrawSql);
            PreparedStatement depositStatement =
                    connection.prepareStatement(depositSql)) {

            connection.setAutoCommit(false);

            try {
                withdrawStatement.setBigDecimal(1, amount);
                withdrawStatement.setInt(2, fromAccountId);

                depositStatement.setBigDecimal(1, amount);
                depositStatement.setInt(2, toAccountId);

                withdrawStatement.executeUpdate();
                depositStatement.executeUpdate();

                connection.commit();

            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not complete transfer", e);
        }
    }

    @Override
    public List<Transaction> findTransactionsByAccountId(int accountId) {

        String sql = """
                SELECT transaction_id,
                    account_id,
                    transaction_type,
                    amount,
                    related_account_id,
                    created_at
                FROM transactions
                WHERE account_id = ?
                ORDER BY created_at DESC
                """;

        List<Transaction> transactions = new ArrayList<>();

        try (Connection connection = ConnectionFactory
                .getConnectionFactory()
                .getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

                statement.setInt(1, accountId);

                try (ResultSet resultSet = statement.executeQuery()) {

                    while (resultSet.next()) {

                        Transaction transaction = new Transaction(
                                resultSet.getInt("transaction_id"),
                                resultSet.getInt("account_id"),
                                resultSet.getString("transaction_type"),
                                resultSet.getBigDecimal("amount"),
                                (Integer) resultSet.getObject("related_account_id"),
                                resultSet.getTimestamp("created_at").toLocalDateTime()
                        );

                        transactions.add(transaction);
                    }
                }

                return transactions;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not retrieve transaction history", e);
        }
    }

    @Override
    public void createTransaction(Transaction transaction) {

        String sql = """
                INSERT INTO transactions
                    (account_id, transaction_type, amount, related_account_id)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = ConnectionFactory
                .getConnectionFactory()
                .getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, transaction.getAccountId());
            statement.setString(2, transaction.getTransactionType());
            statement.setBigDecimal(3, transaction.getAmount());

            if (transaction.getRelatedAccountId() == null) {
                statement.setNull(4, java.sql.Types.INTEGER);
            } else {
                statement.setInt(4, transaction.getRelatedAccountId());
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Could not create transaction", e);
        }
    }
}
