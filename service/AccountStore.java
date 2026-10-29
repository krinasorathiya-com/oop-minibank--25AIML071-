
package src.test.java.service;

import model.Account;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AccountStore {

    private final Map<String, Account> accounts = new HashMap<>();
    private final ArrayList<String> transactionHistory =
            new ArrayList<>();

    public void add(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    public Account get(String accountNumber) {
        return accounts.get(accountNumber);
    }

    public ArrayList<Account> getAll() {
        return new ArrayList<>(accounts.values());
    }

    public void addTransaction(String transaction) {
        transactionHistory.add(transaction);
    }

    public ArrayList<String> getTransactionHistory() {
        return new ArrayList<>(transactionHistory);
    }

    public ArrayList<Account> sortedByBalance() {
        ArrayList<Account> sorted =
                new ArrayList<>(accounts.values());

        sorted.sort(
                Comparator.comparingDouble(
                        (Account account) -> account.getBalance()
                ).reversed()
        );

        return sorted;
    }

    public ArrayList<Account> getAccountsAbove(double amount) {
        ArrayList<Account> result = new ArrayList<>();

        for (Account account : accounts.values()) {
            if (account.getBalance() > amount) {
                result.add(account);
            }
        }

        return result;
    }

    public Map<String, Account> getAccountsMap() {
        return new HashMap<>(accounts);
    }
}
