
package service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

import exception.BankException;
import model.Account;
import model.CurrentAccount;
import model.FixedDepositAccount;
import model.SavingsAccount;

public class MiniBank {

    private static final Path DATA_DIRECTORY = Path.of("data");
    private static final Path ACCOUNTS_FILE =
            DATA_DIRECTORY.resolve("accounts.dat");
    private static final Path LOG_DIRECTORY =
            DATA_DIRECTORY.resolve("logs");
    private static final Path LOG_FILE =
            LOG_DIRECTORY.resolve("transactions.log");
    private static final Path REPORT_FILE =
            DATA_DIRECTORY.resolve("report.txt");

    public static void main(String[] args) {

        try (Scanner sc = new Scanner(System.in)) {

            System.out.println("================================");
            System.out.println("           MINIBANK");
            System.out.println("          Main Branch");
            System.out.println("================================");

            // LAB 11 / LAB 14: Load saved accounts or initialize defaults.
            Account[] accounts;

            try {
                if (Files.exists(ACCOUNTS_FILE)) {
                    accounts = StatePersister.load(ACCOUNTS_FILE);
                    if (accounts == null) {
                        accounts = new Account[0];
                    }
                    System.out.println("Saved accounts loaded successfully.");
                } else {
                    accounts = new Account[] {
                        new SavingsAccount("Krina", 5000, 1000),
                        new CurrentAccount("Meera", 2000, 5000),
                        new FixedDepositAccount("Bhakti", 10000)
                    };

                    StatePersister.save(accounts, ACCOUNTS_FILE);
                    System.out.println("Default accounts created and saved.");
                }

                synchronizeAccountCounter(accounts);

            } catch (IOException | ClassNotFoundException e) {
                System.out.println(
                        "Unable to load account data: " + e.getMessage());
                return;
            }

            // LAB 13 / LAB 14: Repository and service layer.
            Repository<Account> repository = new Repository<>();

            for (Account account : accounts) {
                repository.save(account);
            }

            BankService bankService = new BankService(repository);

            // LAB 14: Functional menu.
            boolean running = true;

            while (running) {
                printMenu();

                System.out.print("Enter your choice: ");
                String input = sc.nextLine().trim();

                int choice;

                try {
                    choice = Integer.parseInt(input);
                } catch (NumberFormatException e) {
                    System.out.println("Please enter a valid menu number.");
                    continue;
                }

                try {
                    switch (choice) {

                        case 1 -> openAccount(sc, bankService);

                        case 2 -> deposit(sc, bankService);

                        case 3 -> withdraw(sc, bankService);

                        case 4 -> transfer(sc, bankService);

                        case 5 -> listAccounts(bankService);

                        case 6 -> showStatement(sc, bankService);

                        case 7 -> {
                            ReportGenerator.generate(
                                    LOG_DIRECTORY, REPORT_FILE);
                            System.out.println(
                                    "Report generated: " + REPORT_FILE);
                        }

                        case 8 -> {
                            System.out.println(
                                    "Working Hours: Monday to Friday, "
                                    + "9:00 AM to 5:00 PM");
                        }

                        case 9 -> {
                            saveAccounts(bankService);
                            System.out.println(
                                    "Thank you for using MiniBank.");
                            System.out.println("Goodbye!");
                            running = false;
                        }

                        default ->
                                System.out.println(
                                        "Invalid choice. Select 1 to 9.");
                    }

                } catch (BankException e) {
                    System.out.println(
                            "Operation failed: " + e.getMessage());

                } catch (IOException e) {
                    System.out.println(
                            "File operation failed: " + e.getMessage());

                } catch (NumberFormatException e) {
                    System.out.println(
                            "Invalid number. Please try again.");
                }

                System.out.println();
            }

        }
    }

    private static void printMenu() {
        System.out.println("\n------------- MENU -------------");
        System.out.println("1. Open Account");
        System.out.println("2. Deposit");
        System.out.println("3. Withdraw");
        System.out.println("4. Transfer");
        System.out.println("5. List All Accounts");
        System.out.println("6. View Account Statement");
        System.out.println("7. Generate Report");
        System.out.println("8. Working Hours");
        System.out.println("9. Save and Exit");
        System.out.println("--------------------------------");
    }

    private static void openAccount(
            Scanner sc, BankService bankService) throws BankException {

        System.out.println("\nAccount Types:");
        System.out.println("1. Savings Account");
        System.out.println("2. Current Account");
        System.out.println("3. Fixed Deposit Account");

        int type = readInt(sc, "Choose account type: ");

        if (type < 1 || type > 3) {
            System.out.println("Invalid account type.");
            return;
        }

        System.out.print("Enter owner name: ");
        String owner = sc.nextLine().trim();

        if (owner.isEmpty()) {
            System.out.println("Owner name cannot be empty.");
            return;
        }

        long openingBalance =
                readLong(sc, "Enter opening balance: ");

        if (openingBalance < 0) {
            System.out.println("Opening balance cannot be negative.");
            return;
        }

        String accountType;
        switch (type) {
            case 1 -> accountType = "SAVINGS";
            case 2 -> accountType = "CURRENT";
            default -> accountType = "FD";
        }

        Account account = bankService.openAccount(
                owner, accountType, openingBalance);

        System.out.println("Account opened successfully!");
        System.out.println(account);
    }

