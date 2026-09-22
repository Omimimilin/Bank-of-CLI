package com.bankofcli.api;
import java.util.Scanner;

import com.bankofcli.service.BankServiceImpl;
import com.bankofcli.service.BankService;
import com.bankofcli.persistence.BankRepository;
import com.bankofcli.persistence.BankRepositoryImpl;
import com.bankofcli.domain.Account;
import java.math.BigDecimal;

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
}
