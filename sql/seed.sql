-- ENDERECOS
INSERT INTO endereco (numero, bairro, logradouro, cidade, uf)
VALUES
('101', 'Centro', 'Rua João da Escóssia', 'Mossoró', 'RN'),
('202', 'Nova Betânia', 'Av. Rio Branco', 'Mossoró', 'RN'),
('303', 'Abolição', 'Rua Delfim Moreira', 'Mossoró', 'RN');

-- USUARIOS
INSERT INTO usuario (nome, cpf, endereco_id, login, senha)
VALUES
('Pedro Silva', '11111111111', 1, 'pedro.autor', '123456'),
('Ana Costa', '22222222222', 2, 'ana.avaliadora', '123456'),
('Lucas Mendes', '33333333333', 3, 'lucas.gerente', '123456');

-- PERFIS
INSERT INTO autor (id) VALUES (1);
INSERT INTO avaliador (id) VALUES (2);
INSERT INTO gerente (id) VALUES (3);

-- OBRAS
INSERT INTO obra (
    id,
    titulo,
    genero,
    ano,
    status,
    autor_id,
    avaliador_id,
    data_submissao,
    data_avaliacao
)
VALUES
(
    'OBRA001',
    'O Reino das Sombras',
    'Fantasia',
    2025,
    1,
    1,
    2,
    CURDATE(),
    NULL
),
(
    'OBRA002',
    'Entre Linhas',
    'Drama',
    2024,
    2,
    1,
    2,
    CURDATE(),
    CURDATE()
),
(
    'OBRA003',
    'Fragmentos do Futuro',
    'Ficção Científica',
    2026,
    0,
    1,
    NULL,
    CURDATE(),
    NULL
);