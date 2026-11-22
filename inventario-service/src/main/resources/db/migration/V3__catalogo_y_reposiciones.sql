CREATE TABLE categoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(255) NOT NULL,
    categoria_id BIGINT NOT NULL,
    precio DECIMAL(12,2) NOT NULL,
    descripcion TEXT,
    imagen VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE orden_reposicion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    fecha_creacion DATETIME NOT NULL,
    fecha_recepcion DATETIME,
    CONSTRAINT fk_reposicion_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
);

CREATE TABLE movimiento_inventario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    stock_resultante INT NOT NULL,
    motivo VARCHAR(100) NOT NULL,
    fecha DATETIME NOT NULL,
    orden_reposicion_id BIGINT,
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES producto(id),
    CONSTRAINT fk_movimiento_reposicion FOREIGN KEY (orden_reposicion_id) REFERENCES orden_reposicion(id)
);

INSERT INTO categoria (id, nombre) VALUES
(1, 'Alimento'), (2, 'Accesorios'), (3, 'Higiene'), (4, 'Salud'), (5, 'Juguetes');

INSERT INTO producto (id, codigo, nombre, categoria_id, precio, descripcion, imagen, activo) VALUES
(1001, 'ALM-001', 'Alimento seco Adulto 12kg', 1, 42990.00, 'Alimento balanceado para perros adultos, todas las razas.', 'bravery_lamb.webp', TRUE),
(1002, 'ALM-002', 'Alimento gato Esterilizado 7kg', 1, 15990.00, 'Fórmula para gatos esterilizados, control de peso.', 'BRAVERY-STERILIZED.webp', TRUE),
(1003, 'ACC-001', 'Correa ajustable 4.5m', 2, 8990.00, 'Correa resistente con mango acolchado.', 'correa_gato.webp', TRUE),
(1004, 'ACC-002', 'Cama acolchada M', 2, 21990.00, 'Cama lavable, talla mediana, hasta 15kg.', 'cama_m.webp', TRUE),
(1005, 'HIG-001', 'Shampoo antipulgas 400ml', 3, 6990.00, 'Shampoo dermoprotector con acción antipulgas.', 'SHAMPOO-ANTIPULGAS-GATOS.webp', TRUE),
(1006, 'HIG-002', 'Arena sanitaria 10L', 3, 7490.00, 'Arena aglomerante de bajo polvo.', 'arena.webp', TRUE),
(1007, 'SAL-001', 'Antiparasitario externo (pipeta) Nex Guard', 4, 9990.00, 'Protección contra pulgas y garrapatas, 1 aplicación mensual.', 'nexgard.webp', TRUE),
(1008, 'JUG-001', 'Pelota interactiva sonora', 5, 5490.00, 'Juguete resistente a mordidas, sonido incorporado.', 'juegogato.webp', TRUE);

INSERT INTO inventario (producto_id, nombre_producto, stock_actual, stock_minimo, unidad_medida, ultima_actualizacion) VALUES
(1001, 'Alimento seco Adulto 12kg', 18, 4, 'unidad', NOW()),
(1002, 'Alimento gato Esterilizado 7kg', 25, 5, 'unidad', NOW()),
(1003, 'Correa ajustable 4.5m', 4, 6, 'unidad', NOW()),
(1004, 'Cama acolchada M', 25, 15, 'unidad', NOW()),
(1005, 'Shampoo antipulgas 400ml', 22, 5, 'unidad', NOW()),
(1006, 'Arena sanitaria 10L', 40, 8, 'unidad', NOW()),
(1007, 'Antiparasitario externo (pipeta) Nex Guard', 15, 4, 'unidad', NOW()),
(1008, 'Pelota interactiva sonora', 28, 6, 'unidad', NOW());
