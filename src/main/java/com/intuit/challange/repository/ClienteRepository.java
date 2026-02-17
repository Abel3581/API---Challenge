package com.intuit.challange.repository;

import com.intuit.challange.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClienteRepository extends JpaRepository< Cliente, Long > {

    boolean existsByCuit(String cuit);

    boolean existsByEmail(String email);

    @Query(value = """
            SELECT *
                FROM buscar_clientes_por_nombre(
                    CAST(:nombre AS text),
                    :limit,
                    :offset
                )
            """, nativeQuery = true)
    List<Cliente> searchByNombreProcedure(
            @Param("nombre") String nombre,
            @Param("limit") int limit,
            @Param("offset") int offset
    );

    @Query(value = """
            SELECT COUNT(*)
            FROM clientes
            WHERE nombre ILIKE CONCAT('%', :nombre, '%')
        """, nativeQuery = true)
    long countByNombre(@Param("nombre") String nombre);


}
