package com.jeanpipi.servlets;

// Entrega segura de archivos subidos.
import com.jeanpipi.config.AppConfig;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@WebServlet("/uploads/*")
public class UploadServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String name = req.getPathInfo();
        if (name == null || name.length() < 2) { res.sendError(404); return; }
        name = Path.of(name.substring(1)).getFileName().toString();
        if (!name.matches("[A-Za-z0-9._-]+")) { res.sendError(404); return; }
        Path dir = Path.of(AppConfig.env("JEANPIPI_UPLOAD_DIR", "uploads")).toAbsolutePath().normalize();
        Path file = dir.resolve(name).normalize();
        if (!file.startsWith(dir) || !Files.isRegularFile(file)) { res.sendError(404); return; }
        String type = Files.probeContentType(file);
        res.setContentType(type == null ? "application/octet-stream" : type);
        res.setHeader("Cache-Control", "public, max-age=604800");
        Files.copy(file, res.getOutputStream());
    }
}
