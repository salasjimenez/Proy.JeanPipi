package com.jeanpipi.util;

// Pruebas de validaciones de entrada.
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ValidationUtilTest {
    @Test
    void acceptsValidRegistration() {
        assertTrue(ValidationUtil.registration("Juan Roman", "juan@example.com", "ClaveSegura123").isEmpty());
    }

    @Test
    void rejectsWeakRegistration() {
        assertFalse(ValidationUtil.registration("J", "correo", "123").isEmpty());
    }

    @Test
    void validatesArticleContent() {
        assertTrue(ValidationUtil.article("Titulo valido", "<p>Este contenido editorial tiene longitud suficiente.</p>").isEmpty());
    }
}
