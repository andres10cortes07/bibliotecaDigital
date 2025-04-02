CREATE DATABASE biblioteca;
use biblioteca;

-- Tabla para gestionar usuarios
CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    contrasena VARCHAR(100) NOT NULL,
    telefono VARCHAR(10) UNIQUE NOT NULL,
    rol ENUM('estudiante', 'profesor', 'administrativo') NOT NULL,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla para gestionar recursos (libros, revistas, etc.)
CREATE TABLE recursos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) UNIQUE NOT NULL,
    autor VARCHAR(100) NOT NULL,
    tipo_recurso ENUM('libro', 'revista', 'otro') NOT NULL,
    fecha_publicacion DATE,
    disponible BOOLEAN DEFAULT TRUE
);

-- Tabla para gestionar ejemplares de recursos
CREATE TABLE ejemplares (
    id INT AUTO_INCREMENT PRIMARY KEY,
    recurso_id INT,
    codigo_ejemplar VARCHAR(100) UNIQUE NOT NULL,
    estado ENUM('disponible', 'prestado', 'reservado') NOT NULL,
    fecha_adquisicion DATE,
    FOREIGN KEY (recurso_id) REFERENCES recursos(id) ON DELETE CASCADE
);

-- Tabla para gestionar préstamos
CREATE TABLE prestamos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ejemplar_id INT,
    usuario_id INT,
    fecha_prestamo TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_devolucion TIMESTAMP,
    estado ENUM('activo', 'devuelto', 'atrasado') NOT NULL,
    FOREIGN KEY (ejemplar_id) REFERENCES ejemplares(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- Tabla para gestionar reservas
CREATE TABLE reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    ejemplar_id INT,
    usuario_id INT,
    fecha_reserva TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado ENUM('activa', 'cancelada', 'completada') NOT NULL,
    FOREIGN KEY (ejemplar_id) REFERENCES ejemplares(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);
