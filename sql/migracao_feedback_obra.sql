-- =============================================================
-- LibreFox — Migração: adicionar coluna "feedback" à tabela obra
-- =============================================================
-- O avaliador já podia escrever um comentário no diálogo de avaliação,
-- mas esse texto nunca era salvo em lugar nenhum — a coluna não existia.
-- Esta migração adiciona "feedback" à tabela obra, sem apagar nada.
--
-- Como executar:
--   No MySQL Shell:  \sql  então  \source caminho/para/este/arquivo.sql
-- =============================================================

USE librefox_editora;

SET @coluna_existe := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'obra'
      AND COLUMN_NAME = 'feedback'
);

SET @sql := IF(@coluna_existe = 0,
    'ALTER TABLE obra ADD COLUMN feedback TEXT NULL',
    'SELECT ''Coluna feedback já existe, nada a fazer.'''
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

DESCRIBE obra;