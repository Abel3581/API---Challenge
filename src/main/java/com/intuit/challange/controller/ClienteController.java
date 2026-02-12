package com.intuit.challange.controller;

import com.intuit.challange.dto.ApiErrorResponse;
import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.dto.ClienteResponse;
import com.intuit.challange.dto.EmailUpdateRequest;
import com.intuit.challange.service.abstraction.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping ("/api/clientes")
@RequiredArgsConstructor
@Tag (name = "Clientes", description = "API para la gestión integral de clientes")
public class ClienteController {
    private final ClienteService service;

    @PostMapping
    @Operation(summary = "Registrar un nuevo cliente",
            description = "Crea un cliente en el sistema. Valida que el CUIT y Email sean únicos y que los campos cumplan con el formato requerido.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o duplicados",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflicto de integridad en la base de datos",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @GetMapping
    @Operation(summary = "Obtener todos los clientes",
            description = "Retorna una lista completa de los clientes registrados en formato simplificado.")
    @ApiResponse(responseCode = "200", description = "Lista obtenida correctamente")
    public ResponseEntity<List<ClienteResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID",
            description = "Busca un cliente específico. Si no existe, devuelve un error 404.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "El ID proporcionado no pertenece a ningún cliente",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ClienteResponse> buscarPorId(
            @Parameter(description = "ID numérico del cliente", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar un cliente existente",
            description = "Actualiza los datos del cliente. Si el CUIT o Email cambian, verifica que no pertenezcan ya a otro registro.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Error en los datos enviados",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No se encontró el cliente para actualizar",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ClienteResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(service.actualizar(id, request));
    }

    @PatchMapping("/{id}/email")
    @Operation(summary = "Actualizar email del cliente",
            description = "Modifica únicamente el email del cliente validando que no esté duplicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Email actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Email duplicado o formato inválido",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<ClienteResponse> actualizarEmail(
            @PathVariable Long id,
            @Valid @RequestBody EmailUpdateRequest request) { // Recibe el string directamente
        return ResponseEntity.ok(service.actualizarEmail(id, request.nuevoEmail()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente", description = "Borra físicamente el registro del cliente de la base de datos.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado con éxito"),
            @ApiResponse(responseCode = "404", description = "El cliente que intenta eliminar no existe",
                    content = @Content(schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Ocultamos los endpoints de prueba de la documentación pública
    @Operation(hidden = true)
    @GetMapping("/force-duplicate")
    public void forceDuplicate() {
        throw new org.springframework.dao.DataIntegrityViolationException("email");
    }

    @Operation(hidden = true)
    @GetMapping("/throw-exception")
    public void throwException() {
        throw new RuntimeException("Error forzado");
    }

    @GetMapping("/search")
    @Operation(summary = "Búsqueda por nombre", description = "Busca clientes mediante un Stored Procedure")
    public ResponseEntity<List<ClienteResponse>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(service.buscarPorNombre(nombre));
    }
}
