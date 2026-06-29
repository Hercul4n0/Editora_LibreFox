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