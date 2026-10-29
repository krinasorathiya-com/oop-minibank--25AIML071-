
package src.test.java.service;

import model.Account;
import model.SavingsAccount;

public class DeadlockDemo {

    public static void main(String[] args) {

        Account accountA =
                new SavingsAccount("Krina", 10000, 0);
        Account accountB =
                new SavingsAccount("Meera", 10000, 0);

        System.out.println("Account A: "
                + accountA.getAccountNumber());
        System.out.println("Account B: "
                + accountB.getAccountNumber());

        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    Account.transferWithOrderedLocks(
                            accountA, accountB, 1);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }, "Transfer-A-to-B");

        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    Account.transferWithOrderedLocks(
                            accountB, accountA, 1);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }, "Transfer-B-to-A");

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread interrupted.");
        }

        System.out.println("\nBoth threads completed.");
        System.out.println("Account A balance: "
                + accountA.getBalance());
        System.out.println("Account B balance: "
                + accountB.getBalance());
        System.out.println("Total balance: "
                + (accountA.getBalance()
                + accountB.getBalance()));
    }
}