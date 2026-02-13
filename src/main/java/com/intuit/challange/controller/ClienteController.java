package com.intuit.challange.controller;

import com.intuit.challange.dto.*;
import com.intuit.challange.exception.TestException;
import com.intuit.challange.service.abstraction.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Tag(name = "Clientes", description = "API para la gestión integral de clientes")
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    @Operation(summary = "Registrar un nuevo cliente",
            description = "Crea un cliente en el sistema. Valida que el CUIT y Email sean únicos.")
    @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente")
    @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "409", description = "Conflicto de integridad",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @GetMapping
    @Operation(summary = "Listar clientes con paginación",
            description = "Obtiene una lista paginada de todos los clientes.")
    @ApiResponse(responseCode = "200", description = "Lista paginada obtenida correctamente",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PagedResponse.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<PagedResponse<ClienteResponse>> listar(
            @ParameterObject @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {
        return ResponseEntity.ok(service.listar(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID",
            description = "Busca un cliente específico. Si no existe, devuelve un error 404.")
    @ApiResponse(responseCode = "200", description = "Cliente encontrado")
    @ApiResponse(responseCode = "404", description = "ID no encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<ClienteResponse> buscarPorId(
            @Parameter(description = "ID numérico del cliente", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente existente",
            description = "Actualiza los datos del cliente validando unicidad de CUIT y Email.")
    @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente")
    @ApiResponse(responseCode = "400", description = "Error en los datos enviados",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "No se encontró el cliente",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<ClienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/email")
    @Operation(summary = "Actualizar email del cliente",
            description = "Modifica únicamente el email del cliente.")
    @ApiResponse(responseCode = "200", description = "Email actualizado correctamente")
    @ApiResponse(responseCode = "400", description = "Email duplicado o inválido",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<ClienteResponse> actualizarEmail(
            @PathVariable Long id,
            @Valid @RequestBody EmailUpdateRequest request) {
        return ResponseEntity.ok(service.actualizarEmail(id, request.nuevoEmail()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", description = "Borra físicamente el registro del cliente.")
    @ApiResponse(responseCode = "204", description = "Cliente eliminado con éxito")
    @ApiResponse(responseCode = "404", description = "El cliente no existe",
            content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Búsqueda por nombre", description = "Busca clientes mediante un Stored Procedure")
    @ApiResponse(responseCode = "200", description = "Búsqueda completada")
    public ResponseEntity<List<ClienteResponse>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }

    @Operation(hidden = true)
    @GetMapping("/force-duplicate")
    public void forceDuplicate() {
        throw new org.springframework.dao.DataIntegrityViolationException("email");
    }

    @Operation(hidden = true)
    @GetMapping("/force-duplicate-cuit")
    public void forceDuplicateCuit() {
        throw new org.springframework.dao.DataIntegrityViolationException("cuit");
    }

    @Operation(hidden = true)
    @GetMapping("/throw-exception")
    public void throwException() {
        throw new TestException("Error forzado para pruebas");
    }

}