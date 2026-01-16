package com.pluralsight;

import javax.swing.*;
import java.awt.*;

public class LedgerAppFrame extends JFrame {

    public LedgerAppFrame() {
        super("Accounting Ledger (Swing)");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(980, 640);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        LedgerTableModel sharedModel = new LedgerTableModel();

        AddTransactionPanel addPanel = new AddTransactionPanel(sharedModel);
        LedgerPanel ledgerPanel = new LedgerPanel(sharedModel);
        ReportsPanel reportsPanel = new ReportsPanel(sharedModel);

        tabs.addTab("Add Transaction", addPanel);
        tabs.addTab("Ledger", ledgerPanel);
        tabs.addTab("Reports", reportsPanel);

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);

        // Initial load.
        sharedModel.reloadAll();
    }
}
