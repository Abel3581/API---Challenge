package com.intuit.challange.controller;


import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.dto.ClienteResponse;
import com.intuit.challange.dto.EmailUpdateRequest;
import com.intuit.challange.dto.PagedResponse;
import com.intuit.challange.service.abstraction.ClienteService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith ( MockitoExtension.class)
class ClienteControllerTest {

    @Mock
    private ClienteService service;

    @InjectMocks
    private ClienteController controller;

    // ==========================
    // CREAR
    // ==========================

    @Test
    void crear_debeRetornar201() {

        ClienteRequest request = new ClienteRequest();
        ClienteResponse response = new ClienteResponse();

        when(service.crear(request)).thenReturn(response);

        ResponseEntity <ClienteResponse> result = controller.crear(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    // ==========================
    // LISTAR
    // ==========================

    @Test
    void listar_debeRetornar200() {

        Pageable pageable = PageRequest.of(0, 10);
        PagedResponse <ClienteResponse> paged = PagedResponse.<ClienteResponse>builder().build();

        when(service.listar(pageable)).thenReturn(paged);

        ResponseEntity<PagedResponse<ClienteResponse>> result = controller.listar(pageable);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(paged, result.getBody());
    }

    // ==========================
    // BUSCAR POR ID
    // ==========================

    @Test
    void buscarPorId_debeRetornar200() {

        ClienteResponse response = new ClienteResponse();

        when(service.buscarPorId(1L)).thenReturn(response);

        ResponseEntity<ClienteResponse> result = controller.buscarPorId(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    // ==========================
    // ACTUALIZAR
    // ==========================

    @Test
    void actualizar_debeRetornar200() {

        ClienteRequest request = new ClienteRequest();
        ClienteResponse response = new ClienteResponse();

        when(service.actualizar(1L, request)).thenReturn(response);

        ResponseEntity<ClienteResponse> result = controller.actualizar(1L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    // ==========================
    // ACTUALIZAR EMAIL
    // ==========================

    @Test
    void actualizarEmail_debeRetornar200() {

        EmailUpdateRequest request = new EmailUpdateRequest("test@mail.com");
        ClienteResponse response = new ClienteResponse();

        when(service.actualizarEmail(1L, "test@mail.com")).thenReturn(response);

        ResponseEntity<ClienteResponse> result = controller.actualizarEmail(1L, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(response, result.getBody());
    }

    // ==========================
    // ELIMINAR
    // ==========================

    @Test
    void eliminar_debeRetornar204() {

        ResponseEntity<Void> result = controller.eliminar(1L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(service).eliminar(1L);
    }

    // ==========================
    // BUSCAR POR NOMBRE
    // ==========================

    @Test
    void buscarPorNombre_debeRetornar200() {

        List <ClienteResponse> lista = List.of(new ClienteResponse());

        when(service.buscarPorNombre("Juan")).thenReturn(lista);

        ResponseEntity<List<ClienteResponse>> result =
                controller.buscarPorNombre("Juan");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(lista, result.getBody());
    }

    // ==========================
    // ENDPOINTS OCULTOS (EXCEPCIONES)
    // ==========================

    @Test
    void forceDuplicate_debeLanzarException() {
        assertThrows(Exception.class, () -> controller.forceDuplicate());
    }

    @Test
    void forceDuplicateCuit_debeLanzarException() {
        assertThrows(Exception.class, () -> controller.forceDuplicateCuit());
    }

    @Test
    void throwException_debeLanzarException() {
        assertThrows(Exception.class, () -> controller.throwException());
    }
}

