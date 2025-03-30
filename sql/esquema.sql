-- Tabla de administración de categorías
CREATE TABLE categoria (
    id_categoria SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla de administración de productos
CREATE TABLE producto (
    id_producto SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    id_categoria INTEGER NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    cantidad_existente INTEGER NOT NULL,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (id_categoria)
        REFERENCES categoria (id_categoria)
);

-- Tabla de administración de clientes
CREATE TABLE cliente (
    id_cliente SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    cedula VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    CONSTRAINT uc_cliente UNIQUE (cedula, email)
);

-- Tabla de cabecera de ventas
CREATE TABLE venta (
    id_venta SERIAL PRIMARY KEY,
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_cliente INTEGER NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_venta_cliente FOREIGN KEY (id_cliente)
        REFERENCES cliente (id_cliente)
);

-- Tabla de detalle de venta
CREATE TABLE detalle_venta (
    id_detalle_venta SERIAL PRIMARY KEY,
    id_venta INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    CONSTRAINT fk_detalleventa_venta FOREIGN KEY (id_venta)
        REFERENCES venta (id_venta),
    CONSTRAINT fk_detalleventa_producto FOREIGN KEY (id_producto)
        REFERENCES producto (id_producto)
);
