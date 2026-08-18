package com.jeanpipi.servicios;

// Servicio de biblioteca multimedia.
import com.jeanpipi.config.AppConfig;
import com.jeanpipi.dao.MedioDao;
import com.jeanpipi.modelos.Medio;
import com.jeanpipi.modelos.Usuario;

import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.file.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MediaService {
    private static final Set<String> TYPES = Set.of("image/jpeg", "image/png", "image/gif", "image/webp");
    private static final long MAX_SIZE = 10L * 1024 * 1024;
    private final MedioDao dao = new MedioDao();

    public Medio upload(Usuario user, Part part, String contextPath) throws IOException, SQLException {
        if (part == null || part.getSize() <= 0 || part.getSize() > MAX_SIZE) throw new IllegalArgumentException("Archivo invalido o demasiado grande");
        String type = part.getContentType();
        if (!TYPES.contains(type)) throw new IllegalArgumentException("Formato de imagen no permitido");
        String original = Path.of(part.getSubmittedFileName() == null ? "imagen" : part.getSubmittedFileName()).getFileName().toString();
        String extension = extension(original, type);
        String stored = UUID.randomUUID() + extension;
        Path dir = Path.of(AppConfig.env("JEANPIPI_UPLOAD_DIR", "uploads")).toAbsolutePath().normalize();
        Files.createDirectories(dir);
        Path target = dir.resolve(stored).normalize();
        if (!target.startsWith(dir)) throw new SecurityException("Ruta invalida");
        try (var input = part.getInputStream()) { Files.copy(input, target, StandardCopyOption.REPLACE_EXISTING); }
        Medio item = new Medio();
        item.setUserId(user.getId()); item.setNombreArchivo(original); item.setNombreGuardado(stored); item.setMimeType(type); item.setTamano(part.getSize());
        item.setUrl(contextPath + "/uploads/" + stored);
        return dao.create(item);
    }

    public List<Medio> list() throws SQLException { return dao.list(); }

    public void delete(long id) throws SQLException, IOException {
        Medio item = dao.find(id).orElseThrow(() -> new IllegalArgumentException("Archivo no encontrado"));
        Path dir = Path.of(AppConfig.env("JEANPIPI_UPLOAD_DIR", "uploads")).toAbsolutePath().normalize();
        Files.deleteIfExists(dir.resolve(item.getNombreGuardado()).normalize());
        dao.delete(id);
    }

    private String extension(String original, String type) {
        int dot = original.lastIndexOf('.');
        if (dot >= 0 && dot < original.length() - 1) {
            String ext = original.substring(dot).toLowerCase();
            if (Set.of(".jpg", ".jpeg", ".png", ".gif", ".webp").contains(ext)) return ext;
        }
        return switch (type) { case "image/png" -> ".png"; case "image/gif" -> ".gif"; case "image/webp" -> ".webp"; default -> ".jpg"; };
    }
}
