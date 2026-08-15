CREATE TABLE IF NOT EXISTS usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(20) NOT NULL DEFAULT 'LECTOR',
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS autores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    biografia TEXT,
    foto TEXT
);

CREATE TABLE IF NOT EXISTS categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS articulos (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    contenido TEXT NOT NULL,
    imagen TEXT,
    categoria_id INT REFERENCES categorias(id) ON DELETE CASCADE,
    autor_id INT REFERENCES autores(id) ON DELETE SET NULL,
    fecha_publicacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS comentarios (
    id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuarios(id) ON DELETE CASCADE,
    articulo_id INT REFERENCES articulos(id) ON DELETE CASCADE,
    contenido TEXT NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS favoritos (
    id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuarios(id) ON DELETE CASCADE,
    articulo_id INT REFERENCES articulos(id) ON DELETE CASCADE,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (usuario_id, articulo_id)
);

INSERT INTO categorias (id, nombre, descripcion) VALUES
    (1, 'Moda', 'Artículos sobre tendencias globales de moda'),
    (2, 'Tendencias', 'Lo último en pasarelas y temporadas'),
    (3, 'Belleza', 'Cuidado de la piel, maquillaje y estilismo'),
    (4, 'Street Style', 'Moda urbana y looks cotidianos'),
    (5, 'Diseñadores', 'Perfiles e historias de creadores de moda'),
    (6, 'Modelos', 'Noticias y entrevistas de figuras de la industria'),
    (7, 'Celebridades', 'Estilo de celebridades y alfombras rojas'),
    (8, 'Pasarelas', 'Cobertura de eventos e hitos de alta costura')
ON CONFLICT DO NOTHING;

SELECT setval(
    pg_get_serial_sequence('categorias', 'id'),
    GREATEST(COALESCE((SELECT MAX(id) FROM categorias), 1), 1),
    true
);

CREATE INDEX IF NOT EXISTS idx_articulos_fecha_publicacion
    ON articulos (fecha_publicacion DESC);
CREATE INDEX IF NOT EXISTS idx_articulos_categoria_id
    ON articulos (categoria_id);
CREATE INDEX IF NOT EXISTS idx_articulos_autor_id
    ON articulos (autor_id);
CREATE INDEX IF NOT EXISTS idx_comentarios_usuario_id
    ON comentarios (usuario_id);
CREATE INDEX IF NOT EXISTS idx_comentarios_articulo_id
    ON comentarios (articulo_id);
CREATE INDEX IF NOT EXISTS idx_favoritos_usuario_id
    ON favoritos (usuario_id);
CREATE INDEX IF NOT EXISTS idx_favoritos_articulo_id
    ON favoritos (articulo_id);
CREATE INDEX IF NOT EXISTS idx_usuarios_email_lower
    ON usuarios (LOWER(email));
