-- CREATE TABLE accounts(
--     account_id INTEGER PRIMARY KEY,
--     pin INTEGER NOT NULL,
--     balance DECIMAL(12,2) NOT NULL DEFAULT 0.00
-- );

-- CREATE TABLE transactions(
--     transaction_id SERIAL PRIMARY KEY,
--     account_id INTEGER NOT NULL,
--     transaction_type VARCHAR(20) NOT NULL,
--     amount DECIMAL(12, 2) NOT NULL,
--     related_account_id INTEGER,
--     created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

--     FOREIGN KEY (account_id) REFERENCES accounts(account_id),
--     FOREIGN KEY (related_account_id) REFERENCES accounts(account_id)
-- );

DELETE FROM transactions
WHERE account_id = 1001;

DELETE FROM accounts
WHERE account_id = 1001;