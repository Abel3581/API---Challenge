package com.intuit.challange.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta estándar para errores de la API")
public class ApiErrorResponse {


    @Schema( description = "Código HTTP del error (400, 404, 409, 500, etc.)", example = "400" )
    private int status;

    @Schema( description = "Descripción estándar del error HTTP", example = "Bad Request" )
    private String error;

    @Schema(description = "Mensaje descriptivo del error", example = "Error de validación en los campos enviados")
    private String message;

    @Schema(description = "Endpoint donde ocurrió el error", example = "/api/v1/clientes")
    private String path;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema (
            description = "Fecha y hora en que ocurrió el error (yyyy-MM-dd HH:mm:ss)",
            example = "2026-02-17 19:30:45",
            type = "string",
            pattern = "yyyy-MM-dd HH:mm:ss"
    )
    private LocalDateTime timestamp;

    @Schema(
            description = "Errores específicos de validación por campo",
            example = "{\"email\": \"El email es obligatorio\", \"cuit\": \"Formato inválido\"}"
    )
    private Map<String, String> validationErrors;
}