    private static void deposit(
            Scanner sc, BankService bankService)
            throws BankException, IOException {

        System.out.print("Enter account number: ");
        String accountNumber = sc.nextLine().trim();

        long amount = readLong(sc, "Enter deposit amount: ");

        bankService.deposit(accountNumber, amount);

        TransactionLog.append(
                LOG_FILE,
                "DEPOSIT " + accountNumber + " " + amount);

        Account account = bankService.findAccount(accountNumber);

        System.out.println("Deposit successful.");
        System.out.println("Updated balance: " + account.getBalance());
    }

    private static void withdraw(
            Scanner sc, BankService bankService)
            throws BankException, IOException {

        System.out.print("Enter account number: ");
        String accountNumber = sc.nextLine().trim();

        long amount = readLong(sc, "Enter withdrawal amount: ");

        bankService.withdraw(accountNumber, amount);

        TransactionLog.append(
                LOG_FILE,
                "WITHDRAW " + accountNumber + " " + amount);

        Account account = bankService.findAccount(accountNumber);

        System.out.println("Withdrawal successful.");
        System.out.println("Updated balance: " + account.getBalance());
    }

    private static void transfer(
            Scanner sc, BankService bankService)
            throws BankException, IOException {

        System.out.print("Enter source account number: ");
        String sourceNumber = sc.nextLine().trim();

        System.out.print("Enter destination account number: ");
        String destinationNumber = sc.nextLine().trim();

        long amount = readLong(sc, "Enter transfer amount: ");

        bankService.transfer(sourceNumber, destinationNumber, amount);

        TransactionLog.append(
                LOG_FILE,
                "WITHDRAW " + sourceNumber + " " + amount);

        TransactionLog.append(
                LOG_FILE,
                "DEPOSIT " + destinationNumber + " " + amount);

        Account source = bankService.findAccount(sourceNumber);
        Account destination = bankService.findAccount(destinationNumber);

        System.out.println("Transfer successful.");
        System.out.println("Source balance: " + source.getBalance());
        System.out.println(
                "Destination balance: " + destination.getBalance());
    }

    private static void listAccounts(BankService bankService) {

        ArrayList<Account> accounts = bankService.listAccounts();

        System.out.println("\n--------- ALL ACCOUNTS ---------");

        if (accounts.isEmpty()) {
            System.out.println("No accounts found.");
            return;
        }

        for (Account account : accounts) {
            System.out.println(account);
        }

        System.out.println("Total accounts: " + bankService.countAccounts());
    }

    private static void showStatement(
            Scanner sc, BankService bankService) throws BankException {

        System.out.print("Enter account number: ");
        String accountNumber = sc.nextLine().trim();

        System.out.println(bankService.statement(accountNumber));
    }

    private static void saveAccounts(
            BankService bankService) throws IOException {

        ArrayList<Account> accounts = bankService.listAccounts();

        StatePersister.save(
                accounts.toArray(new Account[0]), ACCOUNTS_FILE);

        ReportGenerator.generate(LOG_DIRECTORY, REPORT_FILE);

        System.out.println("Accounts saved successfully.");
        System.out.println("Report saved successfully.");
    }

    private static int readInt(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    private static long readLong(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim();

            try {
                return Long.parseLong(input);
            } catch (NumberFormatException e) {
                System.out.println("Enter a valid whole number.");
            }
        }
    }

    /*
     * Account.accountCounter is private in the existing Account class.
     * Synchronize it with persisted IDs so newly opened accounts do not
     * reuse an existing AC number after restarting the application.
     */
    private static void synchronizeAccountCounter(Account[] accounts) {
        long maximum = 0;

        for (Account account : accounts) {
            if (account == null) {
                continue;
            }

            String id = account.getAccountNumber();

            try {
                if (id != null && id.startsWith("AC")) {
                    long number = Long.parseLong(id.substring(2));
                    maximum = Math.max(maximum, number);
                }
            } catch (NumberFormatException ignored) {
                // Ignore IDs that do not follow the AC-number format.
            }
        }

        try {
            Field counter = Account.class.getDeclaredField("accountCounter");
            counter.setAccessible(true);
            counter.setLong(null, maximum);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println(
                    "Warning: could not synchronize account counter.");
        }
    }
}
