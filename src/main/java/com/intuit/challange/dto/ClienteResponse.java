package com.intuit.challange.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteResponse {

    private Long id;
    private String nombre;
    private String apellido;
    private String razonSocial;
    private String cuit;
    @JsonFormat(pattern = "dd/MM/yyyy", timezone = "America/Argentina/Buenos_Aires")
    private LocalDate fechaNacimiento;
    private String telefonoCelular;
    private String email;
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
    private LocalDateTime fechaCreacion;

    @JsonFormat (pattern = "dd/MM/yyyy HH:mm:ss", timezone = "America/Argentina/Buenos_Aires")
    private LocalDateTime fechaModificacion;
}
