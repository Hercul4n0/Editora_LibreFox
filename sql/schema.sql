CREATE DATABASE IF NOT EXISTS editora;
-- Enquanto ainda não arrumamos um jeito de linkar, criei o meu localmente e deixei esses
-- dois arquivos para que voce crie o seu.
--Teste de criação de banco e tabela

USE editora;

CREATE TABLE autor (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100),
    cpf VARCHAR(14),
    endereco VARCHAR(200)
);