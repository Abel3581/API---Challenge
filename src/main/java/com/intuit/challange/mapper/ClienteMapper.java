package com.intuit.challange.mapper;

import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.dto.ClienteResponse;
import com.intuit.challange.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public ClienteResponse mapToResponse(Cliente c) {

        return ClienteResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .apellido(c.getApellido())
                .razonSocial(c.getRazonSocial())
                .cuit(c.getCuit())
                .fechaNacimiento(c.getFechaNacimiento())
                .telefonoCelular(c.getTelefonoCelular())
                .email(c.getEmail())
                .fechaCreacion(c.getFechaCreacion())
                .fechaModificacion(c.getFechaModificacion())
                .build();
    }

    public Cliente mapToEntity(ClienteRequest request) {

        return Cliente.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .razonSocial(request.getRazonSocial())
                .cuit(request.getCuit())
                .fechaNacimiento(request.getFechaNacimiento())
                .telefonoCelular(request.getTelefonoCelular())
                .email(request.getEmail())
                .build();
    }

    public void updateEntity( Cliente c, ClienteRequest r) {
        c.setNombre(r.getNombre());
        c.setApellido(r.getApellido());
        c.setRazonSocial(r.getRazonSocial());
        c.setCuit(r.getCuit());
        c.setFechaNacimiento(r.getFechaNacimiento());
        c.setTelefonoCelular(r.getTelefonoCelular());
        c.setEmail(r.getEmail());
    }

}
