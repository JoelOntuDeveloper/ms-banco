CREATE DATABASE IF NOT EXISTS db_banco;
use db_banco;

-- CREACION DE TABLAS

-- tabla persona
CREATE TABLE IF NOT EXISTS persona (
    persona_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    identificacion VARCHAR(255) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    genero VARCHAR(255),
    edad INT,
    direccion VARCHAR(200),
    telefono VARCHAR(255)
);

-- tabla cliente
CREATE TABLE IF NOT EXISTS cliente (
    cliente_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    contrasena VARCHAR(255) NOT NULL,
    estado VARCHAR(255),
    persona_id BIGINT NOT NULL,
    UNIQUE KEY uk_cliente_persona_id (persona_id),
    FOREIGN KEY (persona_id) REFERENCES persona(persona_id)
);

-- tabla cuenta
CREATE TABLE IF NOT EXISTS cuenta (
    cuenta_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_cuenta VARCHAR(30) NOT NULL,
    tipo_cuenta VARCHAR(30) NOT NULL,
    saldo_inicial DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    estado VARCHAR(20),
    cliente_id BIGINT NOT NULL,
    UNIQUE KEY uk_cuenta_numero (numero_cuenta),
    FOREIGN KEY (cliente_id) REFERENCES cliente(cliente_id)
);

-- tabla movimiento
CREATE TABLE IF NOT EXISTS movimiento (
    movimiento_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME NOT NULL,
    tipo_movimiento VARCHAR(30) NOT NULL,
    valor DECIMAL(15,2) NOT NULL,
    saldo DECIMAL(15,2) NOT NULL,
    cuenta_id BIGINT NOT NULL,
    FOREIGN KEY (cuenta_id) REFERENCES cuenta(cuenta_id)
);