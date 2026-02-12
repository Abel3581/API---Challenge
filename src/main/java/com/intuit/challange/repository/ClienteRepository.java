package com.intuit.challange.repository;

import com.intuit.challange.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository< Cliente, Long > {

    boolean existsByCuit(String cuit);

    boolean existsByEmail(String email);
}
