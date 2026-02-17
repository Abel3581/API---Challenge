package com.intuit.challange.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmailUpdateRequest(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        @Size(max = 150, message = "El email no puede superar los 150 caracteres")
        @Schema (
                description = "Correo nuevo del cliente",
                example = "nuevo.correo@email.com"
        )
        String nuevoEmail
) {}