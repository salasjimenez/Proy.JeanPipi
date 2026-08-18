package com.jeanpipi.config;

// Configuracion de la aplicacion.
public final class AppConfig {
    private AppConfig() {}

    public static String env(String key, String defaultValue) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? defaultValue : value.trim();
    }

    public static String env(String key) {
        return env(key, "");
    }

    public static int envInt(String key, int defaultValue) {
        try {
            return Integer.parseInt(env(key, String.valueOf(defaultValue)));
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    public static boolean isDev() {
        return "dev".equalsIgnoreCase(env("JEANPIPI_ENV", "dev"));
    }

    public static String baseUrl() {
        return env("JEANPIPI_BASE_URL", "http://localhost:8080/JeanPipi").replaceAll("/+$", "");
    }
}
