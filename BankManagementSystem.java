import java.sql.*;
import java.util.Scanner;

class Bank {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/bank_management";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "OviyaaB@AD123"; 
    private Connection conn;

    public Bank() {
        try {
            conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            System.out.println("Connected to database.");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
            conn = null;
        }
    }

    public boolean isConnected() {
        return conn != null;
    }

    public void createUser(String name, String userId) {
        if (!isConnected()) {
            System.out.println("Database connection is not available.");
            return;
        }
        try {
            String query = "INSERT INTO users (user_id, name) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userId);
            stmt.setString(2, name);
            stmt.executeUpdate();
            System.out.println("User created: " + name);
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    public void createAccount(String userId, String accountNumber, String type) {
        if (!isConnected()) {
            System.out.println("Database connection is not available.");
            return;
        }
        try {
            String query = "INSERT INTO accounts (account_number, user_id, type, balance) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, accountNumber);
            stmt.setString(2, userId);
            stmt.setString(3, type);
            stmt.setDouble(4, 0.0); // Initial balance
            stmt.executeUpdate();
            System.out.println(type + " account created for user ID: " + userId);
        } catch (SQLException e) {
            System.out.println("Error creating account: " + e.getMessage());
        }
    }

    public boolean accountExists(String accountNumber) {
        if (!isConnected()) {
            return false;
        }
        try {
            String query = "SELECT COUNT(*) AS count FROM accounts WHERE account_number = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking account existence: " + e.getMessage());
        }
        return false;
    }

    public void updateBalance(String accountNumber, double newBalance) {
        if (!isConnected()) {
            System.out.println("Database connection is not available.");
            return;
        }
        try {
            String query = "UPDATE accounts SET balance = ? WHERE account_number = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setDouble(1, newBalance);
            stmt.setString(2, accountNumber);
            stmt.executeUpdate();
            System.out.println("Balance updated for account: " + accountNumber);
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    public double getBalance(String accountNumber) {
        if (!isConnected()) {
            System.out.println("Database connection is not available.");
            return -1;
        }
        try {
            String query = "SELECT balance FROM accounts WHERE account_number = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving balance: " + e.getMessage());
        }
        return -1;
    }

    public String getAccountType(String accountNumber) {
        if (!isConnected()) {
            System.out.println("Database connection is not available.");
            return null;
        }
        try {
            String query = "SELECT type FROM accounts WHERE account_number = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("type");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving account type: " + e.getMessage());
        }
        return null;
    }

    public void applyLoan(String accountNumber, double loanAmount) {
        if (!isConnected()) {
            System.out.println("Database connection is not available.");
            return;
        }
        try {
            String query = "INSERT INTO loans (account_number, loan_amount) VALUES (?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, accountNumber);
            stmt.setDouble(2, loanAmount);
            stmt.executeUpdate();
            System.out.println("Loan of " + loanAmount + " applied for account: " + accountNumber);
        } catch (SQLException e) {
            System.out.println("Error applying for loan: " + e.getMessage());
        }
    }

    public void viewAllAccounts() {
        if (!isConnected()) {
            System.out.println("Database connection is not available.");
            return;
        }
        try {
            String query = "SELECT account_number, user_id, type, balance FROM accounts";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            System.out.println("=== All Accounts ===");
            while (rs.next()) {
                System.out.println("Account Number: " + rs.getString("account_number"));
                System.out.println("User ID: " + rs.getString("user_id"));
                System.out.println("Account Type: " + rs.getString("type"));
                System.out.println("Balance: " + rs.getDouble("balance"));
                System.out.println("--------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving accounts: " + e.getMessage());
        }
    }
}



public class BankManagementSystem {
    private static Bank bank = new Bank();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Welcome to the Bank Management System ===");

        while (true) {
            System.out.println("\n1. Create User");
            System.out.println("2. Create Account");
            System.out.println("3. Deposit");
            System.out.println("4. Withdraw");
            System.out.println("5. Apply Loan");
            System.out.println("6. Check Balance");
            System.out.println("7. Check Account Type");
            System.out.println("8. View All Accounts");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            int option = scanner.nextInt();
            scanner.nextLine();

            switch (option) {
                case 1 -> createUser();
                case 2 -> createAccount();
                case 3 -> deposit();
                case 4 -> withdraw();
                case 5 -> applyLoan();
                case 6 -> checkBalance();
                case 7 -> checkAccountType();
                case 8 -> viewAllAccounts();
                case 0 -> {
                    System.out.println("Thank you for using the Bank Management System.");
                    return;
                }
                default -> System.out.println("Invalid option.");
            }
        }
    }

    private static void createUser() {
        System.out.print("Enter user name: ");
        String name = scanner.nextLine();
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        bank.createUser(name, userId);
    }

    private static void createAccount() {
        System.out.print("Enter user ID: ");
        String userId = scanner.nextLine();
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Account type (Savings/Checking): ");
        String type = scanner.nextLine();
        bank.createAccount(userId, accountNumber, type);
    }

    private static void deposit() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        if (!bank.accountExists(accountNumber)) {
            System.out.println("Account not found.");
            return;
        }
        System.out.print("Enter deposit amount: ");
        double amount = scanner.nextDouble();
        double newBalance = bank.getBalance(accountNumber) + amount;
        bank.updateBalance(accountNumber, newBalance);
        System.out.println("Deposit successful.");
    }

    private static void withdraw() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        if (!bank.accountExists(accountNumber)) {
            System.out.println("Account not found.");
            return;
        }
        System.out.print("Enter withdrawal amount: ");
        double amount = scanner.nextDouble();
        double currentBalance = bank.getBalance(accountNumber);
        if (currentBalance >= amount) {
            bank.updateBalance(accountNumber, currentBalance - amount);
            System.out.println("Withdrawal successful.");
        } else {
            System.out.println("Insufficient balance.");
        }
    }

    private static void applyLoan() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        if (!bank.accountExists(accountNumber)) {
            System.out.println("Account not found.");
            return;
        }
        System.out.print("Enter loan amount: ");
        double loanAmount = scanner.nextDouble();
        bank.applyLoan(accountNumber, loanAmount);
    }

    private static void checkBalance() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        double balance = bank.getBalance(accountNumber);
        if (balance != -1) {
            System.out.println("Current balance: " + balance);
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void checkAccountType() {
        System.out.print("Enter account number: ");
        String accountNumber = scanner.nextLine();
        String accountType = bank.getAccountType(accountNumber);
        if (accountType != null) {
            System.out.println("Account type: " + accountType);
        } else {
            System.out.println("Account not found.");
        }
    }

    private static void viewAllAccounts() {
        bank.viewAllAccounts();
    }
}
