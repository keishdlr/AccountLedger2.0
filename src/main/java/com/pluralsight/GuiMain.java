package com.pluralsight;

import javax.swing.*;

public class GuiMain {
    public static void main(String[] args) {
        // Use the system look and feel so it looks native on each OS.
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // If look & feel can't be set, Swing will fall back to default.
        }

        SwingUtilities.invokeLater(() -> {
            LedgerAppFrame frame = new LedgerAppFrame();
            frame.setVisible(true);
        });
    }
}
