-- Reinicio limpio del esquema
DROP TABLE IF EXISTS favoritos CASCADE;
DROP TABLE IF EXISTS comentarios CASCADE;
DROP TABLE IF EXISTS articulos CASCADE;
DROP TABLE IF EXISTS categorias CASCADE;
DROP TABLE IF EXISTS autores CASCADE;
DROP TABLE IF EXISTS usuarios CASCADE;

CREATE TABLE usuarios (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) UNIQUE NOT NULL,
    contrasena VARCHAR(255) NOT NULL,
    rol VARCHAR(20) DEFAULT 'LECTOR',
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE autores (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    biografia TEXT,
    foto TEXT
);

CREATE TABLE categorias (
    id SERIAL PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT
);

CREATE TABLE articulos (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(200) NOT NULL,
    descripcion TEXT,
    contenido TEXT NOT NULL,
    imagen TEXT,
    categoria_id INT REFERENCES categorias(id) ON DELETE CASCADE,
    autor_id INT REFERENCES autores(id) ON DELETE SET NULL,
    fecha_publicacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comentarios (
    id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuarios(id) ON DELETE CASCADE,
    articulo_id INT REFERENCES articulos(id) ON DELETE CASCADE,
    contenido TEXT NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE favoritos (
    id SERIAL PRIMARY KEY,
    usuario_id INT REFERENCES usuarios(id) ON DELETE CASCADE,
    articulo_id INT REFERENCES articulos(id) ON DELETE CASCADE,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(usuario_id, articulo_id)
);

-- Inserción de categorías iniciales con IDs explícitos
INSERT INTO categorias (id, nombre, descripcion) VALUES
(1, 'Moda', 'Artículos sobre tendencias globales de moda'),
(2, 'Tendencias', 'Lo último en pasarelas y temporadas'),
(3, 'Belleza', 'Cuidado de la piel, maquillaje y estilismo'),
(4, 'Street Style', 'Moda urbana y looks cotidianos'),
(5, 'Diseñadores', 'Perfiles e historias de creadores de moda'),
(6, 'Modelos', 'Noticias y entrevistas de figuras de la industria'),
(7, 'Celebridades', 'Estilo de celebridades y alfombras rojas'),
(8, 'Pasarelas', 'Cobertura de eventos e hitos de alta costura');

-- Sincronización de la secuencia del autoincremental de categorías
SELECT setval('categorias_id_seq', (SELECT MAX(id) FROM categorias));