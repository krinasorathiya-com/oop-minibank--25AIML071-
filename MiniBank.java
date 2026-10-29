
import java.util.Scanner;

record BankInfo(String name, String branch) {
}

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

        Account account1 = new Account("Riya", 5000);
        Account account2 = new Account("Aarav");
        Account account3 = new Account("Neha", 3000);

        Account[] accounts = {account1, account2, account3};

        account1.deposit(2000);
        System.out.println("Balance after deposit: " + account1.getBalance());

        boolean result1 = account1.withdraw(3000);
        System.out.println("Withdrawal successful: " + result1);
        System.out.println("Balance after withdrawal: " + account1.getBalance());

        boolean result2 = account1.withdraw(10000);
        System.out.println("Withdrawal successful: " + result2);
        System.out.println("Balance after failed withdrawal: " + account1.getBalance());

        System.out.println("\n----- ACCOUNT DETAILS -----");

        for (Account account : accounts) {
            System.out.println("Account Number: " + account.getAccountNumber());
            System.out.println("Owner: " + account.getOwnerName());
            System.out.println("Balance: " + account.getBalance());
            System.out.println("---------------------------");
        }

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
                System.out.println("Invalid choice! Please enter a number between 1 and 6.");
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
                    System.out.println("Open Account - To be implemented in a later lab.");

                case DEPOSIT ->
                    System.out.println("Deposit - To be implemented in a later lab.");

                case WITHDRAW ->
                    System.out.println("Withdraw - To be implemented in a later lab.");

                case TRANSFER ->
                    System.out.println("Transfer - To be implemented in a later lab.");

                case WORKING_HOURS ->
                    System.out.println("Bank Working Hours: Monday to Saturday, 9:00 AM to 5:00 PM");

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
