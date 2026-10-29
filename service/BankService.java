package service;

import java.util.ArrayList;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InvalidAmountException;
import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;

public class BankService {

    private final Repository<Account> repository;

    public BankService(Repository<Account> repository) {
        if (repository == null) {
            throw new IllegalArgumentException(
                    "Repository cannot be null."
            );
        }

        this.repository = repository;
    }

    // Open a new account
    public Account openAccount(
            String ownerName,
            String accountType,
            long openingBalance) throws BankException {

        if (ownerName == null || ownerName.trim().isEmpty()) {
            throw new BankException("Owner name cannot be empty.");
        }

        if (openingBalance < 0) {
            throw new InvalidAmountException(
                    "Opening balance cannot be negative."
            );
        }

        if (accountType == null) {
            throw new BankException("Account type is required.");
        }

        Account account;

        switch (accountType.trim().toUpperCase()) {
            case "SAVINGS" -> account = new SavingsAccount(
                        ownerName, openingBalance, 1000
                );

            case "CURRENT" -> account = new CurrentAccount(
                        ownerName, openingBalance, 5000
                );

            case "FIXED_DEPOSIT", "FIXED DEPOSIT", "FD" -> account = new FixedDepositAccount(
                    ownerName, openingBalance
            );
            default -> throw new BankException(
                    "Invalid account type. Use SAVINGS, CURRENT or FD."
            );
        }

        repository.save(account);
        return account;
    }

    // Find an account by account number
    public Account findAccount(String accountNumber)
            throws AccountNotFoundException {

        Account account = repository.findById(accountNumber);

        if (account == null) {
            throw new AccountNotFoundException(
                    "Account not found: " + accountNumber
            );
        }

        return account;
    }

    // Deposit money
    public void deposit(String accountNumber, long amount)
            throws BankException {

        Account account = findAccount(accountNumber);
        account.deposit(amount);
    }

    // Withdraw money
    public void withdraw(String accountNumber, long amount)
            throws BankException {

        Account account = findAccount(accountNumber);
        account.withdraw(amount);
    }

    // Transfer money between two accounts
    public void transfer(
            String fromAccountNumber,
            String toAccountNumber,
            long amount) throws BankException {

        Account from = findAccount(fromAccountNumber);
        Account to = findAccount(toAccountNumber);

        Account.transfer(from, to, amount);
    }

    // Generate a statement for one account
    public String statement(String accountNumber)
            throws AccountNotFoundException {

        Account account = findAccount(accountNumber);
        return account.getStatement();
    }

    // List all accounts
    public ArrayList<Account> listAccounts() {
        return repository.findAll();
    }

    // Search accounts by owner name
    public ArrayList<Account> searchByOwnerName(String ownerName) {
        ArrayList<Account> matches = new ArrayList<>();

        if (ownerName == null || ownerName.trim().isEmpty()) {
            return matches;
        }

        for (Account account : repository.findAll()) {
            if (account.getOwnerName().equalsIgnoreCase(
                    ownerName.trim())) {
                matches.add(account);
            }
        }

        return matches;
    }

    // Count all accounts
    public int countAccounts() {
        return repository.count();
    }
}