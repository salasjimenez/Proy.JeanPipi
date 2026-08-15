package com.jeanpipi.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class ConexionDB {
    private static final String DEFAULT_URL = "jdbc:postgresql://localhost:5432/jeanpipi_db";
    private static final String DEFAULT_USER = "postgres";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError("No se encontro el driver PostgreSQL.");
        }
    }

    private ConexionDB() {
    }

    public static Connection getConnection() throws SQLException {
        return obtenerConexion();
    }

    public static Connection obtenerConexion() throws SQLException {
        return DriverManager.getConnection(
                resolver("jeanpipi.db.url", "JEANPIPI_DB_URL", DEFAULT_URL),
                resolver("jeanpipi.db.user", "JEANPIPI_DB_USER", DEFAULT_USER),
                resolver("jeanpipi.db.password", "JEANPIPI_DB_PASSWORD", "")
        );
    }

    private static String resolver(String systemProperty, String environmentVariable, String defaultValue) {
        String propertyValue = System.getProperty(systemProperty);
        if (propertyValue != null && !propertyValue.isBlank()) {
            return propertyValue.trim();
        }

        String environmentValue = System.getenv(environmentVariable);
        if (environmentValue != null && !environmentValue.isBlank()) {
            return environmentValue.trim();
        }

        return defaultValue;
    }
}
