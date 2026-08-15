package com.jeanpipi.listeners;

import com.jeanpipi.servicios.AuthService;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebListener
public class BootstrapListener implements ServletContextListener {
    private static final Logger LOGGER = Logger.getLogger(BootstrapListener.class.getName());

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        String email = System.getenv("JEANPIPI_ADMIN_EMAIL");
        String password = System.getenv("JEANPIPI_ADMIN_PASSWORD");
        String name = System.getenv("JEANPIPI_ADMIN_NAME");

        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            LOGGER.info("Administrador inicial no configurado; se omite el bootstrap de usuario.");
            return;
        }

        try {
            new AuthService().crearAdminInicial(name, email, password);
            LOGGER.info("Bootstrap de administrador verificado correctamente.");
        } catch (RuntimeException e) {
            LOGGER.log(Level.SEVERE, "No se pudo verificar el administrador inicial.", e);
        }
    }
}
