CREATE DATABASE IF NOT EXISTS gestion_funcionarios
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE gestion_funcionarios;

-- TABLA: tipo_documento

CREATE TABLE IF NOT EXISTS tipo_documento (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(10) NOT NULL UNIQUE,
    descripcion VARCHAR(100) NOT NULL
);

-- TABLA: estado_civil

CREATE TABLE IF NOT EXISTS estado_civil (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descripcion VARCHAR(50) NOT NULL UNIQUE
);

-- TABLA: funcionarios

CREATE TABLE IF NOT EXISTS funcionarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    id_tipo_documento INT NOT NULL,
    numero_documento VARCHAR(20) NOT NULL UNIQUE,
    fecha_nacimiento DATE NOT NULL,
    id_estado_civil INT NOT NULL,
    email VARCHAR(150),
    telefono VARCHAR(20),
    cargo VARCHAR(100),
    fecha_ingreso DATE NOT NULL,
    activo TINYINT(1) NOT NULL DEFAULT 1,
    CONSTRAINT fk_funcionario_tipo_doc
        FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento(id),
    CONSTRAINT fk_funcionario_estado_civil
        FOREIGN KEY (id_estado_civil) REFERENCES estado_civil(id)
);

-- TABLA: grupo_familiar

CREATE TABLE IF NOT EXISTS grupo_familiar (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_funcionario INT NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    parentesco VARCHAR(50) NOT NULL,
    fecha_nacimiento DATE,
    CONSTRAINT fk_grupo_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionarios(id)
            ON DELETE CASCADE
);

-- TABLA: formacion_academica

CREATE TABLE IF NOT EXISTS formacion_academica (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_funcionario INT NOT NULL,
    nivel_educativo VARCHAR(80) NOT NULL,
    titulo_obtenido VARCHAR(150) NOT NULL,
    institucion VARCHAR(150) NOT NULL,
    anio_graduacion YEAR,
    CONSTRAINT fk_formacion_funcionario
        FOREIGN KEY (id_funcionario) REFERENCES funcionarios(id)
            ON DELETE CASCADE
);

-- DATOS INICIALES


-- Tipos de documento
INSERT INTO tipo_documento (codigo, descripcion) VALUES
    ('CC',  'Cédula de Ciudadanía'),
    ('TI',  'Tarjeta de Identidad'),
    ('CE',  'Cédula de Extranjería'),
    ('PA',  'Pasaporte'),
    ('NIT', 'Número de Identificación Tributaria');

-- Estados civiles
INSERT INTO estado_civil (descripcion) VALUES
    ('Soltero/a'),
    ('Casado/a'),
    ('Unión Libre'),
    ('Divorciado/a'),
    ('Viudo/a');

-- Funcionarios de ejemplo
INSERT INTO funcionarios
    (nombres, apellidos, id_tipo_documento, numero_documento,
     fecha_nacimiento, id_estado_civil, email, telefono, cargo, fecha_ingreso, activo)
VALUES
    ('Carlos Andrés', 'Gómez Restrepo',  1, '1000111222', '1985-03-12', 2,
     'cgomez@entidad.gov.co',    '3101234567', 'Analista de Sistemas',   '2018-06-01', 1),
    ('María Alejandra', 'Torres Ríos',   1, '1000333444', '1990-07-25', 1,
     'matorres@entidad.gov.co',  '3209876543', 'Coordinadora de RRHH',   '2020-01-15', 1),
    ('Juan Pablo', 'Martínez López',     1, '1000555666', '1978-11-08', 3,
     'jmartinez@entidad.gov.co', '3155556677', 'Director Administrativo','2010-03-01', 1),
    ('Laura Sofía', 'Herrera Vargas',    3, '987654321',  '1992-05-19', 1,
     'lherrera@entidad.gov.co',  '3123334455', 'Contadora',              '2021-08-10', 1),
    ('Andrés Felipe', 'Castro Duque',    1, '1000777888', '1988-09-30', 2,
     'acastro@entidad.gov.co',   '3167778899', 'Ingeniero de Soporte',   '2015-11-22', 1);

-- Grupo familiar
INSERT INTO grupo_familiar (id_funcionario, nombres, apellidos, parentesco, fecha_nacimiento) VALUES
    (1, 'Valentina', 'Gómez Sierra',    'Hija',   '2012-04-01'),
    (1, 'Rosa Elena', 'Restrepo de Gómez', 'Cónyuge', '1986-09-15'),
    (2, 'Pedro Luis', 'Torres Cruz',    'Padre',  '1962-01-20'),
    (3, 'Nicolás',   'Martínez Torres', 'Hijo',   '2005-07-11'),
    (5, 'Sara',      'Castro Morales',  'Cónyuge','1990-03-03');

-- Formación académica
INSERT INTO formacion_academica (id_funcionario, nivel_educativo, titulo_obtenido, institucion, anio_graduacion) VALUES
    (1, 'Universitario',  'Ingeniero de Sistemas',         'Universidad de Antioquia', 2008),
    (1, 'Especialización','Esp. en Seguridad Informática', 'Universidad EAFIT',        2012),
    (2, 'Universitario',  'Administradora de Empresas',    'Universidad Nacional',     2013),
    (3, 'Maestría',       'MBA Gestión Pública',           'Universidad de los Andes', 2005),
    (4, 'Universitario',  'Contadora Pública',             'Universidad de Medellín',  2015),
    (5, 'Tecnológico',    'Tecnólogo en Sistemas',         'ITM',                      2010);