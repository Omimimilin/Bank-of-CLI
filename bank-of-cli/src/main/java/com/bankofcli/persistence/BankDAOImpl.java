package com.bankofcli.persistence;

import java.io.*;
import java.util.*;

import com.bankofcli.domain.Account;

public class BankDAOImpl implements BankDAO {
    private final String filename;

    public BankDAOImpl(String filename){
        this.filename = filename;
    }

    @Override
    public void addAccount(Account account){
        List<Account> accounts = getAllAccounts();
        accounts.add(account);

        saveAllAccounts(accounts);
    }

    @Override
    public List<Account> getAllAccounts(){
        List<Account> accounts = new ArrayList<>();
        File file = new File(filename);
        if(!file.exists()){
            return accounts;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            String line;
            while((line = reader.readLine()) != null){
                if(!line.trim().isEmpty()){
                    accounts.add(Account.fromFileString(line));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
        return accounts;
    }

    private void saveAllAccounts(List<Account> accounts){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Account a : accounts) {
                writer.write(a.toString());
                writer.newLine();
            }
        }
        catch(IOException e){
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    /*Account getAccountById(int id);
    void updateAccount(Account account);
    void deleteAccount(int id); */
}