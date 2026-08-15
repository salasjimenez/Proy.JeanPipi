package com.jeanpipi.servicios;

import com.jeanpipi.dao.UsuarioDAO;
import com.jeanpipi.exception.ValidationException;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.util.PasswordUtil;

import java.util.Locale;
import java.util.Optional;

public class AuthService {
    private final UsuarioDAO usuarioDAO;

    public AuthService() {
        this(new UsuarioDAO());
    }

    AuthService(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public Optional<Usuario> autenticar(String email, String contrasena) {
        String normalizedEmail = normalizarEmail(email);
        if (contrasena == null || contrasena.isEmpty()) {
            throw new ValidationException("La contrasena es obligatoria.");
        }

        Optional<Usuario> found = usuarioDAO.buscarPorEmail(normalizedEmail);
        if (found.isEmpty()) {
            return Optional.empty();
        }

        Usuario usuario = found.get();
        String storedPassword = usuario.getContrasena();
        if (!PasswordUtil.verify(contrasena, storedPassword)) {
            return Optional.empty();
        }

        if (!PasswordUtil.isHash(storedPassword)) {
            usuarioDAO.actualizarContrasena(usuario.getId(), PasswordUtil.hash(contrasena));
        }

        usuario.setContrasena(null);
        return Optional.of(usuario);
    }

    public void crearAdminInicial(String nombre, String email, String password) {
        String normalizedEmail = normalizarEmail(email);
        if (password == null || password.length() < 12) {
            throw new ValidationException("La contrasena inicial del administrador debe tener al menos 12 caracteres.");
        }
        if (usuarioDAO.buscarPorEmail(normalizedEmail).isPresent()) {
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre(nombre == null || nombre.isBlank() ? "Administrador" : nombre.trim());
        admin.setEmail(normalizedEmail);
        admin.setContrasena(PasswordUtil.hash(password));
        admin.setRol("ADMIN");
        usuarioDAO.registrar(admin);
    }

    private String normalizarEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new ValidationException("El correo electronico es obligatorio.");
        }
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        if (normalized.length() > 254 || !normalized.contains("@")) {
            throw new ValidationException("El correo electronico no es valido.");
        }
        return normalized;
    }
}
