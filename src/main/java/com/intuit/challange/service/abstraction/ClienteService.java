package com.intuit.challange.service.abstraction;

import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.dto.ClienteResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

public interface ClienteService {
    @Nullable
    ClienteResponse crear ( @Valid ClienteRequest request );

    @Nullable List< ClienteResponse> listar ();

    @Nullable ClienteResponse buscarPorId ( Long id );

    @Nullable ClienteResponse actualizar ( Long id , @Valid ClienteRequest request );

    void eliminar ( Long id );

    ClienteResponse actualizarEmail ( Long id , String nuevoEmail );
}
