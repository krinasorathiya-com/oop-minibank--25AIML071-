
package service;

import exception.BankException;
import exception.InsufficientFundsException;
import exception.InvalidAmountException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;
import model.*;
import util.*;
import service.Repository;
import static util.Validator.*;
import model.Customer;

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

        // Initial accounts: Labs 1-11
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
                sc.close();
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
                    System.out.println("Consumed transaction: " + transaction);
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
            processor.stop();
            sc.close();
            return;
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

        System.out.println("Expected balance: 10750");
        System.out.println("Actual balance: " + poolAccount.getBalance());
        

        System.out.println("\n----- ANNOTATION VALIDATION -----");

        String[] validErrors = AnnotationValidator.validate(accounts[0]);
        System.out.println("Valid account errors: " + validErrors.length);

        try {
            Field balanceField = Account.class.getDeclaredField("balance");
            balanceField.setAccessible(true);
            balanceField.setLong(accounts[0], -100);

            String[] errors = AnnotationValidator.validate(accounts[0]);

            for (String error : errors) {
                System.out.println(error);
            }

            balanceField.setLong(accounts[0], 5000);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("Validation test failed.");
        }

        System.out.println("\n----- EXCEPTION HANDLING -----");

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

        try {
            testAccount.deposit(0);
        } catch (InvalidAmountException e) {
            System.out.println("Deposit failed: " + e.getMessage());
        } finally {
            System.out.println("Deposit attempt completed.");
        }

        try {
            accounts[0].transfer(accounts[1], 1000);
            System.out.println("Transfer successful.");
        } catch (BankException e) {
            System.out.println("Transfer failed: " + e.getMessage());
        } finally {
            System.out.println("Transfer test completed.");
        }

        System.out.println("\n----- TRY WITH RESOURCES -----");

        try (BankResource resource = new BankResource()) {
            System.out.println("Resource opened.");
            throw new IllegalStateException(
                    "Demo exception inside resource block.");
        } catch (IllegalStateException e) {
            System.out.println("Original error: " + e.getMessage());
        }

        System.out.println("\n----- MOBILE VALIDATION -----");
        System.out.println("9876543210: " + isValidMobile("9876543210"));
        System.out.println("12345: " + isValidMobile("12345"));

        System.out.println("\n----- EMAIL VALIDATION -----");
        System.out.println("krina@example.com: "
                + isValidEmail("krina@example.com"));
        System.out.println("krina@wrong: " + isValidEmail("krina@wrong"));

        System.out.println("\n----- PAN VALIDATION -----");
        System.out.println("ABCDE1234F: " + isValidPan("ABCDE1234F"));
        System.out.println("12345: " + isValidPan("12345"));

        System.out.println("\n----- IFSC VALIDATION -----");
        System.out.println("SBIN0001234: " + isValidIfsc("SBIN0001234"));
        System.out.println("ABC123: " + isValidIfsc("ABC123"));

        System.out.println("\n----- AMOUNT VALIDATION -----");
        System.out.println("500: " + isValidAmount("500"));
        System.out.println("-500: " + isValidAmount("-500"));

        System.out.println("\n----- COMMAND PARSER -----");

        Command command = CommandParser.parse("DEPOSIT AC0001 500");

        System.out.println("type=" + command.type()
                + ", accountNumber=" + command.accountNumber()
                + ", amount=" + command.amount());

        System.out.println("\n----- ACCOUNT TYPES -----");

        for (Account account : accounts) {
            System.out.println(account.getOwnerName()
                    + " | Interest Rate: " + account.interestRate() + "%");

            if (account instanceof SavingsAccount savings) {
                System.out.println(
                        "Minimum Balance: " + savings.getMinBalance());
            }
        }

        System.out.println("\n----- WITHDRAW RULE -----");

        WithdrawRule rule1 = new WithdrawRule() {
            @Override
            public boolean allow(Account account, long amount) {
                return account.canWithdraw(amount);
            }
        };

        WithdrawRule rule2 = (account, amount) ->
                account.canWithdraw(amount);

        System.out.println("Anonymous class: "
                + rule1.allow(accounts[0], 1000));
        System.out.println("Lambda: "
                + rule2.allow(accounts[0], 1000));

        System.out.println("\n----- ACCOUNT STATEMENT -----");
        System.out.println(StatementFormatter.buildStatement(accounts[0]));

        // Lab 11: File Handling and NIO
        System.out.println("\n----- LAB 11: FILE HANDLING AND NIO -----");

        Path dataDirectory = Path.of("data");
        Path accountsFile = dataDirectory.resolve("accounts.dat");
        Path logDirectory = dataDirectory.resolve("logs");
        Path logFile = logDirectory.resolve("transactions.log");
        Path reportFile = dataDirectory.resolve("report.txt");

        try {
            StatePersister.save(accounts, accountsFile);
            System.out.println("Accounts saved successfully.");

            Account[] loadedAccounts = StatePersister.load(accountsFile);

            System.out.println("\n----- TRANSIENT FIELD TEST -----");
            System.out.println("Original session note: "
                    + accounts[0].getSessionNote());
            System.out.println("Reloaded session note: "
                    + loadedAccounts[0].getSessionNote());

            System.out.println("\n----- RELOADED ACCOUNTS -----");

            for (Account account : loadedAccounts) {
                System.out.println(account);
            }

            TransactionLog.append(logFile, "DEPOSIT AC0001 500");
            TransactionLog.append(logFile, "WITHDRAW AC0002 200");

            System.out.println("\nTransactions logged successfully.");
            System.out.println("\n----- TRANSACTION REPORT -----");

            ReportGenerator.generate(logDirectory, reportFile);
            System.out.println("Report saved to: " + reportFile);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Lab 11 error: " + e.getMessage());
            e.printStackTrace();
        }
