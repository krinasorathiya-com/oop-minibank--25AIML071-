
package model;

import exception.AccountNotFoundException;
import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.io.Serializable;
import model.annotation.Id;
import model.annotation.Positive;

public abstract class Account
        implements Transactable, InterestBearing, Serializable {

    @Id
    private final String accountNumber;

    private String ownerName;

    @Positive
    private long balance;

    private boolean active;
    private transient String sessionNote = "Active session";

    private static long accountCounter = 0;
    private static final long serialVersionUID = 1L;

    public Account(String ownerName, long openingBalance) {
        accountCounter++;

        this.accountNumber = String.format("AC%04d", accountCounter);
        this.ownerName = ownerName;


        if (openingBalance >= 0) {
            this.balance = openingBalance;
        } else {
            this.balance = 0;
        }

        this.active = true;
    }

    public Account(String ownerName) {
        this(ownerName, 0);
    }

    public abstract double interestRate();

    public abstract boolean canWithdraw(long amount);

    public double monthlyInterest() {
        return balance * interestRate() / 100.0 / 12.0;
    }

    public synchronized void deposit(long amount) throws InvalidAmountException {
        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Deposit amount must be positive."
            );
        }

        balance += amount;
    }

    public synchronized boolean withdraw(long amount)
            throws InsufficientFundsException, InvalidAmountException {

        if (amount <= 0) {
            throw new InvalidAmountException(
                    "Withdrawal amount must be positive."
            );
        }

        if (!canWithdraw(amount)) {
            long shortfall = amount - balance;

            if (shortfall < 0) {
                shortfall = 0;
            }

            throw new InsufficientFundsException(shortfall);
        }

        balance -= amount;
        return true;
    }

    public void transfer(Account to, long amount) throws BankException {
        try {
            if (to == null) {
                throw new AccountNotFoundException(
                        "Destination account not found."
                );
            }

            withdraw(amount);
            to.deposit(amount);

        } catch (BankException e) {
            throw e;

        } finally {
            System.out.println("Transfer operation completed.");
        }
    }

    public static boolean transfer(Account from, Account to, long amount)
            throws BankException {

        if (from == null || to == null) {
            throw new AccountNotFoundException(
                    "Source or destination account not found."
            );
        }

        if (from == to) {
            throw new BankException(
                    "Cannot transfer to the same account."
            );
        }

        from.transfer(to, amount);
        return true;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
    
    public static void transferWithOrderedLocks(
            Account first, Account second, long amount)
            throws BankException {

        if (first == null || second == null) {
            throw new AccountNotFoundException(
                    "Source or destination account not found."
            );
        }

        if (first == second) {
            throw new BankException(
                    "Cannot transfer to the same account."
            );
        }

        Account lower;
        Account higher;

        if (first.getAccountNumber().compareTo(
                second.getAccountNumber()) < 0) {
            lower = first;
            higher = second;
        } else {
            lower = second;
            higher = first;
        }

        synchronized (lower) {
            synchronized (higher) {
                first.withdraw(amount);
                second.deposit(amount);
            }
        }

        System.out.println(
                "Ordered transfer completed: "
                + first.getAccountNumber() + " -> "
                + second.getAccountNumber()
        );
    }


    public String getOwnerName() {
        return ownerName;
    }

    public long getBalance() {
        return balance;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public String toString() {
        return accountNumber + " | " + ownerName + " | "
                + balance + " | Active: " + active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Account)) {
            return false;
        }

        Account other = (Account) o;

        return accountNumber.equals(other.accountNumber);
    }

    @Override
    public int hashCode() {
        return accountNumber.hashCode();
    }

    public String getStatement() {
        return "----- ACCOUNT STATEMENT -----\n"
                + "Account Number: " + accountNumber + "\n"
                + "Owner Name: " + ownerName + "\n"
                + "Balance: " + balance + "\n"
                + "Active: " + active + "\n"
                + "-----------------------------";
    }
}
