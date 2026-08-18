package com.jeanpipi.servicios;

// Servicio de usuarios y perfiles.
import com.jeanpipi.dao.UsuarioDao;
import com.jeanpipi.modelos.PagedResult;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.util.PasswordUtil;
import com.jeanpipi.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class UsuarioService {
    private static final Set<String> ROLES = Set.of("ADMIN", "EDITOR", "AUTOR", "LECTOR");
    private final UsuarioDao dao = new UsuarioDao();

    public Usuario profile(long id) throws SQLException {
        return dao.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
    }

    public Usuario updateProfile(long id, String name, String email, String avatar, String bio, String instagram, String twitter) throws SQLException {
        if (name == null || name.trim().length() < 2) throw new IllegalArgumentException("Nombre invalido");
        if (!ValidationUtil.validEmail(email)) throw new IllegalArgumentException("Correo invalido");
        Usuario current = profile(id);
        dao.findByEmail(email).filter(u -> u.getId() != id).ifPresent(u -> { throw new IllegalArgumentException("El correo ya esta en uso"); });
        dao.updateProfile(id, name, email, avatar, bio, instagram, twitter);
        return profile(current.getId());
    }

    public void changePassword(long id, String currentPassword, String newPassword) throws SQLException {
        Usuario current = profile(id);
        if (!PasswordUtil.verify(currentPassword, current.getPasswordHash())) throw new SecurityException("Contrasena actual incorrecta");
        List<String> errors = ValidationUtil.registration("Usuario", "user@example.com", newPassword);
        if (errors.stream().anyMatch(e -> e.toLowerCase().contains("contrasena"))) throw new IllegalArgumentException("La nueva contrasena no cumple los requisitos");
        dao.setPassword(id, PasswordUtil.hash(newPassword));
    }

    public PagedResult<Usuario> list(String q, String role, int page, int size) throws SQLException {
        if (role != null && !role.isBlank() && !ROLES.contains(role)) throw new IllegalArgumentException("Rol invalido");
        return dao.list(q, role, page, size);
    }

    public Usuario updateAdmin(long id, String name, String role, boolean active) throws SQLException {
        if (!ROLES.contains(role)) throw new IllegalArgumentException("Rol invalido");
        if (name == null || name.trim().length() < 2) throw new IllegalArgumentException("Nombre invalido");
        dao.updateAdmin(id, name, role, active);
        return profile(id);
    }

    public void deactivate(long id) throws SQLException {
        dao.setActive(id, false);
    }

    public List<Usuario> authors() throws SQLException {
        return dao.listAuthors();
    }
}