System.out.println("\n----- LAB 12: COLLECTIONS FRAMEWORK -----");

AccountStore store = new AccountStore();

for (Account account : accounts) {
    store.add(account);
}

store.addTransaction("Deposit: AC0001, Amount: 500");
store.addTransaction("Withdrawal: AC0002, Amount: 200");

System.out.println("\n----- LOOKUP BY ACCOUNT NUMBER -----");

Account found = store.get("AC0002");

if (found != null) {
    System.out.println(found);
} else {
    System.out.println("Account not found");
}

System.out.println("\n----- ACCOUNTS SORTED BY BALANCE -----");

ArrayList<Account> sortedAccounts = store.sortedByBalance();

for (Account account : sortedAccounts) {
    System.out.println(account);
}

System.out.println("\n----- TRANSACTION HISTORY -----");

for (String transaction : store.getTransactionHistory()) {
    System.out.println(transaction);
}

System.out.println("\n----- LAB 13: GENERIC REPOSITORY -----");

Repository<Account> accountRepository = new Repository<>();

Account account1 = new SavingsAccount("Krina", 5000, 1000);
Account account2 = new SavingsAccount("Meera", 2000, 500);
Account account3 = new SavingsAccount("Bhakti", 9000, 1000);

accountRepository.saveAll(account1, account2, account3);

System.out.println("\n----- FIND ACCOUNT BY ID -----");

String searchId = account2.getAccountNumber();
Account foundAccount = accountRepository.findById(searchId);

if (foundAccount != null) {
    System.out.println(foundAccount);
} else {
    System.out.println("Account not found");
}

System.out.println("\n----- ALL SAVED ACCOUNTS -----");

for (Account account : accountRepository.findAll()) {
    System.out.println(account);
}

System.out.println("\nTotal accounts: " + accountRepository.count());

System.out.println("\n----- BOUNDED GENERIC METHOD -----");

Integer[] values = {10, 20, 30};
System.out.println("Average: " + Repository.average(values));
System.out.println("\n----- CUSTOMER REPOSITORY TEST -----");

Repository<Customer> customerRepository = new Repository<>();

Customer customer1 =
        new Customer("Krina", "krina@example.com", "9876543210");

Customer customer2 =
        new Customer("Meera", "meera@example.com", "9876543211");

Customer customer3 =
        new Customer("Bhakti", "bhakti@example.com", "9876543212");

customerRepository.saveAll(customer1, customer2, customer3);

Customer foundCustomer =
        customerRepository.findById(customer2.getCustomerId());

System.out.println("Found customer: " + foundCustomer.getName());
System.out.println("Customer ID: " + foundCustomer.getCustomerId());

System.out.println("\nAll customers:");

for (Customer customer : customerRepository.findAll()) {
    System.out.println(
            customer.getCustomerId() + " | " +
            customer.getName() + " | " +
            customer.getEmail()
    );
}

