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
DB_HOST=localhost
DB_PORT=5432
DB_NAME=clientes_bd
DB_USERNAME=postgres
DB_PASSWORD=1234

# JPA / HIBERNATE
HIBERNATE_DDL=update
SHOW_SQL=true

# SERVER
SERVER_PORT=8080

🐳 Despliegue con Docker Compose

Para levantar la API junto con el contenedor de PostgreSQL, simplemente ejecutá:
Bash

docker-compose up -d

Detalles del despliegue:

    API: Disponible en http://localhost:8080 (o el puerto configurado en SERVER_PORT).

    PostgreSQL: Corre internamente en el puerto 5432, pero se expone al host según DB_PORT.

    Persistencia: La base de datos se crea automáticamente con el nombre definido en DB_NAME.

🧪 Ejecución de Tests e Integración

La suite de tests está diseñada para ser autónoma. Aunque la app usa PostgreSQL, los tests levantan una base H2 en memoria. Esto permite ejecutar pruebas sin necesidad de tener la base de datos de Docker encendida.

Para correr los tests y ver la cobertura:
Bash

mvn clean test

Reporte de Cobertura (JaCoCo)

Al finalizar, podés abrir el reporte detallado en: target/site/jacoco/index.html

    Estado de Cobertura: 100% en las clases de Service y Controller, cubriendo todas las ramificaciones lógicas de validación de Email y CUIT.

🔌 Endpoints Principales

    GET /api/clientes: Lista todos los registros.

    POST /api/clientes: Crea un cliente (valida CUIT/Email duplicados).

    PATCH /api/clientes/{id}/email: Actualización específica del email.

    PUT /api/clientes/{id}: Actualización completa del cliente.

    DELETE /api/clientes/{id}: Borrado físico del registro.

Ejecución en Local (IntelliJ IDEA)

Para correr el proyecto desde el IDE cargando automáticamente la configuración del archivo .env:

    Configuración del Run: Ir a Run -> Edit Configurations... de tu aplicación principal (ChallangeApplication).

    Pestaña EnvFile: Seleccioná la pestaña EnvFile que aparece en el menú lateral de la ventana de configuración.

    Habilitar y Cargar:

        Marcá el check "Enable EnvFile".

        Hacé clic en el icono + (más) y buscá el archivo .env ubicado en la raíz del proyecto.

    Ejecutar: Ahora podés darle a Run o Debug y la app tomará todas las credenciales de base de datos y puertos definidos en el archivo.

Pasos finales recomendados:

    Asegurate de que tu Dockerfile esté en la raíz del proyecto.

    Verificá que el archivo .env no se suba al repositorio público (añadilo al .gitignore), pero dejá un .env.example para que el evaluador sepa qué valores poner.