# Bank of CLI

A Java command-line banking application built with Java, Maven, PostgreSQL, and JDBC.

## Features

* Register and login with Account ID and PIN
* Check account balance
* Deposit and withdraw money
* Transfer money between accounts
* Prevent overdrafts and invalid transfers
* View transaction history
* Log successful and failed operations
* Atomic database transfers

## Tech Stack

* Java 17
* Maven
* PostgreSQL
* JDBC
* JUnit 5
* Mockito
* SLF4J / Logback
* Docker

## Architecture

```text
API
 ↓
Service
 ↓
Repository
 ↓
PostgreSQL
```

* **API:** Handles the CLI and user input
* **Service:** Handles banking rules and validation
* **Repository:** Handles database operations
* **Domain:** Contains `Account` and `Transaction` objects

## Setup

Start PostgreSQL with Docker:

```bash
docker run -d --name project0-postgres -p 5432:5432 -e POSTGRES_USER=project0 -e POSTGRES_PASSWORD=password -e POSTGRES_DB=project0 postgres:17
```

Configure `src/main/resources/db.properties`:

```properties
DB_URL=jdbc:postgresql://localhost:5432/project0
DB_USER=project0
DB_PASSWORD=password
```

Then run:

```bash
mvn compile
mvn test
```

To start the application:

```bash
mvn exec:java
```

## Testing

The project contains **26 JUnit tests**:

* 14 Repository tests
* 12 Service tests
* Positive and negative tests for every Service and Repository method

Run:

```bash
mvn test
```

Expected result:

```text
Tests run: 26, Failures: 0, Errors: 0
BUILD SUCCESS
```

## Logging

Application logs are stored in:

```text
logs/bank-of-cli.log
```

Logs include successful operations and errors such as invalid deposits, insufficient funds, failed logins, and invalid transfers.

## ER Diagram

                 ┌──────────────────┐
                 │     ACCOUNTS     |
                 │──────────────────│
                 │ PK account_id    │
                 │    pin           │
                 │    balance       │
                 └───────┬──────────┘
                         │
              ┌──────────┴──────────┐ 
              │                     │
           1:N│                  1:N│
              │                     │
              ▼                     ▼
       ┌────────────────────────────────┐
       │          TRANSACTIONS          │
       │────────────────────────────────│
       │ PK transaction_id              │
       │ FK account_id                  │
       │    transaction_type            │
       │    amount                      │
       │ FK related_account_id          │
       │    created_at                  │
       └────────────────────────────────┘

## Author

Naomi Lin
