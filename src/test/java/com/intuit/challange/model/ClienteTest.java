package com.intuit.challange.model;

import com.intuit.challange.entity.Cliente;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void onUpdate_deberiaActualizarFechaModificacion() {

        Cliente cliente = new Cliente();
        assertNull(cliente.getFechaModificacion());

        cliente.onUpdate();

        assertNotNull(cliente.getFechaModificacion());
    }
}
