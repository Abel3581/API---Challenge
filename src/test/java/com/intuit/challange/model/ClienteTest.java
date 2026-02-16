package com.intuit.challange.model;

import com.intuit.challange.entity.Cliente;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {
/*
    @Test
    void onUpdate_deberiaActualizarFechaModificacion() {

        Cliente cliente = new Cliente();
        assertNull(cliente.getFechaModificacion());

        cliente.onUpdate();

        assertNotNull(cliente.getFechaModificacion());
    }

 */
@Test
@DisplayName("Lombok - Debe verificar que Builder, Getters y Setters funcionen")
void testLombokMethods() {
    // GIVEN
    LocalDate fechaNac = LocalDate.of(1990, 5, 10);

    // WHEN
    Cliente cliente = Cliente.builder()
            .id(1L)
            .nombre("Juan")
            .apellido("Perez")
            .razonSocial("JP SRL")
            .cuit("20-30123456-7")
            .fechaNacimiento(fechaNac)
            .telefonoCelular("1165874210")
            .email("juan@test.com")
            .build();

    // THEN
    assertAll(
            () -> assertEquals(1L, cliente.getId()),
            () -> assertEquals("Juan", cliente.getNombre()),
            () -> assertEquals("Perez", cliente.getApellido()),
            () -> assertEquals("JP SRL", cliente.getRazonSocial()),
            () -> assertEquals("20-30123456-7", cliente.getCuit()),
            () -> assertEquals(fechaNac, cliente.getFechaNacimiento()),
            () -> assertEquals("1165874210", cliente.getTelefonoCelular()),
            () -> assertEquals("juan@test.com", cliente.getEmail())
    );

    // Test Setters manuales para cubrir todas las líneas
    cliente.setNombre("Carlos");
    assertEquals("Carlos", cliente.getNombre());
}

    @Test
    @DisplayName("JPA Lifecycle - onCreate debe establecer fechas de creación y modificación")
    void testOnCreate() {
        // GIVEN
        Cliente cliente = new Cliente();

        // WHEN
        cliente.onCreate(); // Invocamos manualmente el método @PrePersist

        // THEN
        assertNotNull(cliente.getFechaCreacion());
        assertNotNull(cliente.getFechaModificacion());
        // Verificamos que sean casi iguales (margen de error de 1 segundo)
        assertTrue(cliente.getFechaCreacion().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertEquals(cliente.getFechaCreacion(), cliente.getFechaModificacion());
    }

    @Test
    @DisplayName ("JPA Lifecycle - onUpdate debe actualizar solo la fecha de modificación")
    void testOnUpdate() {
        // GIVEN
        Cliente cliente = new Cliente();
        cliente.onCreate();
        LocalDateTime fechaCreacionOriginal = cliente.getFechaCreacion();

        // Simulamos un pequeño retraso
        try { Thread.sleep(10); } catch (InterruptedException e) { e.printStackTrace(); }

        // WHEN
        cliente.onUpdate(); // Invocamos manualmente el método @PreUpdate

        // THEN
        assertEquals(fechaCreacionOriginal, cliente.getFechaCreacion()); // La creación no cambia
        assertTrue(cliente.getFechaModificacion().isAfter(fechaCreacionOriginal)); // La modificación es nueva
    }
}


