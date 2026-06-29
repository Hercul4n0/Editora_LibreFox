-- =============================================================
-- LibreFox — Migração: adicionar tabela usuario_perfil
-- =============================================================
-- Por que esta migração é necessária:
--
-- O schema.sql atual modela cada perfil como uma tabela própria
-- (autor, avaliador, gerente), cada uma só com "id" (FK para usuario).
-- Os DAOs do projeto (AutorDAO, AvaliadorDAO, GerenteDAO, UsuarioDAO),
-- porém, foram escritos para um modelo onde os perfis de um usuário
-- ficam registrados numa tabela única "usuario_perfil" — é nela que o
-- login, a listagem e o cadastro de cada perfil fazem JOIN.
--
-- Sem essa tabela, qualquer tentativa de login, listagem ou cadastro
-- falha com "Table 'librefox_editora.usuario_perfil' doesn't exist".
--
-- Esta migração CRIA a tabela que falta e a POPULA automaticamente
-- a partir dos dados que já existem em autor/avaliador/gerente —
-- ou seja, não é preciso recriar o banco nem perder nada que você
-- já tenha cadastrado.
--
-- Como executar (depois de schema.sql e seed.sql já terem rodado):
--   mysql -u root -p librefox_editora < migracao_usuario_perfil.sql
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

-- Popular a partir dos dados já existentes nas tabelas de perfil.
-- INSERT IGNORE evita erro caso a migração seja executada mais de uma vez.
INSERT IGNORE INTO usuario_perfil (usuario_id, perfil)
SELECT id, 'AUTOR' FROM autor;

INSERT IGNORE INTO usuario_perfil (usuario_id, perfil)
SELECT id, 'AVALIADOR' FROM avaliador;

INSERT IGNORE INTO usuario_perfil (usuario_id, perfil)
SELECT id, 'GERENTE' FROM gerente;

-- Conferir o resultado (deve mostrar 1 linha por usuário cadastrado
-- em autor/avaliador/gerente):
SELECT u.id, u.nome, u.login, up.perfil
FROM usuario u
JOIN usuario_perfil up ON up.usuario_id = u.id
ORDER BY u.id;
