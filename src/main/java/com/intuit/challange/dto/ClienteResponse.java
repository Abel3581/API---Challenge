package com.intuit.challange.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO de respuesta con la información del cliente")
public class ClienteResponse {

    @Schema(description = "Identificador único del cliente", example = "1")
    private Long id;

    @Schema(description = "Nombre del cliente", example = "Juan")
    private String nombre;

    @Schema(description = "Apellido del cliente", example = "Pérez")
    private String apellido;

    @Schema(description = "Razón social del cliente", example = "Pérez S.A.")
    private String razonSocial;

    @Schema(description = "CUIT del cliente en formato XX-XXXXXXXX-X", example = "20-12345678-3")
    private String cuit;

    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "America/Argentina/Buenos_Aires")
    @Schema(
            description = "Fecha de nacimiento (dd/MM/yyyy)",
            example = "15/06/1985",
            type = "string",
            pattern = "dd/MM/yyyy"
    )
    private LocalDate fechaNacimiento;

    @Schema(
            description = "Teléfono celular del cliente",
            example = "+54 11 2345-6789"
    )
    private String telefonoCelular;

    @Schema(
            description = "Correo electrónico del cliente",
            example = "juan.perez@email.com"
    )
    private String email;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
    @Schema (
            description = "Fecha de creación del registro (dd/MM/yyyy HH:mm:ss)",
            example = "17/02/2026 18:55:25",
            type = "string",
            pattern = "dd/MM/yyyy HH:mm:ss"
    )
    private LocalDateTime fechaCreacion;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
    @Schema(
            description = "Fecha de última modificación (dd/MM/yyyy HH:mm:ss)",
            example = "17/02/2026 19:10:02",
            type = "string",
            pattern = "dd/MM/yyyy HH:mm:ss"
    )
    private LocalDateTime fechaModificacion;
}