
package service;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.lang.reflect.Field;
import java.util.Scanner;
import model.*;
import util.*;
import static util.Validator.*;

record BankInfo(String name, String branch) {}

enum MenuOption {
    OPEN_ACCOUNT,
    DEPOSIT,
    WITHDRAW,
    TRANSFER,
    WORKING_HOURS,
    EXIT
}

public class MiniBank {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        BankInfo bank = new BankInfo("MiniBank", "Main Branch");

        System.out.println("================================");
        System.out.println(bank.name());
        System.out.println(bank.branch());
        System.out.println("================================");

        // Create all account objects first
        Account[] accounts = {
            new SavingsAccount("Krina", 5000, 1000),
            new CurrentAccount("Meera", 2000, 5000),
            new FixedDepositAccount("Bhakti", 10000)
        };
        
System.out.println("\n----- LAB 09: THREAD SYNCHRONIZATION -----");

Account threadAccount = new SavingsAccount("ThreadTest", 0, 0);

int threadCount = 10;
int depositsPerThread = 1000;

Thread[] workers = new Thread[threadCount];

for (int i = 0; i < threadCount; i++) {
    workers[i] = new Thread(
            new AccountWorker(threadAccount, depositsPerThread, 1),
            "Worker-" + (i + 1)
    );
    workers[i].start();
}

for (Thread worker : workers) {
    try {
        worker.join();
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
        System.out.println("Main thread was interrupted.");
        return;
    }
}

System.out.println("Expected balance: 10000");
System.out.println("Actual balance: " + threadAccount.getBalance());

       

System.out.println("\n----- LAB 10: TRANSACTION PROCESSOR -----");

Account poolAccount = new SavingsAccount("PoolTest", 10000, 0);

TransactionProcessor processor = new TransactionProcessor();

for (int i = 1; i <= 10; i++) {
    final int taskId = i;

    processor.submit(() -> {
        try {
            poolAccount.deposit(100);

            System.out.println(
                    Thread.currentThread().getName()
                    + " executed Deposit Task " + taskId
            );
        } catch (Exception e) {
            System.out.println(
                    "Deposit Task " + taskId + " failed: "
                    + e.getMessage()
            );
        }
    });
}

System.out.println("\n----- PRODUCER-CONSUMER -----");

TransactionBuffer buffer = new TransactionBuffer(3);

Thread producer = new Thread(() -> {
    try {
        for (int i = 1; i <= 10; i++) {
            buffer.put(i);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}, "Producer");

Thread consumer = new Thread(() -> {
    try {
        for (int i = 1; i <= 10; i++) {
            int transaction = buffer.take();
            System.out.println(
                    "Consumed transaction: " + transaction
            );
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}, "Consumer");

producer.start();
consumer.start();

try {
    producer.join();
    consumer.join();
} catch (InterruptedException e) {
    Thread.currentThread().interrupt();
    System.out.println("Main thread interrupted.");
}

for (int i = 1; i <= 5; i++) {
    final int taskId = i;

    processor.submit(() -> {
        try {
            poolAccount.withdraw(50);

            System.out.println(
                    Thread.currentThread().getName()
                    + " executed Withdrawal Task " + taskId
            );
        } catch (Exception e) {
            System.out.println(
                    "Withdrawal Task " + taskId + " failed: "
                    + e.getMessage()
            );
        }
    });
}

processor.stop();

System.out.println(
        "Expected balance: 10750"
);
System.out.println(
        "Actual balance: " + poolAccount.getBalance()
);
System.out.println("\n----- ANNOTATION VALIDATION -----");

// Valid account test
String[] validErrors = AnnotationValidator.validate(accounts[0]);
System.out.println("Valid account errors: " + validErrors.length);

// Negative balance test using reflection
try {
    Field balanceField = Account.class.getDeclaredField("balance");
    balanceField.setAccessible(true);
    balanceField.setLong(accounts[0], -100);

    String[] errors = AnnotationValidator.validate(accounts[0]);

    for (String error : errors) {
        System.out.println(error);
    }

    // Restore original balance
    balanceField.setLong(accounts[0], 5000);

} catch (NoSuchFieldException | IllegalAccessException e) {
    System.out.println("Validation test failed.");
}
// Lab 08: Exception Handling
System.out.println("\n----- EXCEPTION HANDLING -----");

// Test insufficient funds
Account testAccount = new SavingsAccount("Test", 1000, 0);

try {
    testAccount.withdraw(5000);
} catch (InsufficientFundsException e) {
    System.out.println("Withdrawal failed: " + e.getMessage());
    System.out.println("Shortfall: " + e.getShortfall());
} catch (InvalidAmountException e) {
    System.out.println("Invalid amount: " + e.getMessage());
} finally {
    System.out.println("Withdrawal attempt completed.");
}

// Test invalid deposit
try {
    testAccount.deposit(0);
} catch (InvalidAmountException e) {
    System.out.println("Deposit failed: " + e.getMessage());
} finally {
    System.out.println("Deposit attempt completed.");
}

// Test transfer
try {
    accounts[0].transfer(accounts[1], 1000);
    System.out.println("Transfer successful.");
} catch (BankException e) {
    System.out.println("Transfer failed: " + e.getMessage());
} finally {
    System.out.println("Transfer test completed.");
}

// Test try-with-resources
System.out.println("\n----- TRY WITH RESOURCES -----");

try (BankResource resource = new BankResource()) {
    System.out.println("Resource opened.");
    throw new IllegalStateException("Demo exception inside resource block.");
} catch (IllegalStateException e) {
    System.out.println("Original error: " + e.getMessage());
}
        


        // Mobile validation
        System.out.println("\n----- MOBILE VALIDATION -----");
        System.out.println("9876543210: " + isValidMobile("9876543210"));
        System.out.println("12345: " + isValidMobile("12345"));

        // Email validation
        System.out.println("\n----- EMAIL VALIDATION -----");
        System.out.println("krina@example.com: "
                + isValidEmail("krina@example.com"));
        System.out.println("krina@wrong: "
                + isValidEmail("krina@wrong"));

        // PAN validation
        System.out.println("\n----- PAN VALIDATION -----");
        System.out.println("ABCDE1234F: " + isValidPan("ABCDE1234F"));
        System.out.println("12345: " + isValidPan("12345"));

        // IFSC validation
        System.out.println("\n----- IFSC VALIDATION -----");
        System.out.println("SBIN0001234: " + isValidIfsc("SBIN0001234"));
        System.out.println("ABC123: " + isValidIfsc("ABC123"));

        // Amount validation
        System.out.println("\n----- AMOUNT VALIDATION -----");
        System.out.println("500: " + isValidAmount("500"));
        System.out.println("-500: " + isValidAmount("-500"));

        // Command parser
        System.out.println("\n----- COMMAND PARSER -----");

        Command command = CommandParser.parse("DEPOSIT AC0001 500");

        System.out.println("type=" + command.type()
                + ", accountNumber=" + command.accountNumber()
                + ", amount=" + command.amount());

        // Account types and polymorphism
        System.out.println("\n----- ACCOUNT TYPES -----");

        for (Account account : accounts) {
            System.out.println(account.getOwnerName()
                    + " | Interest Rate: "
                    + account.interestRate() + "%");

            if (account instanceof SavingsAccount savings) {
                System.out.println("Minimum Balance: "
                        + savings.getMinBalance());
            }
        }

        // WithdrawRule using an anonymous class
        System.out.println("\n----- WITHDRAW RULE -----");

        WithdrawRule rule1 = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };

        // WithdrawRule using a lambda expression
        WithdrawRule rule2 = (account, amount) ->
                account.canWithdraw(amount);

        System.out.println("Anonymous class: "
                + rule1.allow(accounts[0], 1000));

        System.out.println("Lambda: "
                + rule2.allow(accounts[0], 1000));

        // Account statement
        System.out.println("\n----- ACCOUNT STATEMENT -----");
        System.out.println(StatementFormatter.buildStatement(accounts[0]));

        // Menu
        while (true) {
            System.out.println("\n----- MENU -----");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Working Hours");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input! Please enter a number.");
                sc.next();
                continue;
            }

            int choice = sc.nextInt();

            if (choice < 1 || choice > 6) {
                System.out.println("Invalid choice! Please try again.");
                continue;
            }

            MenuOption option = switch (choice) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.WORKING_HOURS;
                default -> MenuOption.EXIT;
            };

            switch (option) {
                case OPEN_ACCOUNT ->
                    System.out.println(
                            "Open Account - To be implemented in a later lab.");

                case DEPOSIT ->
                    System.out.println(
                            "Deposit - To be implemented in a later lab.");

                case WITHDRAW ->
                    System.out.println(
                            "Withdraw - To be implemented in a later lab.");

                case TRANSFER ->
                    System.out.println(
                            "Transfer - To be implemented in a later lab.");

                case WORKING_HOURS ->
                    System.out.println("Working Hours: Monday to Friday, "
                            + "9:00 AM to 5:00 PM");

                case EXIT -> {
                    System.out.println("Thank you for using MiniBank.");
                    System.out.println("Goodbye!");
                    sc.close();
                    return;
                }
            }
        }
    }
}
