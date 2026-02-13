🚀 Cliente Management API - Challenge

Esta es una API REST para la gestión de clientes desarrollada en Java con Spring Boot 3. El proyecto implementa un CRUD completo con validaciones de negocio avanzadas y está preparado para desplegarse mediante Docker.
🛠️ Tecnologías Usadas

    Java 17 & Spring Boot 3.x

    Spring Data JPA: Persistencia de datos.

    PostgreSQL 15: Base de datos relacional para producción/desarrollo.

    H2 Database: Base de datos en memoria para ejecución de tests (rápida e independiente).

    Mappers: Mapeo de Entidades a DTOs Manualmente.

    Lombok: Reducción de código repetitivo.

    JUnit 5 & MockMvc: Pruebas de integración con cobertura del 100% en lógica crítica.

    Docker & Docker Compose: Contenedorización de la app y la base de datos.


📋 Variables de Entorno (.env)

El proyecto utiliza un archivo .env para configurar la conexión a la base de datos y el servidor. El archivo docker-compose.yml carga automáticamente estos valores.
Ini, TOML

# DATABASE CONFIG
### ✅ Configuralos en el archivo .env
DB_HOST=localhost

DB_PORT=5432

DB_NAME={nombreBd}

DB_USERNAME={tuUsernameBd}

DB_PASSWORD={tuPassword}

# JPA / HIBERNATE
HIBERNATE_DDL=update
SHOW_SQL=true

# SERVER
SERVER_PORT=8080

🐳 Despliegue con Docker Compose

Para levantar la API junto con el contenedor de PostgreSQL, simplemente ejecutá:
Bash

docker-compose up -d o docker-compose up --build

Detalles del despliegue:

    API: Disponible en http://localhost:8080 (o el puerto configurado en SERVER_PORT).

    PostgreSQL: Corre internamente en el puerto 5432, pero se expone al host según DB_PORT.

    Persistencia: La base de datos se crea automáticamente con el nombre definido en DB_NAME.

## 🔍 Búsqueda Avanzada (Stored Procedure)

Para cumplir con los requerimientos técnicos de performance y lógica de base de datos, la funcionalidad de **Búsqueda por Nombre** se implementó mediante un **Stored Procedure nativo en PostgreSQL**.

* **Lógica:** Realiza una búsqueda por caracteres centrales utilizando el operador `ILIKE` para asegurar que la búsqueda sea insensible a mayúsculas y minúsculas.
* **Script de Carga:** El procedimiento se crea automáticamente al iniciar la aplicación mediante el script `schema-postgre.sql`, el cual incluye una lógica "inteligente" de inicialización (`CREATE TABLE IF NOT EXISTS` e `INSERT ... ON CONFLICT`), garantizando que los datos de prueba se carguen solo si la base de datos está vacía.

## 🧪 Estrategia de Testing

El proyecto aplica una **pirámide de pruebas** equilibrada para garantizar la estabilidad:

1.  **Tests de Integración (MockMvc + H2):** Se utilizan para validar el flujo completo del CRUD, el manejo de transacciones y la respuesta de los Endpoints. Se configuran con una base de datos **H2 en memoria** para asegurar portabilidad y rapidez.
2.  **Tests Unitarios (Mockito):** Se aplican específicamente para la lógica del **Stored Procedure**.

> **Nota técnica sobre el Testing del Procedure:** > Dado que el Stored Procedure utiliza sintaxis nativa de PostgreSQL (`plpgsql`), la cual no es compatible con H2, se optó por un **Test Unitario en la capa de Servicio**. Esto permite validar que la aplicación interactúa correctamente con el contrato del Repository y procesa los resultados adecuadamente, manteniendo la suite de tests independiente del motor de base de datos de producción.

