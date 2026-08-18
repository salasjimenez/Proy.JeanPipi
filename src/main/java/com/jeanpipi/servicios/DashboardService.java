package com.jeanpipi.servicios;

// Servicio del dashboard administrativo.
import com.jeanpipi.dao.AuditoriaDao;
import com.jeanpipi.dao.DashboardDao;
import com.jeanpipi.dao.HomeSectionDao;

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class DashboardService {
    private final DashboardDao dashboardDao = new DashboardDao();
    private final AuditoriaDao auditDao = new AuditoriaDao();
    private final HomeSectionDao sectionDao = new HomeSectionDao();

    public Map<String, Object> data() throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>(dashboardDao.metrics());
        data.put("auditoria", auditDao.latest(20));
        data.put("secciones", sectionDao.list());
        return data;
    }

    public List<Map<String, Object>> sections() throws SQLException { return sectionDao.list(); }

    public void updateSection(long id, String title, boolean enabled, int position, int limit) throws SQLException {
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Titulo invalido");
        sectionDao.update(id, title.trim(), enabled, Math.max(0, position), Math.max(1, Math.min(24, limit)));
    }
}
