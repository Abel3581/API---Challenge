package com.intuit.challange.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;



@Entity
@Table (name = "clientes",
        uniqueConstraints = {
                @UniqueConstraint (columnNames = "cuit"),
                @UniqueConstraint(columnNames = "email")
        })
@Getter
@Setter
@NoArgsConstructor // 👈 Obligatorio para JPA
@AllArgsConstructor(access = AccessLevel.PRIVATE) // 👈 Para Builder
@Builder(toBuilder = true)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /* ===============================
       DATOS BÁSICOS
       =============================== */

    @NotBlank
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotBlank
    @Column(name = "razon_social", nullable = false, length = 150)
    private String razonSocial;

    /* ===============================
       IDENTIFICACIÓN
       =============================== */

    @NotBlank
    @Column(nullable = false, unique = true, length = 20)
    private String cuit;

    @NotNull
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    /* ===============================
       CONTACTO
       =============================== */

    @NotBlank
    @Column(name = "telefono_celular", nullable = false, length = 30)
    private String telefonoCelular;

    @NotBlank
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    /* ===============================
       AUDITORÍA
       =============================== */

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;

    /* ===============================
       CICLO DE VIDA
       =============================== */

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.fechaModificacion = LocalDateTime.now();
    }
}
