package com.jeanpipi.util;

// Pruebas del hash de contrasenas.
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test
    void hashesAndVerifiesPassword() {
        String hash = PasswordUtil.hash("ClaveSegura123");
        assertTrue(PasswordUtil.verify("ClaveSegura123", hash));
        assertFalse(PasswordUtil.verify("OtraClave123", hash));
        assertNotEquals("ClaveSegura123", hash);
    }

    @Test
    void rejectsInvalidHash() {
        assertFalse(PasswordUtil.verify("ClaveSegura123", "texto-invalido"));
    }
}