🔌 Endpoints Principales

    GET /api/clientes: Lista todos los registros paginados.

    POST /api/clientes: Crea un cliente (valida CUIT/Email duplicados).

    PATCH /api/clientes/{id}/email: Actualización específica del email.

    PUT /api/clientes/{id}: Actualización completa del cliente.

    DELETE /api/clientes/{id}: Borrado físico del registro.
    
    GET /api/clientes/search?nombre={valor}:** Búsqueda por nombre (implementado vía Stored Procedure).

📊 Calidad de Código (SonarQube)

El proyecto integra SonarQube para el análisis estático de código, asegurando el cumplimiento de los estándares de la industria en cuanto a mantenibilidad, confiabilidad y seguridad.
Métricas Alcanzadas:

    Cobertura de Tests: > 100% (Superando el umbral estándar del 80%).

    Code Smells: 0 (Código limpio, sin duplicaciones ni lógicas redundantes).

    Security Hotspots: Revisados y mitigados (Garantizando el manejo seguro de logs y excepciones).

    Vulnerabilidades: 0.

Cómo ejecutar el análisis de calidad:

Para replicar el análisis de calidad en un entorno local, sigue estos pasos:

    Levantar el servidor de SonarQube:
    Bash

    docker run -d --name sonarqube -p 9000:9000 sonarqube:community

    Acceder al Panel:
    Entra a http://localhost:9000 (User/Pass: admin/admin) y genera un Token de proyecto.

    Ejecutar el Scanner de Maven:
    Ejecuta el siguiente comando en la raíz del proyecto (reemplazando tu token):
    Bash

    mvn clean verify sonar:sonar \
      "-Dsonar.projectKey=test" \
      "-Dsonar.host.url=http://localhost:9000" \
      "-Dsonar.token=TU_TOKEN_AQUI" \
      "-Dsonar.scm.disabled=true"

🛠️ Registro y Auditoría de Errores

Se implementó una estrategia de Logging Persistente utilizando Logback con las siguientes características:

    Estrategia de Rotación: Los logs se almacenan en archivos físicos con rotación diaria y una retención de 30 días (RollingFileAppender).

    Filtro de Criticidad: Se configuró un registro exclusivo para errores (errors.log) que captura únicamente eventos de nivel ERROR, facilitando la auditoría y el diagnóstico post-mortem.

    Persistencia en Docker: Mediante volúmenes, los archivos de log sobreviven al ciclo de vida de los contenedores, garantizando que la información de fallos no se pierda ante reinicios del sistema.

Ejecución en Local (IntelliJ IDEA)

Para correr el proyecto desde el IDE cargando automáticamente la configuración del archivo .env:

    Configuración del Run: Ir a Run -> Edit Configurations... de tu aplicación principal (ChallangeApplication).

    Pestaña EnvFile: Seleccioná la pestaña EnvFile que aparece en el menú lateral de la ventana de configuración.

    Habilitar y Cargar:

        Marcá el check "Enable EnvFile".

        Hacé clic en el icono + (más) y buscá el archivo .env ubicado en la raíz del proyecto.

    Ejecutar: Ahora podés darle a Run o Debug y la app tomará todas las credenciales de base de datos y puertos definidos en el archivo.

📖 Documentación de la API (Swagger)

La API cuenta con documentación interactiva generada con SpringDoc OpenAPI. Una vez que la aplicación esté corriendo, podés acceder a la interfaz de Swagger para visualizar y probar todos los endpoints:

    Swagger UI: http://localhost:8080/swagger-ui/index.html

    OpenAPI Spec (JSON): http://localhost:8080/v3/api-docs

¿Qué vas a encontrar en Swagger?

    Interactividad: Podés ejecutar peticiones POST, PUT y PATCH, DELETE, GET directamente desde el navegador.

    Modelos de Datos: Explicación detallada de los esquemas ClienteRequest, ClienteResponse y ApiErrorResponse.

    Respuestas de Error: Documentación de los códigos de estado HTTP (200, 201, 400, 404, 409).
