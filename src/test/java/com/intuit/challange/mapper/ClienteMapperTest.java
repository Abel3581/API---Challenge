package com.intuit.challange.mapper;

import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.dto.ClienteResponse;
import com.intuit.challange.entity.Cliente;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteMapperTest {

    private ClienteMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ClienteMapper();
    }

    @Test
    @DisplayName("mapToResponse - Debe mapear todos los campos de Entity a Response")
    void mapToResponse_DebeMapearCorrectamente() {
        // GIVEN
        LocalDateTime ahora = LocalDateTime.now();
        Cliente cliente = Cliente.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Perez")
                .razonSocial("JP SRL")
                .cuit("20-30123456-7")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .telefonoCelular("11223344")
                .email("juan@test.com")
                .fechaCreacion(ahora)
                .fechaModificacion(ahora)
                .build();

        // WHEN
        ClienteResponse response = mapper.mapToResponse(cliente);

        // THEN
        assertAll(
                () -> assertEquals(cliente.getId(), response.getId()),
                () -> assertEquals(cliente.getNombre(), response.getNombre()),
                () -> assertEquals(cliente.getApellido(), response.getApellido()),
                () -> assertEquals(cliente.getRazonSocial(), response.getRazonSocial()),
                () -> assertEquals(cliente.getCuit(), response.getCuit()),
                () -> assertEquals(cliente.getFechaNacimiento(), response.getFechaNacimiento()),
                () -> assertEquals(cliente.getTelefonoCelular(), response.getTelefonoCelular()),
                () -> assertEquals(cliente.getEmail(), response.getEmail()),
                () -> assertEquals(cliente.getFechaCreacion(), response.getFechaCreacion()),
                () -> assertEquals(cliente.getFechaModificacion(), response.getFechaModificacion())
        );
    }

    @Test
    @DisplayName("mapToEntity - Debe mapear los campos de Request a una nueva Entity")
    void mapToEntity_DebeMapearCorrectamente() {
        // GIVEN
        ClienteRequest request = ClienteRequest.builder()
                .nombre("Maria")
                .apellido("Gomez")
                .razonSocial("MG Servicios")
                .cuit("27-40123456-8")
                .fechaNacimiento(LocalDate.of(1985, 5, 20))
                .telefonoCelular("55443322")
                .email("maria@test.com")
                .build();

        // WHEN
        Cliente cliente = mapper.mapToEntity(request);

        // THEN
        assertAll(
                () -> assertNull(cliente.getId()), // El ID no viene en el request
                () -> assertEquals(request.getNombre(), cliente.getNombre()),
                () -> assertEquals(request.getApellido(), cliente.getApellido()),
                () -> assertEquals(request.getRazonSocial(), cliente.getRazonSocial()),
                () -> assertEquals(request.getCuit(), cliente.getCuit()),
                () -> assertEquals(request.getFechaNacimiento(), cliente.getFechaNacimiento()),
                () -> assertEquals(request.getTelefonoCelular(), cliente.getTelefonoCelular()),
                () -> assertEquals(request.getEmail(), cliente.getEmail())
        );
    }

    @Test
    @DisplayName("updateEntity - Debe actualizar los campos de una entidad existente")
    void updateEntity_DebeActualizarCampos() {
        // GIVEN
        Cliente clienteExistente = Cliente.builder()
                .id(10L)
                .nombre("Original")
                .email("original@test.com")
                .build();

        ClienteRequest request = ClienteRequest.builder()
                .nombre("Editado")
                .apellido("Nuevo")
                .razonSocial("Nueva Razon")
                .cuit("20-99999999-9")
                .fechaNacimiento(LocalDate.of(2000, 10, 10))
                .telefonoCelular("99887766")
                .email("editado@test.com")
                .build();

        // WHEN
        mapper.updateEntity(clienteExistente, request);

        // THEN
        assertAll(
                () -> assertEquals(10L, clienteExistente.getId()), // El ID debe permanecer intacto
                () -> assertEquals("Editado", clienteExistente.getNombre()),
                () -> assertEquals("Nuevo", clienteExistente.getApellido()),
                () -> assertEquals("editado@test.com", clienteExistente.getEmail()),
                () -> assertEquals("20-99999999-9", clienteExistente.getCuit())
        );
    }
}
