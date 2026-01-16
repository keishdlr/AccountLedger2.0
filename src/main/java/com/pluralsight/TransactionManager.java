package com.pluralsight;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Persists transactions to a MySQL database (instead of a CSV file).
 *
 * Connection settings come from {@link DbConfig}.
 */
public class TransactionManager {

    private static final String DB_URL = DbConfig.URL;
    private static final String DB_USER = DbConfig.getUser();
    private static final String DB_PASSWORD = DbConfig.getPassword();

    // Table + columns are named to avoid reserved keywords.
    private static final String CREATE_TABLE_SQL = """
            CREATE TABLE IF NOT EXISTS transactions (
                id INT NOT NULL AUTO_INCREMENT,
                tx_date DATE NOT NULL,
                tx_time TIME NOT NULL,
                description VARCHAR(255) NOT NULL,
                vendor VARCHAR(255) NOT NULL,
                amount DECIMAL(10,2) NOT NULL,
                PRIMARY KEY (id)
            )
            """;

    static {
        // Make sure the table exists when the app starts.
        ensureSchema();
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    private static void ensureSchema() {
        try (Connection c = getConnection();
             Statement s = c.createStatement()) {
            s.executeUpdate(CREATE_TABLE_SQL);
        } catch (SQLException e) {
            System.out.println("Error ensuring database schema: " + e.getMessage());
            System.out.println("DB URL: " + DB_URL);
        }
    }

    /**
     * Inserts a new transaction row.
     */
    public static void saveTransaction(Transaction transaction) {
        final String sql = """
                INSERT INTO transactions (tx_date, tx_time, description, vendor, amount)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(transaction.getDate()));
            ps.setTime(2, Time.valueOf(transaction.getTime()));
            ps.setString(3, transaction.getDescription());
            ps.setString(4, transaction.getVendor());
            ps.setBigDecimal(5, java.math.BigDecimal.valueOf(transaction.getAmount()));

            ps.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
        }
    }

    /**
     * Loads all transactions from the database.
     */
    public static List<Transaction> loadTransactions() {
        List<Transaction> transactions = new ArrayList<>();

        final String sql = """
                SELECT tx_date, tx_time, description, vendor, amount
                FROM transactions
                ORDER BY tx_date DESC, tx_time DESC, id DESC
                """;

        try (Connection c = getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                LocalDate date = rs.getDate("tx_date").toLocalDate();
                LocalTime time = rs.getTime("tx_time").toLocalTime();
                String description = rs.getString("description");
                String vendor = rs.getString("vendor");
                double amount = rs.getBigDecimal("amount").doubleValue();

                transactions.add(new Transaction(date, time, description, vendor, amount));
            }
        } catch (SQLException e) {
            System.out.println("Error loading transactions: " + e.getMessage());
        }

        return transactions;
    }
}
