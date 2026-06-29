USE librefox_editora;

CREATE TABLE IF NOT EXISTS usuario_perfil (
    usuario_id  BIGINT NOT NULL,
    perfil      ENUM('AUTOR', 'AVALIADOR', 'GERENTE') NOT NULL,

    PRIMARY KEY (usuario_id, perfil),

    CONSTRAINT fk_usuario_perfil_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuario(id)
        ON DELETE CASCADE
);

INSERT IGNORE INTO usuario_perfil (usuario_id, perfil)
SELECT id, 'AUTOR' FROM autor;

INSERT IGNORE INTO usuario_perfil (usuario_id, perfil)
SELECT id, 'AVALIADOR' FROM avaliador;

INSERT IGNORE INTO usuario_perfil (usuario_id, perfil)
SELECT id, 'GERENTE' FROM gerente;

SELECT u.id, u.nome, u.login, up.perfil
FROM usuario u
JOIN usuario_perfil up ON up.usuario_id = u.id
ORDER BY u.id;
