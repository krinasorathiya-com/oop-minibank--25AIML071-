
public class Account {

    private final String accountNumber;
    private String ownerName;
    private long balance;
    private boolean active;

    private static long accountCounter = 0;

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

    // Copy constructor for testing equals()
    public Account(Account other) {
        this.accountNumber = other.accountNumber;
        this.ownerName = other.ownerName;
        this.balance = other.balance;
        this.active = other.active;
    }

    public void deposit(long amount) {
        if (amount <= 0) {
            System.out.println("Deposit amount must be positive.");
            return;
        }

        balance = balance + amount;
    }

    public boolean withdraw(long amount) {
        if (amount <= 0 || amount > balance) {
            return false;
        }

        balance = balance - amount;
        return true;
    }

    public static boolean transfer(Account from, Account to, long amount) {
        if (from == null || to == null || from == to || amount <= 0) {
            return false;
        }

        if (from.withdraw(amount)) {
            to.deposit(amount);
            return true;
        }

        return false;
    }

    public String getAccountNumber() {
        return accountNumber;
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
        return accountNumber + " | " + ownerName + " | " + balance
                + " | Active: " + active;
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