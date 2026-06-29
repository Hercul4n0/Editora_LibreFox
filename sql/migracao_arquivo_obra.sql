-- =============================================================
-- LibreFox — Migração: adicionar coluna "arquivo" à tabela obra
-- =============================================================
-- Por que esta migração é necessária:
--
-- A classe Obra.java e o ObraDAO.java já foram atualizados para guardar o
-- caminho do arquivo da obra (PDF/TXT/DOCX que o autor anexa ao submeter
-- uma obra) numa coluna chamada "arquivo". Só que o banco real ainda não
-- tem essa coluna — por isso o INSERT falha com:
--   "Unknown column 'arquivo' in 'field list'"
--
-- Esta migração só adiciona a coluna que falta; não apaga nem altera
-- nenhum dado existente.
--
-- Como executar:
--   No MySQL Shell:  \sql  então  \source caminho/para/este/arquivo.sql
--   No terminal:      mysql -u root -p librefox_editora < este_arquivo.sql
-- =============================================================

USE librefox_editora;

SET @coluna_existe := (
    SELECT COUNT(*) FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'obra'
      AND COLUMN_NAME = 'arquivo'
);

SET @sql := IF(@coluna_existe = 0,
    'ALTER TABLE obra ADD COLUMN arquivo VARCHAR(500) NULL',
    'SELECT ''Coluna arquivo já existe, nada a fazer.'''
);

PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- Conferência: a coluna "arquivo" deve aparecer na lista abaixo.
DESCRIBE obra;
