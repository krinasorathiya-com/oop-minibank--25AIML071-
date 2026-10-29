package service;

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

        // Test mobile validation
        System.out.println("\n----- MOBILE VALIDATION -----");
        System.out.println("9876543210: "
                + Validator.isValidMobile("9876543210"));
        System.out.println("12345: "
                + Validator.isValidMobile("12345"));

        // Test email validation
        System.out.println("\n----- EMAIL VALIDATION -----");
        System.out.println("krina@example.com: "
                + Validator.isValidEmail("krina@example.com"));
        System.out.println("krina@wrong: "
                + Validator.isValidEmail("krina@wrong"));

        // Test PAN validation
        System.out.println("\n----- PAN VALIDATION -----");
        System.out.println("ABCDE1234F: "
                + Validator.isValidPan("ABCDE1234F"));
        System.out.println("12345: "
                + Validator.isValidPan("12345"));

        // Test IFSC validation
        System.out.println("\n----- IFSC VALIDATION -----");
        System.out.println("SBIN0001234: "
                + Validator.isValidIfsc("SBIN0001234"));
        System.out.println("ABC123: "
                + Validator.isValidIfsc("ABC123"));

        // Test supplementary amount validation
        System.out.println("\n----- AMOUNT VALIDATION -----");
        System.out.println("500: " + Validator.isValidAmount("500"));
        System.out.println("-500: " + Validator.isValidAmount("-500"));

        // Parse a sample command
        System.out.println("\n----- COMMAND PARSER -----");
System.out.println("\n----- WITHDRAW RULE -----");

// Anonymous class
WithdrawRule rule1 = new WithdrawRule() {
    @Override
    public boolean allow(Account account, long amount) {
        return account.canWithdraw(amount);
    }
};

// Lambda expression
WithdrawRule rule2 = (account, amount) ->
        account.canWithdraw(amount);

System.out.println("Anonymous class: "
        + rule1.allow(accounts[0], 1000));

System.out.println("Lambda: "
        + rule2.allow(accounts[0], 1000));
        try {
            Command command = CommandParser.parse("DEPOSIT AC0001 500");

            System.out.println("type=" + command.type()
                    + ", accountNumber=" + command.accountNumber()
                    + ", amount=" + command.amount());

        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }

        
System.out.println("\n----- ACCOUNT TYPES -----");

Account[] accounts = {
    new SavingsAccount("Krina", 5000, 1000),
    new CurrentAccount("Meera", 2000, 5000),
    new FixedDepositAccount("Bhakti", 10000)
};

for (Account account : accounts) {
    System.out.println(account.getOwnerName()
            + " | Interest Rate: " + account.interestRate()
            + "%");

    if (account instanceof SavingsAccount savings) {
        System.out.println("Minimum Balance: "
                + savings.getMinBalance());
    }
}

        // Print formatted statement
        System.out.println(StatementFormatter.buildStatement(accounts[0]));

        // Existing MiniBank menu
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
                System.out.println(
                        "Invalid choice! Please enter a number between 1 and 6."
                );
                continue;
            }

            MenuOption option = switch (choice) {
                case 1 -> MenuOption.OPEN_ACCOUNT;
                case 2 -> MenuOption.DEPOSIT;
                case 3 -> MenuOption.WITHDRAW;
                case 4 -> MenuOption.TRANSFER;
                case 5 -> MenuOption.WORKING_HOURS;
                case 6 -> MenuOption.EXIT;
                default -> throw new IllegalStateException("Unexpected value");
            };

            switch (option) {
                case OPEN_ACCOUNT ->
                        System.out.println(
                                "Open Account - To be implemented in a later lab."
                        );

                case DEPOSIT ->
                        System.out.println(
                                "Deposit - To be implemented in a later lab."
                        );

                case WITHDRAW ->
                        System.out.println(
                                "Withdraw - To be implemented in a later lab."
                        );

                case TRANSFER ->
                        System.out.println(
                                "Transfer - To be implemented in a later lab."
                        );

                case WORKING_HOURS ->
                        System.out.println(
                                "Bank Working Hours: Monday to Saturday, 9:00 AM to 5:00 PM"
                        );

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