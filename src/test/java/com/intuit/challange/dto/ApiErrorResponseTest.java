package com.intuit.challange.dto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

public class ApiErrorResponseTest {

    @Test
    @DisplayName("Estructura - Verificación de campos de error")
    void testErrorFields() {
        LocalDateTime ahora = LocalDateTime.now();
        ApiErrorResponse error = new ApiErrorResponse(
                400, "Bad Request", "Mensaje", "/api/test", ahora, Map.of("campo", "error")
        );

        assertEquals(400, error.getStatus());
        assertEquals("Mensaje", error.getMessage());
        assertEquals(ahora, error.getTimestamp());
        assertFalse(error.getValidationErrors().isEmpty());
    }
}
