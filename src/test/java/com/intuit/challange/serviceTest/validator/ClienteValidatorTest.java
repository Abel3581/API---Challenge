package com.intuit.challange.serviceTest.validator;

import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.exception.ArgumentoDuplicadoException;
import com.intuit.challange.repository.ClienteRepository;
import com.intuit.challange.service.validator.ClienteValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith ( MockitoExtension.class)
class ClienteValidatorTest {

    @Mock
    private ClienteRepository repository;

    @InjectMocks
    private ClienteValidator validator;

    @Test
    @DisplayName("validarParaCreacion - OK: cuando no existen duplicados")
    void validarParaCreacion_ok() {
        // GIVEN
        ClienteRequest request = ClienteRequest.builder()
                .cuit("20-12345678-9")
                .email("test@test.com")
                .build();

        when(repository.existsByCuit(request.getCuit())).thenReturn(false);
        when(repository.existsByEmail(request.getEmail())).thenReturn(false);

        // WHEN & THEN
        // No debe lanzar ninguna excepción
        assertDoesNotThrow(() -> validator.validarParaCreacion(request));

        verify(repository).existsByCuit(request.getCuit());
        verify(repository).existsByEmail(request.getEmail());
    }

    @Test
    @DisplayName("validarParaCreacion - Error: lanza excepción por CUIT duplicado")
    void validarParaCreacion_cuitDuplicado() {
        // GIVEN
        ClienteRequest request = ClienteRequest.builder().cuit("20-111").build();
        when(repository.existsByCuit("20-111")).thenReturn(true);

        // WHEN & THEN
        assertThrows(ArgumentoDuplicadoException.class,
                () -> validator.validarParaCreacion(request));

        // Verificamos que el segundo IF ni siquiera se evaluó (ahorro de recursos)
        verify(repository, never()).existsByEmail(anyString());
    }

    @Test
    @DisplayName ("validarParaCreacion - Error: lanza excepción por Email duplicado")
    void validarParaCreacion_emailDuplicado() {
        // GIVEN
        ClienteRequest request = ClienteRequest.builder()
                .cuit("20-222")
                .email("duplicado@test.com")
                .build();

        // El CUIT es nuevo (false), pero el Email ya existe (true)
        when(repository.existsByCuit("20-222")).thenReturn(false);
        when(repository.existsByEmail("duplicado@test.com")).thenReturn(true);

        // WHEN & THEN
        assertThrows(ArgumentoDuplicadoException.class,
                () -> validator.validarParaCreacion(request));
    }
}
