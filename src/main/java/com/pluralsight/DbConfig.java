package com.pluralsight;

/**
 * Central place for DB connection settings.
 *
 * The database (schema) must already exist:
 *   jdbc:mysql://localhost:3306/AccountLedger
 *
 * Provide credentials using either:
 *  - Environment variables: LEDGER_DB_USER and LEDGER_DB_PASSWORD (recommended), OR
 *  - JVM system properties: -Dledger.db.user=... -Dledger.db.password=...
 */
public class DbConfig {

    // User-requested URL
    public static final String URL = "jdbc:mysql://localhost:3306/AccountLedger";

    public static String getUser() {
        String prop = System.getProperty("ledger.db.user");
        if (prop != null && !prop.isBlank()) return prop;

        String env = System.getenv("LEDGER_DB_USER");
        if (env != null && !env.isBlank()) return env;

        // Safe default (common in local dev)
        return "root";
    }

    public static String getPassword() {
        String prop = System.getProperty("ledger.db.password");
        if (prop != null) return prop; // allow empty password

        String env = System.getenv("LEDGER_DB_PASSWORD");
        if (env != null) return env;

        // Default empty (common in local dev)
        return "";
    }

    private DbConfig() {
        // no instances
    }
}
