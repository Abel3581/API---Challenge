package com.intuit.challange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "DTO para la creación de un cliente")
public class ClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(
            description = "Nombre del cliente",
            example = "Juan"
    )
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    @Schema(
            description = "Apellido del cliente",
            example = "Pérez"
    )
    private String apellido;

    @NotBlank(message = "La razón social es obligatoria")
    @Size(min = 2, max = 150, message = "La razón social debe tener entre 2 y 150 caracteres")
    @Schema(
            description = "Razón social del cliente",
            example = "Pérez S.A."
    )
    private String razonSocial;

    @NotBlank(message = "El CUIT es obligatorio")
    @Pattern(
            regexp = "^\\d{2}-\\d{8}-\\d$",
            message = "El CUIT debe tener el formato XX-XXXXXXXX-X"
    )
    @Schema(
            description = "CUIT con formato XX-XXXXXXXX-X",
            example = "20-12345678-3",
            pattern = "^\\d{2}-\\d{8}-\\d$"
    )
    private String cuit;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    @Schema(
            description = "Fecha de nacimiento en formato ISO (yyyy-MM-dd)",
            example = "1990-05-15",
            type = "string",
            format = "date"
    )
    private LocalDate fechaNacimiento;

    @Pattern(
            regexp = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,4}\\)?[-.\\s]?[\\d\\s-]{4,15}$",
            message = "El teléfono tiene un formato inválido"
    )
    @Schema(
            description = "Número de teléfono celular. Puede incluir código país (+54), área y separadores.",
            example = "+54 11 2345-6789",
            pattern = "^\\+?\\d{1,4}?[-.\\s]?\\(?\\d{1,4}\\)?[-.\\s]?[\\d\\s-]{4,15}$"
    )
    private String telefonoCelular;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    @Schema(
            description = "Correo electrónico del cliente",
            example = "juan.perez@email.com"
    )
    private String email;
}
