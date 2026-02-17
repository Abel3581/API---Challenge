## CHALLENGE Intuit/Yappa


API REST para la gestión integral de clientes, desarrollada con Java 17 y Spring Boot 3. Este proyecto destaca por un enfoque en Clean Code, alta cobertura de tests y optimización de base de datos.

📊 Calidad de Código (SonarQube)

El proyecto ha sido sometido a un riguroso análisis estático, alcanzando la excelencia en métricas de mantenibilidad y robustez.
<p align="center">
<img src="https://res.cloudinary.com/dlv9gwnw3/image/upload/v1771261519/passed-img_ugcgh4.png" alt="SonarQube Analysis" width="800">
</p>
    Coverage: 100% (Toda la lógica de negocio, manejo de excepciones, mappers, dto, controller, entity están testeados).

    Code Smells: 0 (Código limpio, siguiendo principios SOLID).

    Vulnerabilidades: 0.

    Complejidad Cognitiva: 16 (Altamente legible y mantenible para el equipo).

🛠️ Stack Tecnológico

    Core: Java 17 & Spring Boot 3.x.

    Persistencia: Spring Data JPA con PostgreSQL 15 (Producción).

    Mapping: Mapeo de DTOs mediante lógica personalizada para control total de la exposición de datos.

    Testing: JUnit 5, Mockito y MockMvc.

    Documentación: Swagger / OpenAPI 3.

    Infraestructura: Docker & Docker Compose.

🔍 Características Destacadas

⚡ Búsqueda Optimizada (Stored Procedure)

Para maximizar la performance, la búsqueda por nombre se realiza mediante un Stored Procedure nativo en PostgreSQL.

    Lógica: Utiliza el operador ILIKE para búsquedas parciales e insensibles a mayúsculas.

    Inicialización: El esquema y el procedimiento se crean automáticamente mediante schema-postgre.sql al iniciar el contenedor.

🧪 Estrategia de Testing (100% Coverage)

Se ha implementado una suite de Tests Unitarios que garantiza la estabilidad total del sistema.

    MockMvc: Validamos el ciclo de vida de las peticiones HTTP y el GlobalExceptionHandler.

    Lógica de Negocio: Cobertura total en servicios, validaciones de CUIT/Email y auditoría de entidades JPA.

    Independencia: Los tests utilizan base de datos H2 en memoria para mayor velocidad en pipelines de CI/CD.

🛡️ Manejo Global de Excepciones

Implementación de @RestControllerAdvice que estandariza las respuestas de error (400, 404, 409, 500), proporcionando mensajes claros y precisos al consumidor de la API.

🐳 Despliegue Rápido (Docker)

    Configurar Variables: Crea un archivo .env en la raíz con las credenciales de base de datos.
    En challange.env esta el archivo de configuracion de ejemplo.
    Levantar Entorno:

Bash

docker-compose up --build -d

    API: http://localhost:8080

    Swagger UI: http://localhost:8080/swagger-ui/index.html

🔌 Endpoints Principales

    GET	/api/clientes	Listado paginado de clientes.
    GET /api/clientes/search?nombre=&page=&size= Búsqueda paginada vía Stored Function
    POST	/api/clientes	Registro (Valida CUIT/Email únicos).
    PUT	/api/clientes/{id}	Actualización completa de datos.
    PATCH	/api/clientes/{id}/email	Actualización específica de contacto.
    DELETE	/api/clientes/{id}	Borrado físico del registro.

🔍 Características Destacadas

⚡ Búsqueda Optimizada (Stored Function + Paginación)

    La búsqueda por nombre se implementa mediante una Stored Function nativa en PostgreSQL, optimizada para performance y escalabilidad.

✔ Características:

    Uso de ILIKE para búsquedas parciales e insensibles a mayúsculas.
    
    Implementación con LIMIT y OFFSET para soportar paginación real desde base de datos.
    
    Evita traer registros innecesarios a memoria.
    
    Integrada con Spring Data JPA mediante native query.

