-- =============================================================
-- LibreFox — Dados de teste (seed)
-- =============================================================
-- Execute depois do schema.sql:
--   mysql -u root -p librefox_editora < seed.sql
--
-- ATENÇÃO: as senhas aqui estão em texto puro (ex.: "123456") porque
-- o projeto, no estado atual, compara senha em texto puro no login
-- (LoginController: usuario.getSenha().equals(senha)). Isso é
-- aceitável para fins didáticos, mas não deve ser usado em produção
-- — o ideal seria armazenar um hash (ex. BCrypt) em vez da senha crua.
-- =============================================================

USE librefox_editora;

-- -------------------------------------------------------------
-- ENDEREÇOS
-- -------------------------------------------------------------
INSERT INTO endereco (numero, bairro, logradouro, cidade, uf) VALUES
    ('100', 'Centro',      'Rua das Editoras',     'Mossoró', 'RN'),  -- 1: Gerente
    ('200', 'Boa Vista',   'Av. dos Escritores',   'Recife',  'PE'),  -- 2: Autor 1
    ('300', 'Lagoa Nova',  'Rua dos Livros',        'Natal',   'RN'),  -- 3: Autor 2
    ('400', 'Petrópolis',  'Av. da Crítica',        'Natal',   'RN'),  -- 4: Avaliador 1
    ('500', 'Tirol',       'Rua dos Pareceres',     'Natal',   'RN');  -- 5: Avaliador 2

-- -------------------------------------------------------------
-- USUÁRIOS
-- -------------------------------------------------------------
INSERT INTO usuario (nome, cpf, endereco_id, login, senha) VALUES
    ('Paulo Roberto (Sr. Paulão)', '11111111111', 1, 'paulao@librefox.com',     '123456'),  -- id 1: Gerente
    ('Jonas Emanuel',               '22222222222', 2, 'jonas@librefox.com',      '123456'),  -- id 2: Autor
    ('Ana Helena',                  '33333333333', 3, 'ana.helena@librefox.com', '123456'),  -- id 3: Autor
    ('Kleiton Pereira de Brito',    '44444444444', 4, 'kleiton@librefox.com',    '123456'),  -- id 4: Avaliador
    ('Máspoly Gênes Lispector',     '55555555555', 5, 'maspoly@librefox.com',    '123456'); -- id 5: Avaliador

-- -------------------------------------------------------------
-- PERFIS
-- -------------------------------------------------------------
INSERT INTO usuario_perfil (usuario_id, perfil) VALUES
    (1, 'GERENTE'),
    (2, 'AUTOR'),
    (3, 'AUTOR'),
    (4, 'AVALIADOR'),
    (5, 'AVALIADOR');

-- -------------------------------------------------------------
-- OBRAS (alguns estados diferentes para testar todos os fluxos)
-- status: 0 = Em avaliação, 1 = Aceita, 2 = Rejeitada
-- -------------------------------------------------------------
INSERT INTO obra (id, titulo, genero, ano, status, autor_id, avaliador_id, data_submissao, data_avaliacao) VALUES
    -- Em avaliação, já com avaliador designado (testa a tela "Avaliar" do Avaliador)
    (UUID(), 'POO para Burros: Aprenda classes de uma vez', 'Acadêmico', 2025, 0, 2, 4, '2025-01-10', NULL),

    -- Em avaliação, SEM avaliador (testa "Atribuir avaliador" do Gerente)
    (UUID(), 'O dia em que parei de sentir',                'Mistério',  2026, 0, 3, NULL, '2026-02-05', NULL),

    -- Já aceita (testa relatórios e "Minhas obras" do Autor)
    (UUID(), 'A casa que respirava cinzas',                 'Drama',     2024, 1, 3, 5,    '2024-11-01', '2024-11-20'),

    -- Já rejeitada (testa relatórios e feedback)
    (UUID(), 'O peso de um Quase',                          'Poesia',    2024, 2, 2, 5,    '2024-10-15', '2024-10-30');
