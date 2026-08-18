package com.jeanpipi.servicios;

// Servicio de comentarios.
import com.jeanpipi.dao.ComentarioDao;
import com.jeanpipi.modelos.Comentario;
import com.jeanpipi.modelos.PagedResult;
import com.jeanpipi.modelos.Usuario;
import com.jeanpipi.util.HtmlUtil;
import com.jeanpipi.util.ValidationUtil;

import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public class ComentarioService {
    private static final Set<String> STATUS = Set.of("PENDIENTE", "APROBADO", "OCULTO", "RECHAZADO");
    private final ComentarioDao dao = new ComentarioDao();

    public Comentario create(Usuario user, Comentario item) throws SQLException {
        String content = HtmlUtil.plainText(item.getContenido()).trim();
        if (content.length() < 2 || content.length() > 2000) throw new IllegalArgumentException("Comentario invalido");
        if (user != null) {
            item.setUserId(user.getId()); item.setNombre(user.getNombre()); item.setEmail(user.getEmail());
        } else {
            if (item.getNombre() == null || item.getNombre().trim().length() < 2) throw new IllegalArgumentException("Nombre invalido");
            if (item.getEmail() != null && !item.getEmail().isBlank() && !ValidationUtil.validEmail(item.getEmail())) throw new IllegalArgumentException("Correo invalido");
        }
        item.setContenido(content);
        return dao.create(item);
    }

    public List<Comentario> approved(long articleId) throws SQLException { return dao.approvedForArticle(articleId); }
    public PagedResult<Comentario> adminList(String status, int page, int size) throws SQLException { return dao.listAdmin(status, page, size); }

    public void moderate(long id, String status) throws SQLException {
        if (!STATUS.contains(status)) throw new IllegalArgumentException("Estado invalido");
        dao.moderate(id, status);
    }

    public void delete(long id) throws SQLException { dao.delete(id); }
}
