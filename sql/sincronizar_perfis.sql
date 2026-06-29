USE librefox_editora;

CREATE TABLE IF NOT EXISTS usuario_perfil (
                                              usuario_id  BIGINT NOT NULL,
                                              perfil      ENUM('AUTOR', 'AVALIADOR', 'GERENTE') NOT NULL,
    PRIMARY KEY (usuario_id, perfil),
    CONSTRAINT fk_usuario_perfil_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
    ON DELETE CASCADE
    );

INSERT IGNORE INTO autor (id)
SELECT usuario_id FROM usuario_perfil WHERE perfil = 'AUTOR';

INSERT IGNORE INTO avaliador (id)
SELECT usuario_id FROM usuario_perfil WHERE perfil = 'AVALIADOR';

INSERT IGNORE INTO gerente (id)
SELECT usuario_id FROM usuario_perfil WHERE perfil = 'GERENTE';

SELECT 'Autores sem vínculo na tabela autor:' AS verificacao;
SELECT u.id, u.nome, u.login
FROM usuario u
         JOIN usuario_perfil up ON up.usuario_id = u.id AND up.perfil = 'AUTOR'
WHERE u.id NOT IN (SELECT id FROM autor);

SELECT 'Avaliadores sem vínculo na tabela avaliador:' AS verificacao;
SELECT u.id, u.nome, u.login
FROM usuario u
         JOIN usuario_perfil up ON up.usuario_id = u.id AND up.perfil = 'AVALIADOR'
WHERE u.id NOT IN (SELECT id FROM avaliador);


SELECT 'Resumo geral:' AS verificacao;
SELECT u.id, u.nome, u.login,
       GROUP_CONCAT(up.perfil ORDER BY up.perfil) AS perfis,
       (u.id IN (SELECT id FROM autor)) AS tem_linha_autor,
       (u.id IN (SELECT id FROM avaliador)) AS tem_linha_avaliador,
       (u.id IN (SELECT id FROM gerente)) AS tem_linha_gerente
FROM usuario u
         LEFT JOIN usuario_perfil up ON up.usuario_id = u.id
GROUP BY u.id, u.nome, u.login
ORDER BY u.id;