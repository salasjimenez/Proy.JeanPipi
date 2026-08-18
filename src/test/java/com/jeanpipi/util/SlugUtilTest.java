package com.jeanpipi.util;

// Pruebas de URLs amigables.
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SlugUtilTest {
    @Test
    void createsNormalizedSlug() {
        assertEquals("moda-y-tendencias-2026", SlugUtil.from("Moda y Tendencias 2026"));
    }

    @Test
    void usesFallbackForBlankText() {
        assertEquals("articulo", SlugUtil.from("   "));
    }
}
