package com.intuit.challange.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClienteResponseTest {

    @Test
    @DisplayName ("Lombok - Verificación de integridad de datos y Builder")
    void testIntegridadDeDatos() {
        LocalDateTime ahora = LocalDateTime.now();
        ClienteResponse response = ClienteResponse.builder()
                .id(1L)
                .nombre("Juan")
                .fechaCreacion(ahora)
                .build();

        assertEquals(1L, response.getId());
        assertEquals("Juan", response.getNombre());
        assertEquals(ahora, response.getFechaCreacion());

        response.setApellido("Perez");
        assertEquals("Perez", response.getApellido());
    }
}
