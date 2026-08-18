package com.jeanpipi.servicios;

// Servicio de autenticacion.
import com.jeanpipi.config.AppConfig;
import com.jeanpipi.dao.PasswordResetDao;
import com.jeanpipi.dao.UsuarioDao;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.util.PasswordUtil;
import com.jeanpipi.util.ValidationUtil;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

public class AuthService {
    private final UsuarioDao usuarioDao = new UsuarioDao();
    private final PasswordResetDao resetDao = new PasswordResetDao();
    private final EmailService emailService = new EmailService();
    private final SecureRandom random = new SecureRandom();

    public Usuario register(String name, String email, String password) throws SQLException {
        List<String> errors = ValidationUtil.registration(name, email, password);
        if (!errors.isEmpty()) throw new IllegalArgumentException(String.join(". ", errors));
        if (usuarioDao.findByEmail(email).isPresent()) throw new IllegalArgumentException("El correo ya esta registrado");
        return usuarioDao.create(name, email, PasswordUtil.hash(password), "LECTOR");
    }

    public Usuario login(String email, String password) throws SQLException {
        if (!ValidationUtil.validEmail(email) || password == null || password.isBlank()) {
            throw new SecurityException("Credenciales invalidas");
        }
        Optional<Usuario> optional = usuarioDao.findByEmail(email);
        if (optional.isEmpty()) throw new SecurityException("Credenciales invalidas");
        Usuario user = optional.get();
        if (!user.isActivo()) throw new SecurityException("Cuenta inactiva");
        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(OffsetDateTime.now())) {
            throw new SecurityException("Cuenta temporalmente bloqueada");
        }
        if (!PasswordUtil.verify(password, user.getPasswordHash())) {
            usuarioDao.recordFailure(user.getId());
            throw new SecurityException("Credenciales invalidas");
        }
        usuarioDao.resetFailures(user.getId());
        return usuarioDao.findById(user.getId()).orElseThrow();
    }

    public void requestReset(String email) throws Exception {
        if (!ValidationUtil.validEmail(email)) return;
        Optional<Usuario> user = usuarioDao.findByEmail(email);
        if (user.isEmpty() || !user.get().isActivo()) return;
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        resetDao.create(user.get().getId(), sha256(token), OffsetDateTime.now().plusMinutes(30));
        String link = AppConfig.baseUrl() + "/restablecer.jsp?token=" + token;
        emailService.sendPasswordReset(user.get().getEmail(), link);
    }

    public void resetPassword(String token, String password) throws Exception {
        if (token == null || token.isBlank()) throw new IllegalArgumentException("Token invalido");
        List<String> errors = ValidationUtil.registration("Usuario", "user@example.com", password);
        if (!errors.isEmpty() && errors.stream().anyMatch(e -> e.toLowerCase().contains("contrasena"))) {
            throw new IllegalArgumentException("La nueva contrasena no cumple los requisitos");
        }
        String hash = sha256(token);
        long userId = resetDao.validUserId(hash).orElseThrow(() -> new IllegalArgumentException("Token invalido o expirado"));
        usuarioDao.setPassword(userId, PasswordUtil.hash(password));
        resetDao.markUsed(hash);
    }

    public void ensureAdminFromEnvironment() throws SQLException {
        String email = AppConfig.env("JEANPIPI_ADMIN_EMAIL");
        String password = AppConfig.env("JEANPIPI_ADMIN_PASSWORD");
        String name = AppConfig.env("JEANPIPI_ADMIN_NAME", "Administrador");
        if (email.isBlank() || password.isBlank() || password.length() < 12) return;
        if (usuarioDao.findByEmail(email).isEmpty()) {
            usuarioDao.create(name, email, PasswordUtil.hash(password), "ADMIN");
        }
    }

    private String sha256(String value) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        return Base64.getUrlEncoder().withoutPadding().encodeToString(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
    }
}
