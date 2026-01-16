package com.pluralsight;

import javax.swing.table.AbstractTableModel;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Table model that displays transactions in a JTable.
 *
 * It supports:
 * - Reloading from transactions.csv
 * - Applying a filter predicate (e.g., deposits only)
 * - Basic totals (income/expenses/net)
 */
public class LedgerTableModel extends AbstractTableModel {

    private static final String[] COLS = {"Date", "Time", "Description", "Vendor", "Amount"};
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private final List<Transaction> all = new ArrayList<>();
    private final List<Transaction> visible = new ArrayList<>();
    private Predicate<Transaction> filter = t -> true;

    public void reloadAll() {
        all.clear();
        all.addAll(TransactionManager.loadTransactions());

        // Sort newest first (date + time)
        all.sort(Comparator.comparing(Transaction::getDate)
                .thenComparing(Transaction::getTime)
                .reversed());

        applyFilter(filter);
    }

    public void applyFilter(Predicate<Transaction> filter) {
        this.filter = (filter == null) ? (t -> true) : filter;

        visible.clear();
        for (Transaction t : all) {
            if (this.filter.test(t)) {
                visible.add(t);
            }
        }
        fireTableDataChanged();
    }

    public List<Transaction> getVisibleTransactions() {
        return new ArrayList<>(visible);
    }

    public Totals getTotalsForVisible() {
        double income = 0;
        double expenses = 0;
        for (Transaction t : visible) {
            if (t.getAmount() >= 0) income += t.getAmount();
            else expenses += t.getAmount();
        }
        return new Totals(income, expenses);
    }

    @Override
    public int getRowCount() {
        return visible.size();
    }

    @Override
    public int getColumnCount() {
        return COLS.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLS[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        // Helps sorting/renderers.
        return switch (columnIndex) {
            case 0 -> String.class;
            case 1 -> String.class;
            case 2 -> String.class;
            case 3 -> String.class;
            case 4 -> Double.class;
            default -> Object.class;
        };
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Transaction t = visible.get(rowIndex);
        return switch (columnIndex) {
            case 0 -> t.getDate().toString();
            case 1 -> t.getTime().withNano(0).format(TIME_FMT);
            case 2 -> t.getDescription();
            case 3 -> t.getVendor();
            case 4 -> t.getAmount();
            default -> null;
        };
    }

    /** Adds a transaction (persist + refresh). */
    public void addTransaction(Transaction transaction) {
        TransactionManager.saveTransaction(transaction);
        reloadAll();
    }

    /**
     * Simple totals struct.
     * expenses is negative (matches your stored data), net = income + expenses.
     */
    public static class Totals {
        public final double income;
        public final double expenses;
        public final double net;

        public Totals(double income, double expenses) {
            this.income = income;
            this.expenses = expenses;
            this.net = income + expenses;
        }
    }
}
