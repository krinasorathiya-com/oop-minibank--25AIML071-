
import java.util.Scanner;

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

        Account a1 = new Account("Krina", 5000);
        Account a2 = new Account("Meera", 0);
        Account a3 = new Account("Bhakti", 3000);

        a1.deposit(2000);
        a1.withdraw(3000);
        a1.withdraw(10000);

        // Copy account number to check equality.
        Account a1Copy = new Account(a1);

        System.out.println("\n----- ACCOUNT DETAILS -----");
        System.out.println(a1.toString());
        System.out.println(a2.toString());
        System.out.println(a3.toString());

        System.out.println("\n----- EQUALITY CHECK -----");
        System.out.println("a1 equals a1Copy: " + a1.equals(a1Copy));

        Object obj = a1;
        System.out.println("Object is an Account: " + (obj instanceof Account));

        System.out.println("\n" + a1.getStatement());

        Customer.Address address = new Customer.Address(
                "12, Green Park",
                "Rajkot",
                "360001"
        );

        Customer customer = new Customer(
                "Krina",
                "krina@example.com",
                "9876543210",
                address
        );

        System.out.println("\n----- CUSTOMER DETAILS -----");
        System.out.println("Customer ID: " + customer.getCustomerId());
        System.out.println("Name: " + customer.getName());
        System.out.println("Email: " + customer.getEmail());
        System.out.println("Mobile: " + customer.getMobile());
        System.out.println("Address: " + customer.getAddress().getLine());
        System.out.println("City: " + customer.getAddress().getCity());
        System.out.println("Pincode: " + customer.getAddress().getPincode());

        Customer clonedCustomer = customer.clone();
        System.out.println("Customer cloned: " + (customer != clonedCustomer));
        System.out.println("Cloned customer name: " + clonedCustomer.getName());

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