-- =============================================================
-- LibreFox — Correção definitiva: sincronizar perfis com autor/avaliador/gerente
-- =============================================================
-- O erro "Cannot add or update a child row: a foreign key constraint
-- fails ... FOREIGN KEY (autor_id) REFERENCES autor (id)" acontece porque
-- existe um usuário com perfil de Autor em usuario_perfil, mas SEM a linha
-- correspondente na tabela "autor" — e é "autor.id" que a tabela "obra"
-- exige via FK, não "usuario.id" direto.
--
-- Este script:
-- 1) Garante que a tabela usuario_perfil existe (não recria se já existir).
-- 2) Preenche autor/avaliador/gerente a partir de usuario_perfil para
--    QUALQUER usuário que esteja faltando — não só o seu caso específico.
-- 3) Ao final, mostra uma conferência: se aparecer "0 usuários sem
--    vínculo", está tudo certo.
--
-- Como executar:
--   No MySQL Shell:  \sql  então  \source caminho/para/este/arquivo.sql
-- =============================================================

USE librefox_editora;

CREATE TABLE IF NOT EXISTS usuario_perfil (
                                              usuario_id  BIGINT NOT NULL,
                                              perfil      ENUM('AUTOR', 'AVALIADOR', 'GERENTE') NOT NULL,
    PRIMARY KEY (usuario_id, perfil),
    CONSTRAINT fk_usuario_perfil_usuario
    FOREIGN KEY (usuario_id) REFERENCES usuario(id)
    ON DELETE CASCADE
    );

-- Popular autor/avaliador/gerente a partir de usuario_perfil,
-- só para quem ainda não tem a linha correspondente.
INSERT IGNORE INTO autor (id)
SELECT usuario_id FROM usuario_perfil WHERE perfil = 'AUTOR';

INSERT IGNORE INTO avaliador (id)
SELECT usuario_id FROM usuario_perfil WHERE perfil = 'AVALIADOR';

INSERT IGNORE INTO gerente (id)
SELECT usuario_id FROM usuario_perfil WHERE perfil = 'GERENTE';

-- -------------------------------------------------------------
-- CONFERÊNCIA 1: deve mostrar 0 linhas (nenhum usuário com perfil
-- de Autor que ainda esteja faltando na tabela autor).
-- -------------------------------------------------------------
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

-- -------------------------------------------------------------
-- CONFERÊNCIA 2: visão geral de todos os usuários e seus perfis.
-- -------------------------------------------------------------
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