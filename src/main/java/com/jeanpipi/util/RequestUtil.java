package com.jeanpipi.util;

// Lectura segura de parametros HTTP.
import javax.servlet.http.HttpServletRequest;

public final class RequestUtil {
    private RequestUtil() {}

    public static int intParam(HttpServletRequest request, String name, int defaultValue, int min, int max) {
        try {
            int value = Integer.parseInt(request.getParameter(name));
            return Math.max(min, Math.min(max, value));
        } catch (Exception ex) {
            return defaultValue;
        }
    }

    public static Long longValue(String value) {
        try {
            return value == null ? null : Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
