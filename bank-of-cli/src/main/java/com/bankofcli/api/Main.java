package com.bankofcli.api;

/**
 * Hello world!
 *
 */
public class Main
{
    public static void main( String[] args )
    {
        BankDAO bankDAO = new BankDAOImpl();
        new BankRepl().run();
    }
}
