package com.jeanpipi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class JsonUtil {
    private static final Gson GSON = new GsonBuilder()
            .serializeNulls()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            .create();

    private JsonUtil() {
    }

    public static Gson gson() {
        return GSON;
    }

    public static void escribir(HttpServletResponse response, int status, Object body) throws IOException {
        response.setStatus(status);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("application/json;charset=UTF-8");
        GSON.toJson(body, response.getWriter());
    }

    public static void mensaje(HttpServletResponse response, int status, String message) throws IOException {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("ok", status >= 200 && status < 300);
        body.put("mensaje", message);
        escribir(response, status, body);
    }
}
