package com.jeanpipi.servicios;

// Servicio de correo electronico.
import com.jeanpipi.config.AppConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);

    public void sendPasswordReset(String email, String link) {
        String host = AppConfig.env("JEANPIPI_SMTP_HOST");
        String user = AppConfig.env("JEANPIPI_SMTP_USER");
        String password = AppConfig.env("JEANPIPI_SMTP_PASSWORD");
        String from = AppConfig.env("JEANPIPI_SMTP_FROM", user);
        if (host.isBlank() || user.isBlank() || password.isBlank() || from.isBlank()) {
            LOG.info("Correo de recuperacion preparado para {}", email);
            return;
        }
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", host);
            props.put("mail.smtp.port", String.valueOf(AppConfig.envInt("JEANPIPI_SMTP_PORT", 587)));
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, password);
                }
            });
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Recuperacion de acceso - JeanPipi");
            message.setText("Usa este enlace para restablecer tu contrasena: " + link + "\nEl enlace expira en 30 minutos.");
            Transport.send(message);
        } catch (MessagingException ex) {
            LOG.error("No se pudo enviar correo de recuperacion", ex);
        }
    }
}
