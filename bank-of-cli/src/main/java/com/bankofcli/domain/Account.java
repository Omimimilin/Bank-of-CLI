package com.bankofcli.domain;

import java.math.BigDecimal;

public class Account {
    
    private int accountId;
    private int pin;
    private BigDecimal balance;

    public Account(int accountId, int pin, BigDecimal balance){
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

    public BigDecimal getBalance(){
        return balance;
    }
}
