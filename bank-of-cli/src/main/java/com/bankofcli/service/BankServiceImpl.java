package com.bankofcli.service;

import com.bankofcli.domain.Account;
import com.bankofcli.persistence.BankDAO;

import java.util.List;

public class BankServiceImpl implements BankService {
    private final BankDAO bankDAO;

    public BankServiceImpl(BankDAO bankDAO){
        this.bankDAO = bankDAO;
    }

    @Override
    public void addAccount(Account account){
        if(BankDAO.getAccountById(account.getId()) != null){
            throw new IllegalArgumentException("Account Id already exist.");
        }

        bankDAO.addAccount(account);
    }
    
    @Override
    public List<Account> findAllAccounts(){
        return bankDAO.getAllAccounts();
    } 
}
