package com.intuit.challange.service.abstraction;

import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.dto.ClienteResponse;
import com.intuit.challange.dto.PagedResponse;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ClienteService {
    @Nullable
    ClienteResponse crear ( @Valid ClienteRequest request );

    @Nullable ClienteResponse buscarPorId ( Long id );

    @Nullable ClienteResponse actualizar ( Long id , @Valid ClienteRequest request );

    void eliminar ( Long id );

    ClienteResponse actualizarEmail ( Long id , String nuevoEmail );

    List< ClienteResponse> buscarPorNombre ( String nombre );

    PagedResponse <ClienteResponse> listar ( Pageable pageable );
}
