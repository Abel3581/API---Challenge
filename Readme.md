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

    Levantar Entorno:

Bash

docker-compose up --build -d

    API: http://localhost:8080

    Swagger UI: http://localhost:8080/swagger-ui/index.html

🔌 Endpoints Principales

    GET	/api/clientes	Listado paginado de clientes.
    GET	/api/clientes/search?nombre={v}	Búsqueda avanzada vía Stored Procedure.
    POST	/api/clientes	Registro (Valida CUIT/Email únicos).
    PUT	/api/clientes/{id}	Actualización completa de datos.
    PATCH	/api/clientes/{id}/email	Actualización específica de contacto.
    DELETE	/api/clientes/{id}	Borrado físico del registro.

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