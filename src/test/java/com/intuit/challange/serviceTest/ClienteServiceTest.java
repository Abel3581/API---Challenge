package com.intuit.challange.serviceTest;

import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.dto.ClienteResponse;
import com.intuit.challange.dto.PagedResponse;
import com.intuit.challange.entity.Cliente;
import com.intuit.challange.exception.ArgumentoDuplicadoException;
import com.intuit.challange.exception.ClienteNotFoundException;
import com.intuit.challange.mapper.ClienteMapper;
import com.intuit.challange.repository.ClienteRepository;
import com.intuit.challange.service.ClienteServiceImpl;
import com.intuit.challange.service.validator.ClienteValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith ( MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private ClienteMapper clienteMapper;

    @InjectMocks
    private ClienteServiceImpl service;

    @Mock
    private ClienteValidator clienteValidator;

    // =====================================================
    // CREATE
    // =====================================================

    @Test
    @DisplayName("crear - debe guardar correctamente cuando no hay duplicados")
    void crear_ok() {
        // GIVEN
        ClienteRequest request = this.crearRequest();
        Cliente entity = new Cliente();
        Cliente guardado = new Cliente();
        guardado.setId(1L);

        ClienteResponse response = new ClienteResponse();
        response.setNombre("Juan");

        // No necesitamos mockear el repository.exists porque el Service ya no lo llama
        // Solo verificamos que el mapper y el save funcionen
        when(clienteMapper.mapToEntity(request)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(guardado);
        when(clienteMapper.mapToResponse(guardado)).thenReturn(response);

        // WHEN
        ClienteResponse resultado = service.crear(request);

        // THEN
        assertNotNull(resultado);
        assertEquals("Juan", resultado.getNombre());

        // Verificamos que se llamó al validador una vez
        verify(clienteValidator).validarParaCreacion(request);
        verify(repository).save(entity);
    }

    @Test
    @DisplayName("crear - debe lanzar excepción si el validador falla")
    void crear_error_validacion() {
        // GIVEN
        ClienteRequest request = crearRequest();

        // Simulamos que el VALIDADOR lanza la excepción
        doThrow(new ArgumentoDuplicadoException("Ya existe un cliente con ese CUIT"))
                .when(clienteValidator).validarParaCreacion(request);

        // WHEN & THEN
        assertThrows(ArgumentoDuplicadoException.class, () -> service.crear(request));

        // Verificamos que el proceso se detuvo y NUNCA se llamó al save
        verify(repository, never()).save(any());
    }
/*
    @Test
    @DisplayName("crear - debe lanzar excepción si CUIT duplicado")
    void crear_cuitDuplicado() {

        ClienteRequest request = crearRequest();

        when(repository.existsByCuit(request.getCuit())).thenReturn(true);

        assertThrows(ArgumentoDuplicadoException.class,
                () -> service.crear(request));
    }

    @Test
    @DisplayName("crear - debe lanzar excepción si email duplicado")
    void crear_emailDuplicado() {

        ClienteRequest request = crearRequest();

        when(repository.existsByCuit(request.getCuit())).thenReturn(false);
        when(repository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(ArgumentoDuplicadoException.class,
                () -> service.crear(request));
    }

 */

    // =====================================================
    // BUSCAR POR ID
    // =====================================================

    @Test
    @DisplayName("buscarPorId - debe retornar cliente existente")
    void buscarPorId_ok() {

        Cliente cliente = new Cliente();
        ClienteResponse response = new ClienteResponse();

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(clienteMapper.mapToResponse(cliente)).thenReturn(response);

        ClienteResponse resultado = service.buscarPorId(1L);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("buscarPorId - debe lanzar excepción si no existe")
    void buscarPorId_notFound() {

        when(repository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ClienteNotFoundException.class,
                () -> service.buscarPorId(1L));
    }

    // =====================================================
    // ACTUALIZAR
    // =====================================================

    @Test
    @DisplayName("actualizar - debe actualizar correctamente")
    void actualizar_ok() {

        ClienteRequest request = crearRequest();
        Cliente cliente = new Cliente();
        cliente.setCuit(request.getCuit());
        cliente.setEmail(request.getEmail());

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.mapToResponse(cliente)).thenReturn(new ClienteResponse());

        ClienteResponse resultado = service.actualizar(1L, request);

        assertNotNull(resultado);
        verify(clienteMapper).updateEntity(cliente, request);
    }

    @Test
    @DisplayName("actualizar - debe lanzar excepción si cambia CUIT y ya existe")
    void actualizar_cuitDuplicado() {

        ClienteRequest request = crearRequest();
        Cliente cliente = new Cliente();
        cliente.setCuit("viejo");
        cliente.setEmail(request.getEmail());

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.existsByCuit(request.getCuit())).thenReturn(true);

        assertThrows(ArgumentoDuplicadoException.class,
                () -> service.actualizar(1L, request));
    }

    @Test
    @DisplayName("actualizar - debe lanzar excepción si cambia email y ya existe")
    void actualizar_emailDuplicado() {

        ClienteRequest request = crearRequest();
        Cliente cliente = new Cliente();
        cliente.setCuit(request.getCuit());
        cliente.setEmail("viejo@mail.com");

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(ArgumentoDuplicadoException.class,
                () -> service.actualizar(1L, request));
    }

    @Test
    @DisplayName("actualizar - mismo email no debe validar existencia")
    void actualizar_mismoEmail_noValidaExistencia() {

        ClienteRequest request = crearRequest();

        Cliente cliente = new Cliente();
        cliente.setCuit(request.getCuit());
        cliente.setEmail(request.getEmail()); // mismo email

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.mapToResponse(cliente)).thenReturn(new ClienteResponse());

        service.actualizar(1L, request);

        //Esto es CLAVE para branch coverage
        verify(repository, never()).existsByEmail(any());
    }


    @Test
    @DisplayName("actualizar - cambia CUIT pero no existe en BD, debe permitir")
    void actualizar_cuitNuevoNoExistente() {

        ClienteRequest request = crearRequest();
        request.setCuit("27-99999999-1");

        Cliente cliente = new Cliente();
        cliente.setCuit("20-30123456-7");
        cliente.setEmail(request.getEmail());

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.existsByCuit(request.getCuit())).thenReturn(false);
        when(repository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.mapToResponse(cliente)).thenReturn(new ClienteResponse());

        ClienteResponse resultado = service.actualizar(1L, request);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("actualizar - mismo CUIT no debe validar existencia")
    void actualizar_mismoCuit_noValida() {

        ClienteRequest request = crearRequest();

        Cliente cliente = new Cliente();
        cliente.setCuit(request.getCuit());
        cliente.setEmail(request.getEmail());

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.mapToResponse(cliente)).thenReturn(new ClienteResponse());

        service.actualizar(1L, request);

        verify(repository, never()).existsByCuit(any());
    }


    // =====================================================
    // ACTUALIZAR EMAIL
    // =====================================================

    @Test
    @DisplayName("actualizarEmail - debe actualizar correctamente")
    void actualizarEmail_ok() {

        Cliente cliente = new Cliente();
        cliente.setEmail("viejo@mail.com");

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.existsByEmail("nuevo@mail.com")).thenReturn(false);
        when(repository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.mapToResponse(cliente)).thenReturn(new ClienteResponse());

        ClienteResponse resultado = service.actualizarEmail(1L, "nuevo@mail.com");

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("actualizarEmail - debe lanzar excepción si email ya existe")
    void actualizarEmail_duplicado() {

        Cliente cliente = new Cliente();
        cliente.setEmail("viejo@mail.com");

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.existsByEmail("duplicado@mail.com")).thenReturn(true);

        assertThrows(ArgumentoDuplicadoException.class,
                () -> service.actualizarEmail(1L, "duplicado@mail.com"));
    }

    @Test
    @DisplayName("actualizarEmail - mismo email no debe validar duplicado")
    void actualizarEmail_mismoEmail() {

        Cliente cliente = new Cliente();
        cliente.setEmail("test@mail.com");

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.mapToResponse(cliente)).thenReturn(new ClienteResponse());

        service.actualizarEmail(1L, "test@mail.com");

        verify(repository, never()).existsByEmail(any());
    }

    @Test
    @DisplayName("actualizar - email distinto pero no existe en BD, debe permitir")
    void actualizar_emailNuevoNoExistente() {

        ClienteRequest request = crearRequest();
        request.setEmail("nuevo@email.com");

        Cliente cliente = new Cliente();
        cliente.setCuit(request.getCuit());
        cliente.setEmail("viejo@email.com");

        when(repository.findById(1L)).thenReturn(Optional.of(cliente));
        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(repository.save(cliente)).thenReturn(cliente);
        when(clienteMapper.mapToResponse(cliente)).thenReturn(new ClienteResponse());

        ClienteResponse resultado = service.actualizar(1L, request);

        assertNotNull(resultado);
    }


    // =====================================================
    // ELIMINAR
    // =====================================================

    @Test
    @DisplayName("eliminar - debe borrar si existe")
    void eliminar_success() {
        // GIVEN
        Cliente cliente = new Cliente();
        cliente.setId(1L);
        when(repository.findById(1L)).thenReturn(Optional.of(cliente));

        // WHEN
        service.eliminar(1L);

        // THEN
        verify(repository, times(1)).delete(cliente);
    }

    @Test
    @DisplayName("eliminar - debe lanzar excepción si no existe")
    void eliminar_notFound() {
        // GIVEN: El repositorio ahora usa findById, por lo que mockeamos un Optional vacío
        when(repository.findById(1L)).thenReturn(Optional.empty());

        // WHEN & THEN
        assertThrows(ClienteNotFoundException.class,
                () -> service.eliminar(1L));

        // Verificación extra (Nivel Senior):
        // Aseguramos que NUNCA se intentó borrar si no se encontró el cliente
        verify(repository, never()).delete(any());
    }

    // =====================================================
    // LISTAR (CORRECTO SEGÚN TU SERVICE)
    // =====================================================

    @Test
    @DisplayName("listar - debe retornar PagedResponse correctamente mapeado")
    void listar_ok() {

        Pageable pageable = PageRequest.of(0, 10);
        Cliente cliente = new Cliente();
        Page<Cliente> page = new PageImpl<>(List.of(cliente), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);
        when(clienteMapper.mapToResponse(cliente)).thenReturn(new ClienteResponse());

        PagedResponse <ClienteResponse> resultado = service.listar(pageable);

        assertEquals(1, resultado.getContent().size());
        assertEquals(1, resultado.getPage().getTotalElements());
        assertEquals(1, resultado.getPage().getTotalPages());
        assertEquals(0, resultado.getPage().getNumber());
    }

    @Test
    @DisplayName("listar - debe cubrir rama cuando página solicitada es mayor al total")
    void listar_paginaInexistente() {

        Pageable pageable = PageRequest.of(99, 10);
        Page<Cliente> page = new PageImpl<>(List.of(), pageable, 1);

        when(repository.findAll(pageable)).thenReturn(page);

        PagedResponse<ClienteResponse> resultado = service.listar(pageable);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("listar - debe cubrir rama TRUE del if interno")
    void listar_cubreIfVerdadero() {

        Pageable pageable = PageRequest.of(5, 10);

        // totalPages = 1 → pageNumber >= totalPages
        Page<Cliente> page = new PageImpl<>(
                List.of(new Cliente()),
                pageable,
                1
        );

        when(repository.findAll(pageable)).thenReturn(page);
        when(clienteMapper.mapToResponse(any())).thenReturn(new ClienteResponse());

        PagedResponse<ClienteResponse> resultado = service.listar(pageable);

        assertNotNull(resultado);
    }

    @Test
    @DisplayName("listar - pageNumber mayor pero totalElements 0")
    void listar_totalElementsCero() {

        Pageable pageable = PageRequest.of(5, 10);

        Page<Cliente> page = new PageImpl<>(
                List.of(),
                pageable,
                0
        );

        when(repository.findAll(pageable)).thenReturn(page);

        PagedResponse<ClienteResponse> resultado = service.listar(pageable);

        assertTrue(resultado.getContent().isEmpty());
    }


    // =====================================================
    // STORED PROCEDURE
    // =====================================================
    @Test
    @DisplayName("buscarPorNombre - debe limpiar el nombre y retornar lista de resultados")
    void buscarPorNombre_ok() {
        // GIVEN: Un nombre con espacios para probar el trim()
        String nombreSucio = "  Juan  ";
        String nombreLimpio = "Juan";

        Cliente cliente = Cliente.builder().id(1L).nombre(nombreLimpio).build();
        ClienteResponse response = new ClienteResponse();

        when(repository.searchByNombreProcedure(nombreLimpio))
                .thenReturn(List.of(cliente));

        when(clienteMapper.mapToResponse(cliente))
                .thenReturn(response);

        // WHEN
        List<ClienteResponse> resultado = service.buscarPorNombre(nombreSucio);

        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());

        // Verificamos que el service hizo el trim() antes de llamar al repositorio
        verify(repository).searchByNombreProcedure(nombreLimpio);
        verify(repository, times(1)).searchByNombreProcedure(anyString());
    }

    @Test
    @DisplayName("buscarPorNombre - debe retornar lista vacía si el nombre es nulo o vacío")
    void buscarPorNombre_empty() {
        // Probamos el comportamiento "Fail-Fast" sin tocar la DB
        List<ClienteResponse> resultadoNull = service.buscarPorNombre(null);
        List<ClienteResponse> resultadoVacio = service.buscarPorNombre("   ");

        assertTrue(resultadoNull.isEmpty());
        assertTrue(resultadoVacio.isEmpty());

        // Verificamos que NUNCA se llamó al repositorio (ahorro de recursos)
        verify(repository, never()).searchByNombreProcedure(anyString());
    }

    @Test
    @DisplayName("buscarPorNombre - Debe retornar lista vacía cuando el SP no encuentra nada")
    void buscarPorNombre_cuandoNoHayResultados() {
        // GIVEN
        String nombre = "Inexistente";
        // Simulamos que el repositorio devuelve una lista vacía (no null)
        when(repository.searchByNombreProcedure(nombre)).thenReturn(Collections.emptyList());

        // WHEN
        List<ClienteResponse> resultado = service.buscarPorNombre(nombre);

        // THEN
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        // Verificamos que se ejecutó la lógica hasta el log de "No se encontraron clientes"
        verify(repository).searchByNombreProcedure(nombre);
    }

    @Test
    @DisplayName("buscarPorNombre - Debe manejar correctamente si el repositorio devuelve null")
    void buscarPorNombre_cuandoRepositorioDevuelveNull() {
        // GIVEN
        String nombre = "ErrorDB";
        // Forzamos explícitamente que el repositorio devuelva null
        when(repository.searchByNombreProcedure(nombre)).thenReturn(null);

        // WHEN
        List<ClienteResponse> resultado = service.buscarPorNombre(nombre);

        // THEN
        // El service no debe devolver null, sino Collections.emptyList()
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(repository).searchByNombreProcedure(nombre);
    }

    // =====================================================
    // HELPER
    // =====================================================

    private ClienteRequest crearRequest() {
        return ClienteRequest.builder()
                .nombre("Juan")
                .apellido("Perez")
                .razonSocial("JP Servicios")
                .cuit("20-30123456-7")
                .email("juan@test.com")
                .telefonoCelular("12345678")
                .fechaNacimiento(LocalDate.of(1990, 1, 1))
                .build();
    }

}
