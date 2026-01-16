
package com.pluralsight;
import javax.swing.*;
import java.awt.*;

// Imports Java time classes for capturing the current date and time
import java.time.LocalDate;
import java.time.LocalTime;

// This class represents a reusable UI panel for adding a transaction.
// It extends JPanel, meaning it *is* a Swing panel that can be added to a frame or tab.
public class AddTransactionPanel extends JPanel {

    // Reference to the table model that manages and displays transactions in the ledger
    // This allows this panel to add new transactions to the table
    private final LedgerTableModel model;

    // Text field for entering the transaction description
    private final JTextField descriptionField = new JTextField(25);

    // Text field for entering the vendor name
    private final JTextField vendorField = new JTextField(25);

    // Text field for entering the transaction amount
    private final JTextField amountField = new JTextField(12);

    // Radio button for selecting a deposit (credit)
    // "true" means this option is selected by default
    private final JRadioButton depositRadio = new JRadioButton("Deposit", true);

    // Radio button for selecting a payment (debit)
    private final JRadioButton paymentRadio = new JRadioButton("Payment (Debit)");

    // Constructor: runs when a new AddTransactionPanel is created
    // The LedgerTableModel is injected so we can update it when saving transactions
    public AddTransactionPanel(LedgerTableModel model) {

        // Save the model reference for later use
        this.model = model;

        // Set the layout manager for this panel
        // BorderLayout divides the panel into regions (NORTH, CENTER, SOUTH, etc.)
        // 12px horizontal and vertical spacing between regions
        setLayout(new BorderLayout(12, 12));

        // Adds padding (empty space) around the edges of the panel
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Adds the form (input fields and button) to the top of the panel
        add(buildForm(), BorderLayout.NORTH);

        // Adds the help/instructions text to the center area
        add(buildHelp(), BorderLayout.CENTER);
    }

    // Builds and returns the form section of the panel
    // This method keeps UI construction clean and organized
    private JComponent buildForm() {

        // Create a panel that uses GridBagLayout (flexible grid-based layout)
        JPanel form = new JPanel(new GridBagLayout());

        // Constraints object controls how each component is placed
        GridBagConstraints gbc = new GridBagConstraints();

        // Adds spacing around each component
        gbc.insets = new Insets(6, 6, 6, 6);

        // Aligns components to the left by default
        gbc.anchor = GridBagConstraints.WEST;

        // ButtonGroup ensures only ONE radio button can be selected at a time
        ButtonGroup group = new ButtonGroup();
        group.add(depositRadio);
        group.add(paymentRadio);

        // Tracks which row we are currently adding components to
        int row = 0;

        // --- Row 0: Transaction Type ---
        gbc.gridx = 0;      // Column 0
        gbc.gridy = row;    // Row 0
        form.add(new JLabel("Type:"), gbc);

        gbc.gridx = 1;      // Column 1

        // Panel to hold radio buttons side-by-side
        JPanel typePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        typePanel.add(depositRadio);
        typePanel.add(paymentRadio);
        form.add(typePanel, gbc);

        // --- Row 1: Description ---
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        form.add(descriptionField, gbc);

        // --- Row 2: Vendor ---
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Vendor:"), gbc);

        gbc.gridx = 1;
        form.add(vendorField, gbc);

        // --- Row 3: Amount ---
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;
        form.add(new JLabel("Amount:"), gbc);

        gbc.gridx = 1;
        form.add(amountField, gbc);

        // --- Row 4: Save Button ---
        row++;
        gbc.gridx = 0;
        gbc.gridy = row;

        // Span the button across two columns
        gbc.gridwidth = 2;

        // Center the button horizontally
        gbc.anchor = GridBagConstraints.CENTER;

        // Create the Save button
        JButton saveBtn = new JButton("Save Transaction");

        // When clicked, call the onSave() method
        saveBtn.addActionListener(e -> onSave());

        // Add the button to the form
        form.add(saveBtn, gbc);

        // Return the completed form panel
        return form;
    }

    // Builds the help/instructions section shown below the form
    private JComponent buildHelp() {

        // JTextArea used for multi-line instructional text
        JTextArea help = new JTextArea(
                "Tips:\n" +
                        "- Payments are stored as negative amounts (same as your console app).\n" +
                        "- Data is saved to transactions.csv in the program's working directory.\n" +
                        "- Use the Ledger tab to filter and review your entries."
        );

        // Prevents the user from editing the help text
        help.setEditable(false);

        // Makes background transparent so it blends with the panel
        help.setOpaque(false);

        // Slightly increases font size for readability
        help.setFont(help.getFont().deriveFont(13f));

        // Return the help component
        return help;
    }

    // Handles saving a transaction when the Save button is clicked
    private void onSave() {

        // Read and trim user input
        String desc = descriptionField.getText().trim();
        String vendor = vendorField.getText().trim();
        String amountText = amountField.getText().trim();

        // Validate required fields
        if (desc.isEmpty() || vendor.isEmpty() || amountText.isEmpty()) {

            // Show warning dialog if any field is missing
            JOptionPane.showMessageDialog(this,
                    "Please fill out Description, Vendor, and Amount.",
                    "Missing fields",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Parse the amount into a double
        double amount;
        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException ex) {

            // Show error dialog if the amount is not a valid number
            JOptionPane.showMessageDialog(this,
                    "Amount must be a valid number (example: 12.50).",
                    "Invalid amount",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Determine whether this is a deposit or payment
        boolean isDeposit = depositRadio.isSelected();

        // Payments are stored as negative values
        if (!isDeposit) {
            amount = -Math.abs(amount);
        }

        // Create a new Transaction object with the current date and time
        Transaction t = new Transaction(
                LocalDate.now(),
                LocalTime.now(),
                desc,
                vendor,
                amount
        );

        // Add the transaction to the ledger table model
        model.addTransaction(t);

        // Clear input fields after saving
        descriptionField.setText("");
        vendorField.setText("");
        amountField.setText("");

        // Show confirmation dialog
        JOptionPane.showMessageDialog(this,
                "Transaction saved successfully! OK",
                "Saved",
                JOptionPane.INFORMATION_MESSAGE);
    }
}