package com.intuit.challange.serviceTest;

import com.intuit.challange.dto.ClienteResponse;
import com.intuit.challange.entity.Cliente;
import com.intuit.challange.mapper.ClienteMapper;
import com.intuit.challange.repository.ClienteRepository;
import com.intuit.challange.service.ClienteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

    /**
     * Se utiliza un Test Unitario para este método debido a que invoca un Stored Procedure
     * nativo de PostgreSQL. Para mantener la suite de tests independiente del motor de DB
     * (H2 en memoria para tests), mockeamos la respuesta del repository validando
     * exclusivamente la lógica del Service y la transformación de la respuesta.
     */
    @Test
    @DisplayName ("Debe llamar al repository y mapear los resultados correctamente")
    void buscarPorNombre_DebeRetornarListaDeResponses() {
        // GIVEN
        String nombreABuscar = "Juan";
        Cliente clienteMock = Cliente.builder().id(1L).nombre("Juan").build();
        ClienteResponse responseMock = new ClienteResponse();
        responseMock.setNombre("Juan");

        // Simulamos que el Repository devuelve el cliente al llamar al SP
        when(repository.searchByNombreProcedure(nombreABuscar)).thenReturn(List.of(clienteMock));
        when(clienteMapper.mapToResponse(any(Cliente.class))).thenReturn(responseMock);

        // WHEN
        List<ClienteResponse> resultado = service.buscarPorNombre(nombreABuscar);

        // THEN
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Juan", resultado.get(0).getNombre());

        // Verificamos que se llamó al método exacto del SP
        verify(repository, times(1)).searchByNombreProcedure(nombreABuscar);
    }

}
