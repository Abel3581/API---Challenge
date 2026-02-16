package com.intuit.challange.dto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ClienteRequestTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    @DisplayName("Validación - CUIT con formato inválido debe fallar")
    void cuandoCuitEsInvalido_entoncesHayViolaciones() {
        ClienteRequest request = ClienteRequest.builder()
                .nombre("Juan").apellido("Perez").razonSocial("S.A.")
                .cuit("12345") // Formato incorrecto
                .email("juan@test.com").fechaNacimiento(LocalDate.of(1990, 1, 1))
                .telefonoCelular("12345678")
                .build();

        Set<ConstraintViolation<ClienteRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
        assertTrue(violations.stream().anyMatch(v -> v.getMessage().contains("XX-XXXXXXXX-X")));
    }

    @Test
    @DisplayName("Lombok - Verificación de Getters y Builder")
    void testGettersYBuilder() {
        ClienteRequest request = ClienteRequest.builder().nombre("Juan").build();
        assertEquals("Juan", request.getNombre());
        assertNotNull(request.toString());
    }
}
