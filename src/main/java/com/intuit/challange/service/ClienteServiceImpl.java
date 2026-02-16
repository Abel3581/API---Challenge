package com.intuit.challange.service;

import com.intuit.challange.dto.ClienteRequest;
import com.intuit.challange.dto.ClienteResponse;
import com.intuit.challange.dto.PagedResponse;
import com.intuit.challange.entity.Cliente;
import com.intuit.challange.exception.ArgumentoDuplicadoException;
import com.intuit.challange.exception.ClienteNotFoundException;
import com.intuit.challange.mapper.ClienteMapper;
import com.intuit.challange.repository.ClienteRepository;
import com.intuit.challange.service.abstraction.ClienteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper clienteMapper;

    @Override
    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        log.info("Iniciando creación de cliente. CUIT: {}", request.getCuit());

        validarUnicidad(request);

        Cliente cliente = clienteMapper.mapToEntity(request);
        Cliente guardado = repository.save(cliente);

        log.info("Cliente creado exitosamente con ID: {}", guardado.getId());
        return clienteMapper.mapToResponse(guardado);
    }

    private void validarUnicidad(ClienteRequest request) {
        if (repository.existsByCuit(request.getCuit())) {
            log.error("Intento de creación con CUIT duplicado: {}", request.getCuit());
            throw new ArgumentoDuplicadoException("Ya existe un cliente con ese CUIT");
        }
        if (repository.existsByEmail(request.getEmail())) {
            log.error("Intento de creación con email duplicado: {}", request.getEmail());
            throw new ArgumentoDuplicadoException("Ya existe un cliente con ese email");
        }
    }

    @Transactional(readOnly = true)
    @Override
    public ClienteResponse buscarPorId(Long id) {

        log.debug("Buscando cliente con ID: {}", id);

        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("Cliente no encontrado con ID: {}", id);
                    return new ClienteNotFoundException(id);
                });

        log.info("Cliente encontrado con ID: {}", id);

        return clienteMapper.mapToResponse(cliente);
    }

    @Override
    @Transactional
    public ClienteResponse actualizar(Long id, ClienteRequest request) {
        log.info("Actualizando cliente con ID: {}", id);

        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new ClienteNotFoundException(id));

        validarDuplicadosUpdate(cliente, request);

        clienteMapper.updateEntity(cliente, request);
        Cliente actualizado = repository.save(cliente);

        log.info("Cliente actualizado correctamente. ID: {}", actualizado.getId());
        return clienteMapper.mapToResponse(actualizado);
    }

    private void validarDuplicadosUpdate(Cliente clienteActual, ClienteRequest request) {

        if (!clienteActual.getCuit().equals(request.getCuit()) &&
                repository.existsByCuit(request.getCuit())) {
            throw new ArgumentoDuplicadoException("El CUIT ya pertenece a otro cliente");
        }

        if (!clienteActual.getEmail().equals(request.getEmail()) &&
                repository.existsByEmail(request.getEmail())) {
            throw new ArgumentoDuplicadoException("El email ya pertenece a otro cliente");
        }
    }

     @Override
     @Transactional
     public ClienteResponse actualizarEmail(Long id, String nuevoEmail) {
         log.info("Actualizando email del cliente ID: {} a {}", id, nuevoEmail);

         Cliente cliente = repository.findById(id)
                 .orElseThrow(() -> new ClienteNotFoundException(id));

         if (!cliente.getEmail().equals(nuevoEmail) && repository.existsByEmail(nuevoEmail)) {
             log.error("Email duplicado detectado: {}", nuevoEmail);
             throw new ArgumentoDuplicadoException("El email ya pertenece a otro cliente");
         }

         cliente.setEmail(nuevoEmail);
         Cliente actualizado = repository.save(cliente);

         return clienteMapper.mapToResponse(actualizado);
     }

    @Override
    @Transactional
    public void eliminar(Long id) {
        log.info("Iniciando proceso de eliminación para el cliente ID: {}", id);

        // Buscamos y ejecutamos la acción en una sola cadena lógica
        repository.findById(id)
                .ifPresentOrElse(
                        cliente -> {
                            repository.delete(cliente);
                            log.info("Cliente eliminado exitosamente. ID: {}", id);
                        },
                        () -> {
                            log.error("Fallo al eliminar: Cliente no encontrado con ID: {}", id);
                            throw new ClienteNotFoundException(id);
                        }
                );
    }

    @Transactional(readOnly = true)
    @Override
    public List<ClienteResponse> buscarPorNombre(String nombre) {
        // 1. Validación Fail-Fast (Evita llamadas innecesarias a la DB)
        if (nombre == null || nombre.trim().isEmpty()) {
            log.warn("Búsqueda abortada: El parámetro 'nombre' está vacío");
            return Collections.emptyList();
        }

        String nombreBusqueda = nombre.trim();
        log.info("Ejecutando búsqueda por nombre vía Stored Procedure: '{}'", nombreBusqueda);

        // 2. Ejecución y procesamiento defensivo
        List<Cliente> clientes = repository.searchByNombreProcedure(nombreBusqueda);

        if (clientes == null || clientes.isEmpty()) {
            log.info("No se encontraron clientes que coincidan con: '{}'", nombreBusqueda);
            return Collections.emptyList();
        }

        // 3. Transformación eficiente
        return clientes.stream()
                .map(clienteMapper::mapToResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public PagedResponse<ClienteResponse> listar(Pageable pageable) {
        log.info("Solicitud de listado de clientes - Página: {}, Tamaño: {}, Orden: {}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());

        Page<Cliente> page = repository.findAll(pageable);

        if (pageable.getPageNumber() >= page.getTotalPages() && page.getTotalElements() > 0) {
            log.error("Se solicitó una página inexistente: {} de un total de {}",
                    pageable.getPageNumber(), page.getTotalPages());
        }

        List<ClienteResponse> contenido = page.getContent().stream()
                .map(clienteMapper::mapToResponse)
                .toList();

        log.info("Listado completado. Se encontraron {} elementos en esta página. Total global: {}",
                contenido.size(), page.getTotalElements());

        return PagedResponse.<ClienteResponse>builder()
                .content(contenido)
                .page(PagedResponse.PageMetadata.builder()
                        .size(page.getSize())
                        .totalElements(page.getTotalElements())
                        .totalPages(page.getTotalPages())
                        .number(page.getNumber())
                        .build())
                .build();
    }


}
