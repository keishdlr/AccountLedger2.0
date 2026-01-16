package com.pluralsight;

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Tiny helper to avoid boilerplate when reacting to JTextField changes.
 */
@FunctionalInterface
public interface SimpleDocumentListener extends DocumentListener {
    void update(DocumentEvent e);

    @Override
    default void insertUpdate(DocumentEvent e) {
        update(e);
    }

    @Override
    default void removeUpdate(DocumentEvent e) {
        update(e);
    }

    @Override
    default void changedUpdate(DocumentEvent e) {
        update(e);
    }
}
