package com.pluralsight;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.NumberFormat;
import java.util.Locale;

//Displays the transaction ledger with filters + totals.
public class LedgerPanel extends JPanel {

    private final LedgerTableModel model;
    private final JTable table;

    private final JComboBox<String> typeFilter = new JComboBox<>(new String[]{"All", "Deposits", "Payments"});
    private final JTextField vendorSearch = new JTextField(18);

    private final JLabel incomeLabel = new JLabel();
    private final JLabel expensesLabel = new JLabel();
    private final JLabel netLabel = new JLabel();

    private final NumberFormat money = NumberFormat.getCurrencyInstance(Locale.US);

    public LedgerPanel(LedgerTableModel model) {
        this.model = model;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setRowHeight(22);
        table.setAutoCreateRowSorter(true);

        // Amount column rendering (green for +, red for -)
        table.getColumnModel().getColumn(4).setCellRenderer(new AmountRenderer());

        add(buildTopBar(), BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(buildTotalsBar(), BorderLayout.SOUTH);

        // React to UI events
        typeFilter.addActionListener(e -> applyFilters());
        vendorSearch.getDocument().addDocumentListener((SimpleDocumentListener) e -> applyFilters());

        // Initial totals
        refreshTotals();
    }

    private JComponent buildTopBar() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Type:"), gbc);

        gbc.gridx = 1;
        panel.add(typeFilter, gbc);

        gbc.gridx = 2;
        panel.add(new JLabel("Vendor search:"), gbc);

        gbc.gridx = 3;
        panel.add(vendorSearch, gbc);

        gbc.gridx = 4;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(Box.createHorizontalGlue(), gbc);

        gbc.gridx = 5;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> {
            model.reloadAll();
            applyFilters();
        });
        panel.add(refresh, gbc);

        return panel;
    }

    private JComponent buildTotalsBar() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 18, 6));
        panel.add(new JLabel("Totals (visible rows):"));
        panel.add(incomeLabel);
        panel.add(expensesLabel);
        panel.add(netLabel);
        return panel;
    }

    private void applyFilters() {
        String type = (String) typeFilter.getSelectedItem();
        String vendor = vendorSearch.getText().trim().toLowerCase();

        model.applyFilter(t -> {
            if ("Deposits".equals(type) && t.getAmount() < 0) return false;
            if ("Payments".equals(type) && t.getAmount() >= 0) return false;

            if (!vendor.isEmpty()) {
                return t.getVendor() != null && t.getVendor().toLowerCase().contains(vendor);
            }
            return true;
        });

        refreshTotals();
    }

    private void refreshTotals() {
        LedgerTableModel.Totals totals = model.getTotalsForVisible();
        incomeLabel.setText("Deposits: " + money.format(totals.income));
        expensesLabel.setText("Payments: " + money.format(totals.expenses));
        netLabel.setText("Net: " + money.format(totals.net));
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
