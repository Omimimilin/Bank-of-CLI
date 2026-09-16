package com.bankofcli.domain;

public class Account {
    
    private int accountId;
    private int pin;
    private double balance;

    public Account(int accountId, int pin, double balance){
        this.accountId = accountId;
        this.pin = pin;
        this.balance = balance;
    }

    public int getId(){
        return accountId;
    }

    public int getPin(){
        return pin;
    }

    public double getBalance(){
        return balance;
    }

    public String toString(){
        return accountId + "," + pin + "," + ", $" + balance;
    }

    public static Account fromFileString(String line){
        String[] parts = line.split(",");
        return new Account(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Double.parseDouble(parts[2]));
    }
}
