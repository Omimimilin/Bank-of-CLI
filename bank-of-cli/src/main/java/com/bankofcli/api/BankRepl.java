package com.bankofcli.api;
import java.sql.Connection;
import java.util.Scanner;

import com.bankofcli.persistence.ConnectionFactory;

/* Handles options, taking user inputs */
public class BankRepl {

    private final Scanner SCAN = new Scanner(System.in);

    public BankRepl(){

    }

    public void run(){
        ConnectionFactory factory =
                ConnectionFactory.getConnectionFactory();

        try (Connection connection = factory.getConnection()) {

            System.out.println("Successfully connected to PostgreSQL!");

        } catch (Exception e) {

            e.printStackTrace();

        }
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
        }
    }

    private void printLogin(){
        System.out.println("Please enter your account id: ");
        try {
            
        } catch (Exception e) {

        }
        
    }

    private void printRegister(){

    }

    private void printHelp(){
        System.out.println("Avaliable Commands:");
        System.out.println("Exit - Exit the application");
    }
}
