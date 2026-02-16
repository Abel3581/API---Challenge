package com.intuit.challange.dto;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class EmailUpdateRequestTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    @DisplayName("Validación - Email mal formado debe fallar")
    void cuandoEmailEsInvalido_entoncesHayViolaciones() {
        EmailUpdateRequest request = new EmailUpdateRequest("no-es-un-email");
        var violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    @DisplayName("Record - Verificación de acceso al campo")
    void testRecordAccessor() {
        EmailUpdateRequest request = new EmailUpdateRequest("test@test.com");
        assertEquals("test@test.com", request.nuevoEmail());
    }
}
