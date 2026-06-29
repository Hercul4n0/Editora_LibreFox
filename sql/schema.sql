-- =========================
-- ENDERECO
-- =========================
CREATE TABLE endereco (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          numero VARCHAR(20) NOT NULL,
                          bairro VARCHAR(100) NOT NULL,
                          logradouro VARCHAR(150) NOT NULL,
                          cidade VARCHAR(100) NOT NULL,
                          uf CHAR(2) NOT NULL
);

-- =========================
-- USUARIO
-- =========================
CREATE TABLE usuario (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         nome VARCHAR(150) NOT NULL,
                         cpf VARCHAR(14) NOT NULL UNIQUE,
                         endereco_id BIGINT NOT NULL,
                         login VARCHAR(100) NOT NULL UNIQUE,
                         senha VARCHAR(255) NOT NULL,

                         CONSTRAINT fk_usuario_endereco
                             FOREIGN KEY (endereco_id)
                                 REFERENCES endereco(id)
                                 ON DELETE CASCADE
);

-- =========================
-- AUTOR
-- =========================
CREATE TABLE autor (
                       id BIGINT PRIMARY KEY,

                       CONSTRAINT fk_autor_usuario
                           FOREIGN KEY (id)
                               REFERENCES usuario(id)
                               ON DELETE CASCADE
);

-- =========================
-- AVALIADOR
-- =========================
CREATE TABLE avaliador (
                           id BIGINT PRIMARY KEY,

                           CONSTRAINT fk_avaliador_usuario
                               FOREIGN KEY (id)
                                   REFERENCES usuario(id)
                                   ON DELETE CASCADE
);

-- =========================
-- GERENTE
-- =========================
CREATE TABLE gerente (
                         id BIGINT PRIMARY KEY,

                         CONSTRAINT fk_gerente_usuario
                             FOREIGN KEY (id)
                                 REFERENCES usuario(id)
                                 ON DELETE CASCADE
);

-- =========================
-- OBRA
-- =========================
CREATE TABLE obra (
                      id VARCHAR(30) PRIMARY KEY,
                      titulo VARCHAR(200) NOT NULL,
                      genero VARCHAR(100) NOT NULL,
                      ano INT NOT NULL,
                      status INT NOT NULL,

                      autor_id BIGINT NOT NULL,
                      avaliador_id BIGINT NULL,

                      data_submissao DATE NOT NULL,
                      data_avaliacao DATE NULL,

    -- Caminho do arquivo (txt/pdf/docx) que o autor anexou com o
    -- conteúdo da obra a ser avaliado. Veja util/ArquivoObra.java.
                      arquivo VARCHAR(500) NULL,

                      CONSTRAINT fk_obra_autor
                          FOREIGN KEY (autor_id)
                              REFERENCES autor(id)
                              ON DELETE CASCADE,

                      CONSTRAINT fk_obra_avaliador
                          FOREIGN KEY (avaliador_id)
                              REFERENCES avaliador(id)
                              ON DELETE SET NULL
);

-- =========================
-- NOTIFICACAO
-- =========================
-- Gerada pelos observadores de ObraService (Observer pattern) a cada evento
-- do ciclo de vida da obra. Veja Model/service/ObraEventListener.java e
-- Model/service/NotificacaoObserver.java.
CREATE TABLE notificacao (
                             id BIGINT AUTO_INCREMENT PRIMARY KEY,
                             usuario_id BIGINT NOT NULL,
                             mensagem VARCHAR(500) NOT NULL,
                             lida BOOLEAN NOT NULL DEFAULT FALSE,
                             data_criacao DATETIME NOT NULL,

                             CONSTRAINT fk_notificacao_usuario
                                 FOREIGN KEY (usuario_id)
                                     REFERENCES usuario(id)
                                     ON DELETE CASCADE
);