package com.bankofcli.api;
import java.math.BigDecimal;
import java.util.Scanner;

import com.bankofcli.domain.Account;
import com.bankofcli.persistence.BankRepository;
import com.bankofcli.persistence.BankRepositoryImpl;
import com.bankofcli.service.BankService;
import com.bankofcli.service.BankServiceImpl;

/* Handles options, taking user inputs */
public class BankRepl {

    private final Scanner SCAN = new Scanner(System.in);
    private final BankService bankService;

    public BankRepl(){
        BankRepository repository = new BankRepositoryImpl();
        this.bankService = new BankServiceImpl(repository);
    }

    public void run(){
        while(true){
            System.out.println("Welcome to Bank of CLI\n1. Login\n2. Register\n3. Help\n4. Exit\nPlease choose an option using numbers 1-4");
            int command = SCAN.nextInt();
            
            if(command == 4){
                return;
            }

            try{
                handle(command);
            } catch (IllegalArgumentException e){
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handle(int command){
        switch(command) {
            case 1 -> printLogin();
            case 2 -> printRegister();
            case 3 -> printHelp();
            default -> throw new IllegalArgumentException("Invalid option.");
        }
    }

    private void printLogin(){
        System.out.println("Please enter your account ID:");
        int accountId = SCAN.nextInt();

        System.out.println("Please enter your PIN:");
        int pin = SCAN.nextInt();

        Account account = bankService.login(accountId, pin);

        System.out.println("Login successful.");
        System.out.println("Current balance: $" + account.getBalance());
        showAccountMenu(account);
    }

    private void printRegister(){
        System.out.println("Enter a valid account ID");
        int accountId = SCAN.nextInt();

        System.out.println("Enter a PIN:");
        int pin = SCAN.nextInt();

        Account account = new Account(accountId, pin, new BigDecimal("0.00"));
        
        bankService.createAccount(account);
        System.out.println("Account successfully created.");
    }

    private void printHelp(){
        System.out.println("Avaliable Commands:");
        System.out.println("Register - Register a new account");
        System.out.println("Login - Log into your account");
        System.out.println("Exit - Exit the application");
    }

    private void showAccountMenu(Account account){
        while(true){
            System.out.println("Account Menu:");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Transaction History");
            System.out.println("6. Logout");
            int command = SCAN.nextInt();
            try{
                switch (command) {
                    case 1 -> checkBalance(account);
                    case 2 -> account = deposit(account);
                    case 3 -> account = withdraw(account);
                    case 4 -> account = transfer(account);
                    case 5 -> System.out.println("Transaction History selected.");
                    case 6 -> {
                        System.out.println("Logging out...");
                        return;
                    }
                    default -> System.out.println("Invalid option.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void checkBalance(Account account) {
        System.out.println("Current balance: $" + account.getBalance());
    }

    private Account deposit(Account account) {
        System.out.println("Enter deposit amount:");
        BigDecimal amount = SCAN.nextBigDecimal();

        Account updatedAccount = bankService.deposit(account.getAccountId(), amount);

        System.out.println("Deposit successful.");
        System.out.println("Current balance: $" + updatedAccount.getBalance());

        return updatedAccount;
    }

    private Account withdraw(Account account) {
        System.out.println("Enter withdrawal amount:");
        BigDecimal amount = SCAN.nextBigDecimal();

        Account updatedAccount =
                bankService.withdraw(account.getAccountId(), amount);

        System.out.println("Withdrawal successful.");
        System.out.println("Current balance: $" + updatedAccount.getBalance());

        return updatedAccount;
    }

    private Account transfer(Account account) {
        System.out.println("Enter recipient account ID:");
        int toAccountId = SCAN.nextInt();

        System.out.println("Enter transfer amount:");
        BigDecimal amount = SCAN.nextBigDecimal();

        Account updatedAccount = bankService.transfer(
                account.getAccountId(),
                toAccountId,
                amount
        );

        System.out.println("Transfer successful.");
        System.out.println("Current balance: $" + updatedAccount.getBalance());

        return updatedAccount;
    }
}
