package com.intuit.challange.exception;

import com.intuit.challange.dto.ClienteRequest;
import jakarta.validation.Valid;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test de Unidad para el Manejador Global de Excepciones.
 * Utiliza un TestController dedicado para disparar cada tipo de excepción
 * y validar que la respuesta JSON (ApiErrorResponse) sea la correcta.
 */
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Configuramos MockMvc de forma independiente (standalone) inyectando el Handler
        mockMvc = MockMvcBuilders.standaloneSetup(new TestController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // --- SECCIÓN: EXCEPCIONES DE NEGOCIO ---

    @Test
    @DisplayName("1. ClienteNotFoundException -> Retorna 404 Not Found")
    void handleNotFound_DebeRetornar404() throws Exception {
        mockMvc.perform(get("/test/not-found"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Cliente no encontrado"));
    }

    @Test
    @DisplayName("2. ArgumentoDuplicadoException -> Retorna 400 Bad Request")
    void handleArgumentoDuplicado_DebeRetornar400() throws Exception {
        mockMvc.perform(get("/test/duplicado"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El email ya existe"));
    }

    // --- SECCIÓN: INTEGRIDAD DE DATOS (DB) ---

    @Test
    @DisplayName("3. DataIntegrityViolation (CUIT) -> Retorna 409 Conflict")
    void handleDataIntegrity_Cuit_DebeRetornar409() throws Exception {
        mockMvc.perform(get("/test/db-cuit"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Ya existe un cliente con ese CUIT"));
    }

    @Test
    @DisplayName("4. DataIntegrityViolation (Email) -> Retorna 409 Conflict")
    void handleDataIntegrity_Email_DebeRetornar409() throws Exception {
        mockMvc.perform(get("/test/db-email"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("Ya existe un cliente con ese email"));
    }

    // --- SECCIÓN: VALIDACIONES DE SPRING ---

    @Test
    @DisplayName("5. MethodArgumentNotValidException -> Valida campos obligatorios (400)")
    void handleValidationExceptions_DebeRetornar400() throws Exception {
        // Enviamos JSON vacío para que fallen las anotaciones @NotBlank
        mockMvc.perform(post("/test/validation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error de validación en los datos enviados"))
                .andExpect(jsonPath("$.validationErrors").exists());
    }

    @Test
    @DisplayName("6. MethodArgumentTypeMismatchException -> Valida tipos de parámetros (400)")
    void handleTypeMismatch_DebeRetornar400() throws Exception {
        // Enviamos 'abc' a un endpoint que espera un ID numérico (Long)
        mockMvc.perform(get("/test/type-mismatch/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", containsString("recibió un valor inválido")));
    }

    // --- SECCIÓN: ERRORES NO CONTROLADOS ---

    @Test
    @DisplayName("7. Exception Genérica -> Retorna 500 Internal Server Error")
    void handleGeneralException_DebeRetornar500() throws Exception {
        mockMvc.perform(get("/test/error"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Ocurrió un error inesperado. Contacte al administrador"));
    }

    /* ========================================================================
        CONTROLADOR DE PRUEBA (INNER CLASS)
        Este controlador existe únicamente para disparar las excepciones que el
        GlobalExceptionHandler debe capturar, permitiendo testear la respuesta JSON.
        ======================================================================== */
    @RestController
    static class TestController {

        @PostMapping("/test/validation")
        public void throwValidation(@Valid @RequestBody ClienteRequest request) {
            // Método vacío intencionalmente: el error es disparado por @Valid
            // antes de entrar al cuerpo del método.
        }

        @GetMapping("/test/type-mismatch/{id}")
        public void throwTypeMismatch(@PathVariable Long id) {
            // Método vacío intencionalmente: el error de tipo (TypeMismatch)
            // ocurre durante el binding del @PathVariable por Spring.
        }

        @GetMapping("/test/not-found")
        public void throwNotFound() { throw new ClienteNotFoundException("Cliente no encontrado"); }

        @GetMapping("/test/duplicado")
        public void throwDuplicado() { throw new ArgumentoDuplicadoException("El email ya existe"); }

        @GetMapping("/test/db-cuit")
        public void throwCuitDb() {
            throw new DataIntegrityViolationException("Error", new RuntimeException("cuit unique constraint"));
        }

        @GetMapping("/test/db-email")
        public void throwEmailDb() {
            throw new DataIntegrityViolationException("Error", new RuntimeException("email unique constraint"));
        }

        @GetMapping("/test/error")
        public void throwAny() throws Exception { throw new Exception("Error genérico"); }
    }
}
