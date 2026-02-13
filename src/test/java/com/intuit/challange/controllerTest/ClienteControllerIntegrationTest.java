package com.intuit.challange.controllerTest;


import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.dto.ClienteResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intuit.challange.dto.EmailUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles ("test")
@Transactional
class ClienteControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private ClienteRequest clienteValido;


    @BeforeEach
    void setUp() {

        clienteValido = ClienteRequest.builder()
                .nombre("Juan")
                .apellido("Perez")
                .razonSocial("JP Servicios SRL")
                .cuit("20-30123456-7")
                .fechaNacimiento(LocalDate.of(1990, 5, 10))
                .telefonoCelular("1165874210")
                .email("juan@gmail.com")
                .build();
    }

    /* ===============================
     CREATE OK
     =============================== */
    @Test
    @DisplayName("POST - Debería registrar un cliente exitosamente cuando los datos son válidos")
    void deberiaCrearCliente() throws Exception {

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    /* ===============================
    VALIDACION ERROR
    =============================== */
    @Test
    @DisplayName("POST - Debería retornar 400 cuando el formato del email es inválido")
    void noDebeCrearClienteConEmailInvalido() throws Exception {

        clienteValido.setEmail("email-invalido");

        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.email")
                        .value("El email debe tener un formato válido"));
    }

    @Test
    @DisplayName("POST - Debería retornar 400 por error de negocio al intentar duplicar Email")
    void deberiaRetornarConflictPorEmailDuplicado() throws Exception {
        // Crear primero
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteValido)));

        // Intentar crear duplicado con mismo email
        clienteValido.setEmail("juan@gmail.com");
        clienteValido.setCuit("27-23456789-1");
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Ya existe un cliente con ese email"));
    }

    @Test
    @DisplayName("POST - Debería retornar 400 por error de negocio al intentar duplicar CUIT")
    void deberiaRetornarConflictPorCuitDuplicado() throws Exception {
        // Crear primero
        mockMvc.perform(post("/api/clientes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(clienteValido)));

        // Intentar crear duplicado con CUIT
        clienteValido.setEmail("juan_perez@gmail.com");
        clienteValido.setCuit("20-30123456-7");
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Ya existe un cliente con ese CUIT"));
    }

    /* ===============================
     READ ALL (PAGINADO)
     =============================== */
    @Test
    @DisplayName("GET - Debería listar clientes de forma paginada")
    void deberiaListarClientesPaginados() throws Exception {
        registrarCliente(clienteValido);

        mockMvc.perform(get("/api/clientes")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Verificamos el contenido
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].nombre").value("Juan"))
                .andExpect(jsonPath("$.content[0].apellido").value("Perez"))

                // --- Validamos DTO PagedResponse ---
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.size").value(10));
    }

    @Test
    @DisplayName("GET - Debería retornar contenido vacío al solicitar una página inexistente")
    void deberiaRetornarVacioAlSolicitarPaginaInexistente() throws Exception {
        // 1. Aseguramos que haya al menos un cliente para que totalPages sea > 0
        registrarCliente(clienteValido);

        // 2. Pedimos la página 99 (que no existe)
        mockMvc.perform(get("/api/clientes")
                        .param("page", "99")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)))
                .andExpect(jsonPath("$.page.number").value(99))
                .andExpect(jsonPath("$.page.totalElements").value(1));
    }

    @Test
    @DisplayName("Cobertura - Lista vacía no dispara el error de página")
    void deberiaCubrirIfFalsoPorListaVacia() throws Exception {
        // No registramos nada, la BD está vacía
        mockMvc.perform(get("/api/clientes")
                        .param("page", "0")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    /* ===============================
       READ BY ID
       =============================== */
    @Test
    @DisplayName("GET - Debería obtener un cliente específico por su ID")
    void deberiaBuscarClientePorId() throws Exception {

        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClienteResponse creado =
                objectMapper.readValue(response, ClienteResponse.class);

        mockMvc.perform(get("/api/clientes/" + creado.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Juan"));
    }

    /* ===============================
       UPDATE
       =============================== */
    @Test
    @DisplayName("PUT - Debería actualizar todos los campos de un cliente existente")
    void actualizar_DeberiaSerExitoso_CuandoDatosSonValidos() throws Exception {
        ClienteResponse existente = registrarCliente(clienteValido);

        // Cambiamos solo el nombre (CUIT y Email siguen igual)
        // Esto cubre la parte "false" de los IFs (no entra a las excepciones)
        clienteValido.setNombre("Nuevo Nombre");

        mockMvc.perform(put("/api/clientes/" + existente.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo Nombre"));
    }

    @Test
    @DisplayName("PUT - Debería retornar 404 al intentar actualizar un ID inexistente")
    void actualizar_DeberiaLanzarNotFound_CuandoIdNoExiste() throws Exception {
        // Cubre el .orElseThrow()
        mockMvc.perform(put("/api/clientes/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT - Debería retornar 400 si se intenta cambiar el CUIT por uno ya registrado")
    void actualizar_DeberiaLanzarDuplicado_CuandoCuitYaExisteEnOtroCliente() throws Exception {
        // 1. Crear Cliente A
        registrarCliente(clienteValido);

        // 2. Crear Cliente B (con datos distintos)
        ClienteRequest requestB = crearRequest("Otro", "27-44444444-2", "otro@test.com");
        ClienteResponse clienteB = registrarCliente(requestB);

        // 3. Intentar ponerle a B el CUIT de A
        requestB.setCuit(clienteValido.getCuit());

        mockMvc.perform(put("/api/clientes/" + clienteB.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestB)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El CUIT ya pertenece a otro cliente"));
    }

    @Test
    @DisplayName("PUT - Debería lanzar 400 si se intenta usar un Email que ya pertenece a otro cliente")
    void actualizar_DeberiaLanzarDuplicado_CuandoEmailYaExisteEnOtroCliente() throws Exception {
        // 1. Crear Cliente A
        registrarCliente(clienteValido);

        // 2. Crear Cliente B
        ClienteRequest requestB = crearRequest("Maria", "27-44444444-2", "maria@test.com");
        ClienteResponse clienteB = registrarCliente(requestB);

        // 3. Intentar ponerle a B el Email de A
        requestB.setEmail(clienteValido.getEmail());

        mockMvc.perform(put("/api/clientes/" + clienteB.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestB)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El email ya pertenece a otro cliente"));
    }

    @Test
    @DisplayName("PUT - No debería validar existencia si el CUIT enviado es el mismo que ya tiene el cliente")
    void actualizar_MismoCuit_NoDebeValidarExistencia() throws Exception {
        // 1. Registro inicial
        ClienteResponse creado = registrarCliente(clienteValido);

        // 2. Mantenemos el mismo CUIT pero cambiamos el nombre para que el payload sea distinto
        clienteValido.setNombre("Nombre Editado");

        mockMvc.perform(put("/api/clientes/" + creado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nombre Editado"));
    }

    @Test
    @DisplayName("PUT - Debería permitir el cambio de CUIT si el nuevo valor no existe en la base de datos")
    void actualizar_CuitNuevo_NoExistente() throws Exception {
        ClienteResponse creado = registrarCliente(clienteValido);

        // 3. Mandamos un CUIT diferente que NO existe en la BD.
        // (!cliente.equals(request)) es TRUE, pero repository.exists es FALSE.
        // Cubre la segunda parte del '&&' en su rama falsa.
        clienteValido.setCuit("27-99999999-1");

        mockMvc.perform(put("/api/clientes/" + creado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT - Debería fallar si el nuevo CUIT solicitado ya está siendo usado por otro registro")
    void actualizar_CuitNuevo_YaExistente() throws Exception {
        // 4. Creamos Cliente A y Cliente B.
        registrarCliente(clienteValido);

        ClienteRequest reqB = crearRequest("Maria", "27-44444444-2", "maria@test.com");
        ClienteResponse resB = registrarCliente(reqB);

        // Intentamos ponerle a B el CUIT de A.
        // TRUE && TRUE -> Lanza excepción.
        reqB.setCuit("20-30123456-7");

        mockMvc.perform(put("/api/clientes/" + resB.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqB)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PUT - No debería validar existencia si el Email enviado es el mismo que ya tiene el cliente")
    void actualizar_MismoEmail_NoDebeValidarExistencia() throws Exception {
        // 1. Registro inicial
        ClienteResponse creado = registrarCliente(clienteValido);

        // 2. Mantenemos el mismo Email pero cambiamos la Razón Social
        // Al cambiar un campo distinto al del test anterior, Sonar ya no los ve como duplicados
        clienteValido.setRazonSocial("Nueva Razon Social S.A.");

        mockMvc.perform(put("/api/clientes/" + creado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.razonSocial").value("Nueva Razon Social S.A."));
    }

    @Test
    @DisplayName("PUT - Debería permitir el cambio de Email si el nuevo valor no está registrado")
    void actualizar_EmailNuevo_NoExistente() throws Exception {
        ClienteResponse creado = registrarCliente(clienteValido);

        // 3. Mandamos un Email diferente que NO existe en la BD.
        // (!cliente.equals(request)) es TRUE, pero repository.existsByEmail es FALSE.
        // Cubre la rama falsa de la segunda condición del '&&'.
        clienteValido.setEmail("nuevo_email@test.com");

        mockMvc.perform(put("/api/clientes/" + creado.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nuevo_email@test.com"));
    }

    @Test
    @DisplayName("PUT - Debería fallar si el nuevo Email solicitado ya está registrado en otro cliente")
    void actualizar_EmailNuevo_YaExistente() throws Exception {
        // 4. Creamos Cliente A (con email original) y Cliente B.
        registrarCliente(clienteValido); // Email: juan@gmail.com

        ClienteRequest reqB = crearRequest("Maria", "27-44444444-2", "maria@test.com");
        ClienteResponse resB = registrarCliente(reqB);

        // Intentamos ponerle a B el Email de A.
        // TRUE (es distinto al de B) && TRUE (ya existe en A) -> Lanza ArgumentoDuplicadoException.
        reqB.setEmail("juan@gmail.com");

        mockMvc.perform(put("/api/clientes/" + resB.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reqB)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("El email ya pertenece a otro cliente"));
    }

    // Helper para crear requests rápidas
    private ClienteRequest crearRequest(String nombre, String cuit, String email) {
        return ClienteRequest.builder()
                .nombre(nombre).apellido("Test").razonSocial("Test SA")
                .cuit(cuit).email(email).telefonoCelular("12345678")
                .fechaNacimiento(LocalDate.of(1990, 1, 1)).build();
    }

    // --- Método Helper para limpiar el código de los tests ---
    private ClienteResponse registrarCliente(ClienteRequest request) throws Exception {
        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(response, ClienteResponse.class);
    }

    /* ===============================
       DELETE
       =============================== */
    @Test
    @DisplayName("DELETE - Debería eliminar el cliente correctamente y retornar 204")
    void deberiaEliminarCliente() throws Exception {

        String response = mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clienteValido)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        ClienteResponse creado =
                objectMapper.readValue(response, ClienteResponse.class);

        mockMvc.perform(delete("/api/clientes/" + creado.getId()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/clientes/" + creado.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE - Debería retornar 404 al intentar eliminar un ID que no existe")
    void noDebeEliminarClienteInexistente() throws Exception {
        long idInexistente = 999L; // Un ID que seguro no existe

        mockMvc.perform(delete("/api/clientes/" + idInexistente))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Cliente no encontrado con id: " + idInexistente));
    }

    @Test
    @DisplayName("Handler - Debería retornar 400 cuando el ID en el Path no es un número válido")
    void deberiaRetornarBadRequestPorParametroInvalido() throws Exception {
        // Dispara MethodArgumentTypeMismatchException
        mockMvc.perform(get("/api/clientes/{id}", "no-long"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    /* ===============================
        EXCEPCIONES - DATA INTEGRITY
       =============================== */
    @Test
    @DisplayName("Handler - Debería capturar errores de integridad (Unique Constraint) de la DB y retornar 409")
    void deberiaCubrirDataIntegrityViolationException() throws Exception {
        mockMvc.perform(get("/api/clientes/force-duplicate"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ya existe un cliente con ese email"));
    }

    @Test
    @DisplayName("Handler - Debería cubrir mensaje de CUIT duplicado en DB")
    void deberiaCubrirMensajeCuitDuplicado() throws Exception {
        mockMvc.perform(get("/api/clientes/force-duplicate-cuit"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Ya existe un cliente con ese CUIT"));
    }

    /* ===============================
       EXCEPCIONES - GENERICA
       =============================== */
    @Test
    @DisplayName("Handler - Debería retornar 500 con un mensaje genérico ante cualquier error inesperado")
    void deberiaCubrirExceptionGenerica() throws Exception {
        mockMvc.perform(get("/api/clientes/throw-exception"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message")
                        .value("Ocurrió un error inesperado. Contacte al administrador"));
    }

    /* ===============================
       PATCH EMAIL TESTS (Versión DTO)
       =============================== */

    @Test
    @DisplayName("PATCH Email - Debería cambiar el email con éxito cuando es válido y no existe en la BD")
    void actualizarEmail_DeberiaTenerExito() throws Exception {
        ClienteResponse creado = registrarCliente(clienteValido);
        // Creamos el objeto DTO en lugar de enviar un String suelto
        EmailUpdateRequest request = new EmailUpdateRequest("nuevo_email@test.com");

        mockMvc.perform(patch("/api/clientes/" + creado.getId() + "/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("nuevo_email@test.com"));
    }

    @Test
    @DisplayName("PATCH Email - Debería retornar 400 cuando el email ya pertenece a otro cliente")
    void actualizarEmail_DeberiaRetornarErrorPorDuplicado() throws Exception {
        registrarCliente(clienteValido);

        ClienteRequest reqB = crearRequest("Maria", "27-44444444-2", "maria@test.com");
        ClienteResponse resB = registrarCliente(reqB);

        EmailUpdateRequest request = new EmailUpdateRequest("juan@gmail.com");

        mockMvc.perform(patch("/api/clientes/" + resB.getId() + "/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("El email ya pertenece a otro cliente"));
    }

    @Test
    @DisplayName("PATCH Email - No debería validar duplicados si el email enviado es el mismo que ya tiene el cliente")
    void actualizarEmail_MismoEmail_DeberiaPasarSinValidar() throws Exception {
        ClienteResponse creado = registrarCliente(clienteValido);
        // Enviamos el mismo email que ya tiene juan@gmail.com
        EmailUpdateRequest request = new EmailUpdateRequest("juan@gmail.com");

        mockMvc.perform(patch("/api/clientes/" + creado.getId() + "/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@gmail.com"));
    }

    @Test
    @DisplayName("PATCH Email - Debería retornar 404 estructurado cuando el ID del cliente no existe")
    void actualizarEmail_DeberiaRetornarNotFound() throws Exception {
        EmailUpdateRequest request = new EmailUpdateRequest("test@test.com");

        mockMvc.perform(patch("/api/clientes/9999/email")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("Handler - Debería cubrir el ELSE de TypeMismatch (Parámetro no Enum)")
    void deberiaCubrirElseTypeMismatch() throws Exception {
        // Intentamos buscar un cliente pasando "abc" en lugar de un ID numérico
        mockMvc.perform(get("/api/clientes/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("Parámetro inválido: id")));
    }

    @Test
    @DisplayName("Handler - Debería cubrir la lista de valores permitidos para Enums")
    void deberiaMostrarValoresPermitidosEnum() throws Exception {
        mockMvc.perform(get("/api/clientes/test-cobertura-enum")
                        .param("estado", "VALOR_INVALIDO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", org.hamcrest.Matchers.containsString("Valores permitidos: [ACTIVO, INACTIVO]")));
    }

}
