package com.jeanpipi.config;

// Conexion y migraciones de PostgreSQL.
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public final class Database {
    private static volatile HikariDataSource dataSource;

    private Database() {}

    public static synchronized void initialize() {
        if (dataSource != null) {
            return;
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(AppConfig.env("JEANPIPI_DB_URL", "jdbc:postgresql://localhost:5432/jeanpipi_db"));
        config.setUsername(AppConfig.env("JEANPIPI_DB_USER", "postgres"));
        config.setPassword(AppConfig.env("JEANPIPI_DB_PASSWORD", ""));
        config.setMaximumPoolSize(AppConfig.envInt("JEANPIPI_DB_POOL_SIZE", 10));
        config.setMinimumIdle(2);
        config.setPoolName("JeanPipiPool");
        config.setConnectionTimeout(10000);
        config.setValidationTimeout(5000);
        dataSource = new HikariDataSource(config);
        Flyway.configure().dataSource(dataSource).baselineOnMigrate(true).load().migrate();
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            initialize();
        }
        return dataSource.getConnection();
    }

    public static DataSource dataSource() {
        if (dataSource == null) {
            initialize();
        }
        return dataSource;
    }

    public static synchronized void close() {
        if (dataSource != null) {
            dataSource.close();
            dataSource = null;
        }
    }
}
