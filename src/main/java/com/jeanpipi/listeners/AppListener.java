package com.jeanpipi.listeners;

// Inicializacion de JeanPipi.
import com.jeanpipi.config.Database;
import com.jeanpipi.servicios.ArticuloService;
import com.jeanpipi.servicios.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@WebListener
public class AppListener implements ServletContextListener {
    private static final Logger LOG = LoggerFactory.getLogger(AppListener.class);
    private ScheduledExecutorService scheduler;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            Database.initialize();
            new AuthService().ensureAdminFromEnvironment();
            ArticuloService articles = new ArticuloService();
            scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "jeanpipi-scheduler");
                t.setDaemon(true);
                return t;
            });
            scheduler.scheduleAtFixedRate(() -> {
                try {
                    int published = articles.publishScheduled();
                    if (published > 0) LOG.info("Articulos programados publicados: {}", published);
                } catch (Exception ex) {
                    LOG.error("Error al publicar articulos programados", ex);
                }
            }, 15, 60, TimeUnit.SECONDS);
            LOG.info("JeanPipi iniciado");
        } catch (Exception ex) {
            LOG.error("No se pudo iniciar JeanPipi", ex);
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (scheduler != null) scheduler.shutdownNow();
        Database.close();
    }
}
