package com.intuit.challange.service.validator;

import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.exception.ArgumentoDuplicadoException;
import com.intuit.challange.repository.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteValidator {
    private final ClienteRepository repository;

    public void validarParaCreacion( ClienteRequest request) {
        if (repository.existsByCuit(request.getCuit())) {
            throw new ArgumentoDuplicadoException("Ya existe un cliente con ese CUIT");
        }
        if (repository.existsByEmail(request.getEmail())) {
            throw new ArgumentoDuplicadoException("Ya existe un cliente con ese email");
        }
    }

}