System.out.println("Total customers: " + customerRepository.count());

        // Interactive menu account list
        ArrayList<Account> menuAccounts =
                new ArrayList<>(java.util.Arrays.asList(accounts));

        // Menu: all choices are functional
        while (true) {
            System.out.println("\n----- MENU -----");
            System.out.println("1. Open Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. Working Hours");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");

            String input = sc.nextLine().trim();
            int choice;

            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
                continue;
            }

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

            try {
                switch (option) {
                    case OPEN_ACCOUNT -> {
                        System.out.println("\nChoose account type:");
                        System.out.println("1. Savings Account");
                        System.out.println("2. Current Account");
                        System.out.println("3. Fixed Deposit Account");
                        System.out.print("Enter account type: ");

                        int type = Integer.parseInt(sc.nextLine().trim());

                        if (type < 1 || type > 3) {
                            System.out.println("Invalid account type.");
                            break;
                        }

                        System.out.print("Enter owner name: ");
                        String owner = sc.nextLine().trim();

                        if (owner.isEmpty()) {
                            System.out.println("Owner name cannot be empty.");
                            break;
                        }

                        System.out.print("Enter opening balance: ");
                        long openingBalance =
                                Long.parseLong(sc.nextLine().trim());

                        if (openingBalance < 0) {
                            System.out.println(
                                    "Opening balance cannot be negative.");
                            break;
                        }

                        Account newAccount;

                        if (type == 1) {
                            System.out.print("Enter minimum balance: ");
                            long minimum =
                                    Long.parseLong(sc.nextLine().trim());

                            if (minimum < 0) {
                                System.out.println(
                                        "Minimum balance cannot be negative.");
                                break;
                            }

                            newAccount = new SavingsAccount(
                                    owner, openingBalance, minimum);

                        } else if (type == 2) {
                            System.out.print("Enter overdraft limit: ");
                            long overdraft =
                                    Long.parseLong(sc.nextLine().trim());

                            if (overdraft < 0) {
                                System.out.println(
                                        "Overdraft limit cannot be negative.");
                                break;
                            }

                            newAccount = new CurrentAccount(
                                    owner, openingBalance, overdraft);

                        } else {
                            newAccount = new FixedDepositAccount(
                                    owner, openingBalance);
                        }

                        menuAccounts.add(newAccount);

                        System.out.println("Account opened successfully!");
                        System.out.println(newAccount);
                    }

                    case DEPOSIT -> {
                        System.out.print("Enter account number: ");
                        String accountNumber = sc.nextLine().trim();

                        Account account =
                                findAccount(menuAccounts, accountNumber);

                        if (account == null) {
                            System.out.println("Account not found.");
                            break;
                        }

                        System.out.print("Enter deposit amount: ");
                        long amount =
                                Long.parseLong(sc.nextLine().trim());

                        account.deposit(amount);

                        TransactionLog.append(
                                logFile,
                                "DEPOSIT " + account.getAccountNumber()
                                        + " " + amount);

                        System.out.println("Deposit successful.");
                        System.out.println("New balance: "
                                + account.getBalance());
                    }

                    case WITHDRAW -> {
                        System.out.print("Enter account number: ");
                        String accountNumber = sc.nextLine().trim();

                        Account account =
                                findAccount(menuAccounts, accountNumber);

                        if (account == null) {
                            System.out.println("Account not found.");
                            break;
                        }

                        System.out.print("Enter withdrawal amount: ");
                        long amount =
                                Long.parseLong(sc.nextLine().trim());

                        account.withdraw(amount);

                        TransactionLog.append(
                                logFile,
                                "WITHDRAW " + account.getAccountNumber()
                                        + " " + amount);

                        System.out.println("Withdrawal successful.");
                        System.out.println("New balance: "
                                + account.getBalance());
                    }

                    case TRANSFER -> {
                        System.out.print("Enter source account number: ");
                        String sourceNumber = sc.nextLine().trim();

                        Account source =
                                findAccount(menuAccounts, sourceNumber);

                        if (source == null) {
                            System.out.println("Source account not found.");
                            break;
                        }

                        System.out.print("Enter destination account number: ");
                        String destinationNumber = sc.nextLine().trim();

                        Account destination =
                                findAccount(menuAccounts, destinationNumber);

                        if (destination == null) {
                            System.out.println(
                                    "Destination account not found.");
                            break;
                        }

                        System.out.print("Enter transfer amount: ");
                        long amount =
                                Long.parseLong(sc.nextLine().trim());

                        Account.transfer(source, destination, amount);

                        TransactionLog.append(
                                logFile,
                                "WITHDRAW " + source.getAccountNumber()
                                        + " " + amount);

                        TransactionLog.append(
                                logFile,
                                "DEPOSIT " + destination.getAccountNumber()
                                        + " " + amount);

                        System.out.println("Transfer successful.");
                        System.out.println("Source balance: "
                                + source.getBalance());
                        System.out.println("Destination balance: "
                                + destination.getBalance());
                    }

                    case WORKING_HOURS ->
                        System.out.println(
                                "Working Hours: Monday to Friday, "
                                + "9:00 AM to 5:00 PM");

                    case EXIT -> {
                        try {
                            StatePersister.save(
                                    menuAccounts.toArray(new Account[0]),
                                    accountsFile);

                            ReportGenerator.generate(
                                    logDirectory, reportFile);

                            System.out.println(
                                    "Accounts and report saved successfully.");
                        } catch (IOException e) {
                            System.out.println(
                                    "Could not save final data: "
                                            + e.getMessage());
                        }

                        System.out.println(
                                "Thank you for using MiniBank.");
                        System.out.println("Goodbye!");
                        sc.close();
                        return;
                    }
                }

            } catch (NumberFormatException e) {
                System.out.println(
                        "Invalid number! Please enter a valid number.");

            } catch (InsufficientFundsException | InvalidAmountException e) {
                System.out.println("Transaction failed: " + e.getMessage());

            } catch (BankException e) {
                System.out.println("Operation failed: " + e.getMessage());

            } catch (IOException e) {
                System.out.println("File operation failed: " + e.getMessage());
            }
        }
    }

    private static Account findAccount(
            ArrayList<Account> accounts, String accountNumber) {

        for (Account account : accounts) {
            if (account.getAccountNumber()
                    .equalsIgnoreCase(accountNumber)) {
                return account;
            }
        }

        return null;
    }
}
