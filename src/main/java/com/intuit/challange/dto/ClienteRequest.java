package com.intuit.challange.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 100, message = "El apellido debe tener entre 2 y 100 caracteres")
    private String apellido;

    @NotBlank(message = "La razón social es obligatoria")
    @Size (min = 2, max = 150, message = "La razón social debe tener entre 2 y 150 caracteres")
    private String razonSocial;

    @NotBlank(message = "El CUIT es obligatorio")
    @Pattern(
            regexp = "^(20|23|24|27|30|33|34)[0-9]{8}[0-9]$",
            message = "El CUIT debe tener un formato válido (ej: 20301234567)"
    )
    private String cuit;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    @Past(message = "La fecha de nacimiento debe ser anterior a hoy")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El teléfono celular es obligatorio")
    @Pattern (
            regexp = "^[0-9+()\\s-]{8,30}$",
            message = "El teléfono tiene un formato inválido"
    )
    private String telefonoCelular;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 150, message = "El email no puede superar los 150 caracteres")
    private String email;
}
