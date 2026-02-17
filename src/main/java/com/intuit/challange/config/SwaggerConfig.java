package com.intuit.challange.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(apiInfo())
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor local")
                ));
    }

    private Info apiInfo() {
        return new Info()
                .title("Challenge - Cliente API")
                .version("1.0.0")
                .description("""
                        API REST desarrollada como prueba técnica.
                        
                        Funcionalidades:
                        - CRUD completo de clientes
                        - Paginación
                        - Búsqueda por nombre
                        - Validación de unicidad (CUIT / Email)
                        - Manejo global de excepciones
                        """)
                .contact(new Contact()
                        .name("Abel")
                        .url("https://github.com/Abel3581"));
    }
}
