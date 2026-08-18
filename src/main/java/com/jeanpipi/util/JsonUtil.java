package com.jeanpipi.util;

// Respuestas JSON de la API.
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonUtil {
    public static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .registerTypeAdapter(OffsetDateTime.class, new TimeAdapter<>(OffsetDateTime::parse))
            .registerTypeAdapter(LocalDateTime.class, new TimeAdapter<>(LocalDateTime::parse))
            .registerTypeAdapter(LocalDate.class, new TimeAdapter<>(LocalDate::parse))
            .registerTypeAdapter(ZonedDateTime.class, new TimeAdapter<>(ZonedDateTime::parse))
            .create();

    private JsonUtil() {}

    public static <T> T read(HttpServletRequest request, Class<T> type) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8)) {
            return GSON.fromJson(reader, type);
        } catch (JsonSyntaxException ex) {
            throw new IllegalArgumentException("JSON invalido");
        }
    }

    public static void send(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json");
        response.getWriter().write(GSON.toJson(body));
    }

    public static void ok(HttpServletResponse response, Object data) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("data", data);
        send(response, HttpServletResponse.SC_OK, body);
    }

    public static void created(HttpServletResponse response, Object data) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", true);
        body.put("data", data);
        send(response, HttpServletResponse.SC_CREATED, body);
    }

    public static void error(HttpServletResponse response, int status, String message) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", false);
        body.put("message", message);
        send(response, status, body);
    }
}