📌 Endpoint:

    GET /api/clientes/search?nombre={valor}&page={n}&size={m}

    nombre → criterio de búsqueda
    
    page → número de página (base 0)
    
    size → cantidad de registros por página
    
    Esto permite búsquedas eficientes incluso con grandes volúmenes de datos.

🔍 Análisis de Calidad Local (SonarQube)

    Para replicar el análisis de calidad y visualizar el reporte detallado en tu máquina, sigue estos pasos:
    1. Levantar el servidor de SonarQube

    Ejecuta el siguiente comando para iniciar una instancia comunitaria en Docker:
    Bash

    docker run -d --name sonarqube -p 9000:9000 sonarqube:community

    2. Acceder al Panel

    Entra a http://localhost:9000 (Credenciales por defecto: admin / admin).

    Crea un proyecto manualmente llamado "test" y genera un Token de Proyecto.

    3. Ejecutar el Scanner de Maven

    Desde la raíz del proyecto, ejecuta el siguiente comando (reemplazando tu token):
    Bash
    
    mvn clean verify sonar:sonar \
    "-Dsonar.projectKey=test" \
    "-Dsonar.host.url=http://localhost:9000" \
    "-Dsonar.token=TU_TOKEN_AQUI" \
    "-Dsonar.scm.disabled=true"

📈 Auditoría y Logs

Se utiliza Logback con una estrategia de rotación diaria para facilitar el monitoreo:

    logs/app.log: Registro general de todas las operaciones exitosas y flujo del sistema.

    logs/errors.log: Filtrado exclusivo de eventos críticos (ERROR) para auditoría rápida y diagnóstico de fallos.

🏗️ Arquitectura y Patrones Aplicados

📐 Arquitectura General

El proyecto está desarrollado siguiendo una Arquitectura en Capas (Layered Architecture), promoviendo separación de responsabilidades, bajo acoplamiento y alta cohesión.

Estructura principal:

    controller → service → repository → database

    Cada capa cumple una responsabilidad específica:

    Controller → Manejo de requests HTTP
    
    Service → Lógica de negocio
    
    Repository → Acceso a datos
    
    Entity → Modelo de persistencia
    
    DTO → Modelo de transferencia de datos
    
    Mapper → Conversión entre Entity y DTO
    
    Exception → Manejo centralizado de errores
    
    Config → Configuración técnica del framework

La aplicación está construida con Spring Boot utilizando Spring Data JPA como capa de persistencia y Hibernate como proveedor ORM.

🧠 Patrones de Diseño Implementados

    ✔ 1. Layered Architecture
    
    Separación clara en capas independientes que permite:
    
    Escalabilidad
    
    Testeo aislado
    
    Mantenibilidad
    
    ✔ 2. Repository Pattern
    
    Abstracción del acceso a datos mediante interfaces que desacoplan la lógica de negocio de la persistencia.
    
    ✔ 3. Service Layer Pattern
    
    La lógica de negocio se centraliza en la capa de servicios, evitando lógica en los controladores.
    
    ✔ 4. DTO (Data Transfer Object)
    
    Se utilizan DTOs para:
    
    No exponer entidades directamente
    
    Controlar la información que se devuelve al cliente
    
    Desacoplar modelo de persistencia del modelo de API
    
    ✔ 5. Mapper Pattern
    
    Conversión explícita entre Entity y DTO, asegurando separación entre dominio y transporte de datos.
    
    ✔ 6. Dependency Injection (IoC)
    
    Implementado mediante el contenedor de inversión de control de Spring, promoviendo bajo acoplamiento y facilitando el testing.
    
    ✔ 7. Interface + Implementation Separation
    
    Se define una interfaz en service.abstraction y su implementación concreta en service, aplicando el principio de inversión de dependencias (SOLID).
    
    ✔ 8. Global Exception Handling

Manejo centralizado de errores mediante @ControllerAdvice, garantizando:

    Respuestas HTTP consistentes
    
    Mensajes de error estructurados
    
    Mejor experiencia para consumidores del API