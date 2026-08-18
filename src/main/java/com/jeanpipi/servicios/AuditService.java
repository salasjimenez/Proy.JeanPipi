package com.jeanpipi.servicios;

// Servicio de auditoria.
import com.jeanpipi.dao.AuditoriaDao;
import com.jeanpipi.modelos.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuditService {
    private static final Logger LOG = LoggerFactory.getLogger(AuditService.class);
    private final AuditoriaDao dao = new AuditoriaDao();

    public void record(Usuario user, String action, String entity, Object entityId, String ip, String detail) {
        try {
            dao.write(user == null ? null : user.getId(), action, entity, entityId == null ? null : String.valueOf(entityId), ip, detail);
        } catch (Exception ex) {
            LOG.warn("No se pudo registrar auditoria: {}", ex.getMessage());
        }
    }
}
