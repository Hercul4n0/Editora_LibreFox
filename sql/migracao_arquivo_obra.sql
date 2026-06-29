-- Adiciona a coluna do arquivo da obra (conteúdo a ser avaliado: txt/pdf/docx).
-- Guarda o caminho do arquivo armazenado localmente (pasta "obras_arquivos").
USE librefox_editora;

ALTER TABLE obra
    ADD COLUMN arquivo VARCHAR(255) NULL AFTER data_avaliacao;
