package com.jeanpipi.dao;

// Prueba de contrato con Mockito.
import com.jeanpipi.modelos.Usuario;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MockitoDaoContractTest {
    @Test
    void verifiesDaoInteraction() throws SQLException {
        UsuarioDao dao = mock(UsuarioDao.class);
        Usuario user = new Usuario();
        user.setId(10L);
        user.setNombre("Editor");
        when(dao.findById(10L)).thenReturn(Optional.of(user));
        assertEquals("Editor", dao.findById(10L).orElseThrow().getNombre());
        verify(dao).findById(10L);
    }
}
