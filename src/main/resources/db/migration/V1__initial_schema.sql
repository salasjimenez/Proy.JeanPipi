-- Esquema inicial de JeanPipi.
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(190) NOT NULL UNIQUE,
    password_hash VARCHAR(500) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'LECTOR' CHECK (rol IN ('ADMIN','EDITOR','AUTOR','LECTOR')),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    avatar_url VARCHAR(500),
    bio TEXT,
    instagram VARCHAR(190),
    twitter VARCHAR(190),
    failed_attempts INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS password_reset_tokens (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    used_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS categorias (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    descripcion TEXT,
    portada_url VARCHAR(500),
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    orden INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS articulos (
    id BIGSERIAL PRIMARY KEY,
    titulo VARCHAR(220) NOT NULL,
    slug VARCHAR(250) NOT NULL UNIQUE,
    extracto TEXT,
    contenido TEXT NOT NULL,
    portada_url VARCHAR(500),
    categoria_id BIGINT REFERENCES categorias(id) ON DELETE SET NULL,
    autor_id BIGINT NOT NULL REFERENCES usuarios(id),
    estado VARCHAR(24) NOT NULL DEFAULT 'BORRADOR' CHECK (estado IN ('BORRADOR','EN_REVISION','PROGRAMADO','PUBLICADO','ARCHIVADO')),
    publicado_at TIMESTAMPTZ,
    programado_at TIMESTAMPTZ,
    destacado BOOLEAN NOT NULL DEFAULT FALSE,
    seo_titulo VARCHAR(220),
    seo_descripcion VARCHAR(320),
    seo_canonical VARCHAR(500),
    seo_imagen VARCHAR(500),
    vistas BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_articulos_estado_publicado ON articulos(estado, publicado_at DESC);
CREATE INDEX IF NOT EXISTS idx_articulos_categoria ON articulos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_articulos_autor ON articulos(autor_id);
CREATE INDEX IF NOT EXISTS idx_articulos_titulo_lower ON articulos(LOWER(titulo));

CREATE TABLE IF NOT EXISTS articulo_versiones (
    id BIGSERIAL PRIMARY KEY,
    articulo_id BIGINT NOT NULL REFERENCES articulos(id) ON DELETE CASCADE,
    titulo VARCHAR(220) NOT NULL,
    extracto TEXT,
    contenido TEXT NOT NULL,
    editor_id BIGINT REFERENCES usuarios(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS comentarios (
    id BIGSERIAL PRIMARY KEY,
    articulo_id BIGINT NOT NULL REFERENCES articulos(id) ON DELETE CASCADE,
    user_id BIGINT REFERENCES usuarios(id) ON DELETE SET NULL,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(190),
    contenido TEXT NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE','APROBADO','OCULTO','RECHAZADO')),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_comentarios_articulo_estado ON comentarios(articulo_id, estado, created_at DESC);

CREATE TABLE IF NOT EXISTS favoritos (
    user_id BIGINT NOT NULL REFERENCES usuarios(id) ON DELETE CASCADE,
    articulo_id BIGINT NOT NULL REFERENCES articulos(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    PRIMARY KEY (user_id, articulo_id)
);

CREATE TABLE IF NOT EXISTS medios (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES usuarios(id) ON DELETE SET NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    nombre_guardado VARCHAR(255) NOT NULL UNIQUE,
    url VARCHAR(600) NOT NULL,
    mime_type VARCHAR(120) NOT NULL,
    tamano BIGINT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE IF NOT EXISTS auditoria (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES usuarios(id) ON DELETE SET NULL,
    accion VARCHAR(80) NOT NULL,
    entidad VARCHAR(80) NOT NULL,
    entidad_id VARCHAR(100),
    ip VARCHAR(80),
    detalle TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_auditoria_fecha ON auditoria(created_at DESC);

CREATE TABLE IF NOT EXISTS articulo_vistas_diarias (
    articulo_id BIGINT NOT NULL REFERENCES articulos(id) ON DELETE CASCADE,
    fecha DATE NOT NULL,
    vistas BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (articulo_id, fecha)
);

CREATE TABLE IF NOT EXISTS secciones_portada (
    id BIGSERIAL PRIMARY KEY,
    clave VARCHAR(80) NOT NULL UNIQUE,
    titulo VARCHAR(160) NOT NULL,
    habilitada BOOLEAN NOT NULL DEFAULT TRUE,
    posicion INT NOT NULL DEFAULT 0,
    limite_items INT NOT NULL DEFAULT 6 CHECK (limite_items BETWEEN 1 AND 24),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

INSERT INTO categorias(nombre, slug, descripcion, orden) VALUES
('Moda','moda','Editoriales y novedades de moda',1),
('Tendencias','tendencias','Tendencias de temporada',2),
('Belleza','belleza','Belleza y cuidado personal',3),
('Street Style','street-style','Estilo urbano',4),
('Disenadores','disenadores','Diseno y creatividad',5),
('Modelos','modelos','Modelos y editoriales',6),
('Celebridades','celebridades','Estilo de celebridades',7),
('Pasarelas','pasarelas','Cobertura de pasarelas',8)
ON CONFLICT (nombre) DO NOTHING;

INSERT INTO secciones_portada(clave, titulo, posicion, limite_items) VALUES
('destacado','Editorial destacado',1,1),
('tendencias','Tendencias de la semana',2,6),
('ultimos','Ultimos articulos',3,9)
ON CONFLICT (clave) DO NOTHING;
