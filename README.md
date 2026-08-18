# JeanPipi

JeanPipi es una revista digital construida con Java 17, JSP, Servlets, JDBC y PostgreSQL. El proyecto incluye portal publico, cuentas de lectores, flujo editorial, administracion, seguridad, API REST, pruebas y despliegue con Docker.

## Funcionalidades incluidas

- Registro, inicio de sesion, perfil y recuperacion de contrasena.
- Roles `ADMIN`, `EDITOR`, `AUTOR` y `LECTOR`.
- Bloqueo temporal por intentos fallidos de autenticacion.
- Gestion administrativa de usuarios.
- CRUD editorial de articulos con borradores, revision, programacion, publicacion y archivo.
- Editor enriquecido, vista previa e historial restaurable de versiones.
- Gestion de categorias, autores y biblioteca de imagenes.
- URLs amigables, SEO, Open Graph, contenido destacado y secciones configurables de portada.
- Articulos relacionados, visualizaciones, lectura estimada, comentarios moderados y favoritos.
- Busqueda, filtros, ordenamiento y paginacion.
- Interfaz responsive Mobile First y accesible.
- Dashboard con KPIs, graficos, auditoria y registro de actividad.
- CSRF, rate limiting, validaciones, cabeceras de seguridad y consultas parametrizadas.
- API REST bajo `/api/v1` y documentacion OpenAPI disponible en `/swagger.jsp`.
- HikariCP, Flyway, logging con SLF4J/Logback, health check y paginas 403/404/500.
- JUnit, Mockito, prueba de integracion opcional y GitHub Actions.
- Docker, Docker Compose y configuracion separada por variables de entorno.

## Requisitos locales

- Java 17.
- Maven 3.9 o compatible.
- PostgreSQL 14 o superior, o Docker.

## Inicio rapido con Docker

1. Copia `.env.example` a `.env`.
2. Cambia las credenciales y variables de administrador.
3. Ejecuta:

```bash
docker compose up -d --build
```

4. Abre `http://localhost:8080`.

Flyway crea el esquema automaticamente al iniciar la aplicacion.

## Ejecucion con Maven

Configura como minimo:

```text
JEANPIPI_DB_URL=jdbc:postgresql://localhost:5432/jeanpipi
JEANPIPI_DB_USER=postgres
JEANPIPI_DB_PASSWORD=tu-clave
JEANPIPI_ADMIN_EMAIL=admin@example.com
JEANPIPI_ADMIN_PASSWORD=una-clave-segura
```

Luego ejecuta:

```bash
mvn clean test
mvn jetty:run
```

La aplicacion queda disponible en `http://localhost:8080/JeanPipi`.

## Produccion

Usa `.env.production.example` como referencia, cambia todos los secretos y ejecuta:

```bash
docker compose --env-file .env -f docker-compose.prod.yml up -d --build
```

En un entorno real se recomienda publicar el puerto 8080 detras de un proxy HTTPS y mantener PostgreSQL sin exposicion publica.

## Correo de recuperacion

Para que el flujo de recuperacion envie enlaces reales, configura las variables `JEANPIPI_SMTP_*`. Sin SMTP configurado la aplicacion no expone tokens en la interfaz.

## Pruebas

```bash
mvn test
```

La prueba de integracion de PostgreSQL se habilita solo si existe `JEANPIPI_TEST_DB_URL`, con `JEANPIPI_TEST_DB_USER` y `JEANPIPI_TEST_DB_PASSWORD` cuando correspondan.

## Build

```bash
mvn clean package
```

El WAR se genera como `target/JeanPipi.war`. `target/` esta excluido del repositorio.
