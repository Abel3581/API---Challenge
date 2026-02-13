package com.intuit.challange.controllerTest;


import com.intuit.challange.controller.ClienteController;
import com.intuit.challange.dto.ClienteResponse;
import com.intuit.challange.service.abstraction.ClienteService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ClienteService service;

    @Test
    void deberiaBuscarClientesPorNombre() throws Exception {

        ClienteResponse response = new ClienteResponse();
        response.setNombre("Juan");

        when(service.buscarPorNombre("Juan"))
                .thenReturn(List.of(response));

        mockMvc.perform(get("/api/clientes/search")
                        .param("nombre", "Juan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Juan"));
    }
}
