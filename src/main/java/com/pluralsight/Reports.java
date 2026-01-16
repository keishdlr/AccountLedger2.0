package com.pluralsight;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Collections;

public class Reports {

    public static final String RESET = "\u001B[0m";
    public static final String RED = "\u001B[31m";
    public static final String GREEN = "\u001B[32m";
    public static final String BLUE = "\u001B[34m";
    public static final String YELLOW = "\u001B[33m";

    public static void showReportsMenu(Scanner scanner) {
        boolean viewingReports = true;


        while (viewingReports) {
            System.out.println("\n========== REPORTS MENU ==========");
            System.out.println("1) Month To Date");
            System.out.println("2) Previous Month");
            System.out.println("3) Year To Date");
            System.out.println("4) Previous Year");
            System.out.println("5) Search by Vendor");
            System.out.println("0) Back");
            System.out.print("Choose an option: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    showMonthToDate();
                    break;
                case "2":
                    showPreviousMonth();
                    break;
                case "3":
                    showYearToDate();
                    break;
                case "4":
                    showPreviousYear();
                    break;
                case "5":
                    searchByVendor(scanner);
                    break;
                case "0":
                    viewingReports = false;
                    break;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }


    //this section is for adding methods that will display different report types
    private static void showMonthToDate() {
        List<Transaction> all = TransactionManager.loadTransactions();
        LocalDate now = LocalDate.now();
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getDate().getYear() == now.getYear()
                    && t.getDate().getMonth() == now.getMonth()) {
                filtered.add(t);
            }
        }
        displayResults(filtered, "MONTH TO DATE");
    }

    private static void showPreviousMonth() {
        List<Transaction> all = TransactionManager.loadTransactions();
        LocalDate now = LocalDate.now();
        YearMonth lastMonth = YearMonth.from(now).minusMonths(1);

        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : all) {
            if (YearMonth.from(t.getDate()).equals(lastMonth)) {
                filtered.add(t);
            }
        }
        displayResults(filtered, "PREVIOUS MONTH");
    }

    private static void showYearToDate() {
        List<Transaction> all = TransactionManager.loadTransactions();
        int currentYear = LocalDate.now().getYear();
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getDate().getYear() == currentYear) {
                filtered.add(t);
            }
        }
        displayResults(filtered, "YEAR TO DATE");
    }

    private static void showPreviousYear() {
        List<Transaction> all = TransactionManager.loadTransactions();
        int lastYear = LocalDate.now().getYear() - 1;
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getDate().getYear() == lastYear) {
                filtered.add(t);
            }
        }
        displayResults(filtered, "PREVIOUS YEAR");
    }

    private static void searchByVendor(Scanner scanner) {
        System.out.print("Enter vendor name to search: ");
        String vendor = scanner.nextLine().trim().toLowerCase();
        List<Transaction> all = TransactionManager.loadTransactions();
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : all) {
            if (t.getVendor().toLowerCase().contains(vendor)) {
                filtered.add(t);
            }
        }
        displayResults(filtered, "VENDOR SEARCH: " + vendor);

    }


    // This section is for a custom searching feature


    private static void customSearch(Scanner scanner) {
        System.out.println("\n--- Custom Search ---");
        System.out.print("Start Date (YYYY-MM-DD or blank): ");
        String startInput = scanner.nextLine().trim();

        System.out.print("End Date (YYYY-MM-DD or blank): ");
        String endInput = scanner.nextLine().trim();

        System.out.print("Description (or blank): ");
        String descInput = scanner.nextLine().trim().toLowerCase();

        System.out.print("Vendor (or blank): ");
        String vendorInput = scanner.nextLine().trim().toLowerCase();

        System.out.print("Amount (exact match or blank): ");
        String amountInput = scanner.nextLine().trim();

        List<Transaction> all = TransactionManager.loadTransactions();
        List<Transaction> filtered = new ArrayList<>();

        for (Transaction t : all) {
            LocalDate date = t.getDate();
            String description = t.getDescription().toLowerCase();
            String vendor = t.getVendor().toLowerCase();
            String amount = Double.toString(t.getAmount());

            // This is to skip the transactions that don't match the filters
            if (!startInput.isEmpty() && date.isBefore(LocalDate.parse(startInput))) continue;
            if (!endInput.isEmpty() && date.isAfter(LocalDate.parse(endInput))) continue;
            if (!descInput.isEmpty() && !description.contains(descInput)) continue;
            if (!vendorInput.isEmpty() && !vendor.contains(vendorInput)) continue;
            if (!amountInput.isEmpty() && !amount.equals(amountInput)) continue;

            // Only if all conditions did pass then add it to the filtered list
            filtered.add(t);

            displayResults(filtered, "CUSTOM SEARCH RESULTS");
        }
    }

    //This section is for displaying results//
    private static void displayResults (List < Transaction > transactions, String title){
        System.out.println(BLUE + "\n========== " + title + " ==========" + RESET);
        if (transactions.isEmpty()) {
            System.out.println(RED + "No matching transactions found." + RESET);
            return;
        }
        transactions.sort(Comparator.comparing(Transaction::getDate).reversed());

        System.out.println(YELLOW + "Date       | Time     | Description          | Vendor           | Amount" + RESET);
        System.out.println(YELLOW + "--------------------------------------------------------------------------" + RESET);

        for (Transaction t : transactions) {
            String amountColor;
            if (t.getAmount() >= 0) amountColor = GREEN;
            else amountColor = RED;
            System.out.println(
                    String.format("%s", amountColor) + t + RESET
            );
        }

        System.out.println(YELLOW + "--------------------------------------------------------------------------" + RESET);
        double total = 0.0;
        for (Transaction transaction : transactions) {
            double amount = transaction.getAmount();
            total += amount;
        }
        System.out.printf("Total: %.2f%n", total);
        String totalColor = total >= 0 ? GREEN : RED;
        System.out.printf(YELLOW + "Total: " + totalColor + "%.2f" + RESET + "%n", total);

    }
}