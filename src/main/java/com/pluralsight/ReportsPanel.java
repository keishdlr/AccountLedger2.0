package com.pluralsight;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * GUI re-implementation of your Reports menu.
 *
 * Note: Your existing console Reports.java is unchanged; this panel
 * just performs the same filtering logic and shows results in a table.
 */
public class ReportsPanel extends JPanel {

    private final ReportTableModel reportModel = new ReportTableModel();
    private final JTable table = new JTable(reportModel);

    private final JLabel titleLabel = new JLabel("Pick a report...");
    private final JLabel totalLabel = new JLabel();

    // So the Refresh button can re-run the last report after new transactions are added.
    private Runnable lastReportAction = null;

    private final NumberFormat money = NumberFormat.getCurrencyInstance(Locale.US);

    public ReportsPanel(LedgerTableModel ignoredSharedModel) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, 15f));

        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.getColumnModel().getColumn(4).setCellRenderer(new AmountRenderer());

        add(buildTop(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildBottom(), BorderLayout.SOUTH);

        // Initial empty state
        setReport("", List.of());
    }

    private JComponent buildTop() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(buildButtons(), BorderLayout.CENTER);
        return panel;
    }

    private JComponent buildButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));

        JButton mtd = new JButton("Month To Date");
        mtd.addActionListener(e -> showMonthToDate());
        panel.add(mtd);

        JButton prevMonth = new JButton("Previous Month");
        prevMonth.addActionListener(e -> showPreviousMonth());
        panel.add(prevMonth);

        JButton ytd = new JButton("Year To Date");
        ytd.addActionListener(e -> showYearToDate());
        panel.add(ytd);

        JButton prevYear = new JButton("Previous Year");
        prevYear.addActionListener(e -> showPreviousYear());
        panel.add(prevYear);

        JButton vendor = new JButton("Search Vendor");
        vendor.addActionListener(e -> searchByVendor());
        panel.add(vendor);

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> {
            if (lastReportAction != null) {
                lastReportAction.run();
            } else {
                setReport("", List.of());
            }
        });
        panel.add(refresh);

        return panel;
    }

    private JComponent buildBottom() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 6));
        panel.add(new JLabel("Total:"));
        panel.add(totalLabel);
        return panel;
    }

    private void showMonthToDate() {
        lastReportAction = this::showMonthToDate;
        LocalDate now = LocalDate.now();
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : TransactionManager.loadTransactions()) {
            if (t.getDate().getYear() == now.getYear() && t.getDate().getMonth() == now.getMonth()) {
                filtered.add(t);
            }
        }
        setReport("MONTH TO DATE", filtered);
    }

    private void showPreviousMonth() {
        lastReportAction = this::showPreviousMonth;
        LocalDate now = LocalDate.now();
        YearMonth lastMonth = YearMonth.from(now).minusMonths(1);

        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : TransactionManager.loadTransactions()) {
            if (YearMonth.from(t.getDate()).equals(lastMonth)) {
                filtered.add(t);
            }
        }
        setReport("PREVIOUS MONTH", filtered);
    }

    private void showYearToDate() {
        lastReportAction = this::showYearToDate;
        int currentYear = LocalDate.now().getYear();
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : TransactionManager.loadTransactions()) {
            if (t.getDate().getYear() == currentYear) {
                filtered.add(t);
            }
        }
        setReport("YEAR TO DATE", filtered);
    }

    private void showPreviousYear() {
        lastReportAction = this::showPreviousYear;
        int lastYear = LocalDate.now().getYear() - 1;
        List<Transaction> filtered = new ArrayList<>();
        for (Transaction t : TransactionManager.loadTransactions()) {
            if (t.getDate().getYear() == lastYear) {
                filtered.add(t);
            }
        }
        setReport("PREVIOUS YEAR", filtered);
    }

    private void searchByVendor() {
        String vendor = JOptionPane.showInputDialog(this, "Enter vendor name to search:");
        if (vendor == null) return; // cancelled
        vendor = vendor.trim().toLowerCase();

        final String vendorFinal = vendor;
        lastReportAction = () -> {
            // Re-run with the same vendor string
            List<Transaction> filtered = new ArrayList<>();
            for (Transaction t : TransactionManager.loadTransactions()) {
                if (t.getVendor() != null && t.getVendor().toLowerCase().contains(vendorFinal)) {
                    filtered.add(t);
                }
            }
            setReport("VENDOR SEARCH: " + vendorFinal, filtered);
        };

        lastReportAction.run();
    }

    private void setReport(String title, List<Transaction> transactions) {
        titleLabel.setText(title.isEmpty() ? "Pick a report..." : ("Report: " + title));

        // Copy into a mutable list before sorting (some callers may pass immutable lists).
        List<Transaction> sorted = new ArrayList<>(transactions);
        sorted.sort(Comparator.comparing(Transaction::getDate)
                .thenComparing(Transaction::getTime)
                .reversed());
        reportModel.setTransactions(sorted);

        double total = 0;
        for (Transaction t : sorted) total += t.getAmount();
        totalLabel.setText(money.format(total));
        totalLabel.setForeground(total >= 0 ? new Color(0, 128, 0) : Color.RED.darker());
    }

    private static class ReportTableModel extends AbstractTableModel {
        private static final String[] COLS = {"Date", "Time", "Description", "Vendor", "Amount"};
        private final List<Transaction> tx = new ArrayList<>();

        void setTransactions(List<Transaction> transactions) {
            tx.clear();
            tx.addAll(transactions);
            fireTableDataChanged();
        }

        // No extra persistence needed here; we always read from disk fresh when running a report.

        @Override
        public int getRowCount() {
            return tx.size();
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
            return switch (columnIndex) {
                case 4 -> Double.class;
                default -> String.class;
            };
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Transaction t = tx.get(rowIndex);
            return switch (columnIndex) {
                case 0 -> t.getDate().toString();
                case 1 -> t.getTime().withNano(0).toString();
                case 2 -> t.getDescription();
                case 3 -> t.getVendor();
                case 4 -> t.getAmount();
                default -> null;
            };
        }
    }

    private static class AmountRenderer extends DefaultTableCellRenderer {
        private final NumberFormat money = NumberFormat.getCurrencyInstance(Locale.US);

        @Override
        protected void setValue(Object value) {
            if (value instanceof Number n) {
                double amt = n.doubleValue();
                setText(money.format(amt));
                setForeground(amt >= 0 ? new Color(0, 128, 0) : Color.RED.darker());
            } else {
                super.setValue(value);
            }
        }
    }
}
