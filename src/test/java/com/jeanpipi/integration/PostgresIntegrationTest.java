package com.jeanpipi.integration;

// Prueba opcional de integracion con PostgreSQL.
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PostgresIntegrationTest {
    @Test
    @EnabledIfEnvironmentVariable(named = "JEANPIPI_TEST_DB_URL", matches = ".+")
    void connectsAndExecutesQuery() throws Exception {
        String url = System.getenv("JEANPIPI_TEST_DB_URL");
        String user = System.getenv().getOrDefault("JEANPIPI_TEST_DB_USER", "postgres");
        String password = System.getenv().getOrDefault("JEANPIPI_TEST_DB_PASSWORD", "");
        try (Connection connection = DriverManager.getConnection(url, user, password);
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery("SELECT 1")) {
            result.next();
            assertEquals(1, result.getInt(1));
        }
    }
}
