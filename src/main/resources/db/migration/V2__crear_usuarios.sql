
CREATE TABLE usuarios (
                          id BIGSERIAL PRIMARY KEY,
                          username VARCHAR(100) NOT NULL UNIQUE,
                          password_hash VARCHAR(255) NOT NULL,
                          nombre_completo VARCHAR(150) NOT NULL,
                          rol VARCHAR(50) NOT NULL DEFAULT 'ADMIN',
                          activo BOOLEAN NOT NULL DEFAULT TRUE,
                          creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
