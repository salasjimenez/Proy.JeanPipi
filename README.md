# JeanPipi

JeanPipi es una revista digital desarrollada con Java 17, Servlets, JSP, JDBC, PostgreSQL, Gson, JavaScript puro y CSS. Esta versión mantiene el diseño visual del repositorio original, corrige los flujos incompletos y organiza el código en capas DAO, servicios, DTO, Servlets y utilidades.

## Requisitos

- Java 17
- Maven 3.9 o superior
- PostgreSQL

## Configuración de PostgreSQL

Crea la base de datos:

```bash
createdb jeanpipi_db
```

Aplica el esquema:

```bash
psql -d jeanpipi_db -f src/main/resources/schema.sql
```

El script es idempotente: no elimina las tablas existentes y puede ejecutarse nuevamente.

## Variables de entorno

Configura la conexión antes de iniciar la aplicación:

```bash
export JEANPIPI_DB_URL='jdbc:postgresql://localhost:5432/jeanpipi_db'
export JEANPIPI_DB_USER='postgres'
export JEANPIPI_DB_PASSWORD='TU_PASSWORD_POSTGRES'
```

Para crear de forma segura el primer administrador en una instalación nueva:

```bash
export JEANPIPI_ADMIN_EMAIL='admin@jeanpipi.local'
export JEANPIPI_ADMIN_PASSWORD='CAMBIA_ESTA_PASSWORD_SEGURA'
export JEANPIPI_ADMIN_NAME='Administrador'
```

`JEANPIPI_ADMIN_PASSWORD` debe tener al menos 12 caracteres. El sistema no contiene credenciales de administrador predeterminadas.

También puedes proporcionar las propiedades JVM `jeanpipi.db.url`, `jeanpipi.db.user` y `jeanpipi.db.password` en lugar de variables de entorno.

## Ejecución local

```bash
mvn clean jetty:run
```

La aplicación queda disponible en el contexto `/JeanPipi/` del puerto `8080`.

## Flujo funcional

- `index.jsp`: portada, búsqueda, filtros por categoría, tendencias y últimos artículos.
- `articulo.jsp?id=ID`: detalle real de un artículo mediante `GET /api/articulos?id=ID`.
- `login.jsp`: autenticación por sesión HTTP.
- `admin.jsp`: panel protegido para usuarios con rol `ADMIN`.
- `POST /api/articulos`: creación protegida de artículos.
- `PUT /api/articulos?id=ID`: actualización protegida.
- `DELETE /api/articulos?id=ID`: eliminación protegida.
- `POST /api/favoritos`: alterna favoritos utilizando el usuario autenticado de la sesión.

## Seguridad aplicada

- Consultas SQL parametrizadas mediante `PreparedStatement`.
- Recursos JDBC cerrados con try-with-resources.
- Credenciales de PostgreSQL fuera del código fuente.
- Contraseñas nuevas almacenadas mediante PBKDF2-HMAC-SHA256 con salt aleatorio.
- Usuarios heredados con contraseña en texto plano son migrados al hash al autenticarse correctamente.
- Regeneración del ID de sesión después del login.
- Autorización del panel administrativo por rol.
- Respuestas de error sin exponer mensajes internos de PostgreSQL.
- Cabeceras CSP, `X-Content-Type-Options`, `X-Frame-Options`, `Referrer-Policy` y `Permissions-Policy`.
- Renderizado de datos externos con `textContent` para reducir riesgo XSS.

## Estructura

```text
src/main/java/com/jeanpipi/
├── config/
├── dao/
├── dto/
├── exception/
├── filtros/
├── listeners/
├── modelos/
├── servicios/
├── servlets/
└── util/
src/main/resources/schema.sql
src/main/webapp/
├── WEB-INF/web.xml
├── css/estilos.css
├── img/JeanPipi.png
├── js/
└── *.jsp
```

No se utilizan archivos `.html`; todo el marcado web permanece en JSP.
