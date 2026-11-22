ALTER TABLE usuario
    MODIFY COLUMN correo_usuario VARCHAR(100) NULL,
    ADD COLUMN run_usuario VARCHAR(12) NULL,
    ADD COLUMN clave_hash VARCHAR(100) NULL,
    ADD COLUMN fecha_nacimiento DATE NULL,
    ADD COLUMN tipo_usuario VARCHAR(20) NOT NULL DEFAULT 'CLIENTE',
    ADD COLUMN region VARCHAR(100) NULL,
    ADD COLUMN comuna VARCHAR(100) NULL,
    ADD COLUMN direccion VARCHAR(300) NULL;

CREATE UNIQUE INDEX uk_usuario_run ON usuario (run_usuario);
