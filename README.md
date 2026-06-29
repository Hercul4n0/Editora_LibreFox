# LibreFox

Sistema de gestão para a editora do Sr. Paulão — um software desktop, feito em **Java + JavaFX**, para controlar o fluxo de submissão, avaliação e publicação de obras, com três perfis de acesso: **Autor**, **Avaliador** e **Gerente**.

Projeto acadêmico desenvolvido para a disciplina de Programação Orientada a Objetos (UFERSA).

## Sumário

- [O que o sistema faz](#o-que-o-sistema-faz)
- [Pré-requisitos](#pré-requisitos)
- [1. Baixando o projeto](#1-baixando-o-projeto)
- [2. Configurando o banco de dados](#2-configurando-o-banco-de-dados)
- [3. Configurando a conexão no projeto](#3-configurando-a-conexão-no-projeto)
- [4. Executando o sistema](#4-executando-o-sistema)
- [5. Contas de teste já cadastradas](#5-contas-de-teste-já-cadastradas)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Problemas comuns](#problemas-comuns)

## O que o sistema faz

- **Autor**: cria sua própria conta, submete obras (anexando o arquivo do texto), acompanha o status (em avaliação, aceita, rejeitada) e lê o feedback do avaliador.
- **Avaliador**: visualiza as obras que o gerente designou para ele, baixa o arquivo anexado, e decide aceitar ou rejeitar, deixando um comentário (feedback) para o autor.
- **Gerente**: cadastra e gerencia autores e avaliadores (podendo promover um autor já existente a também ser avaliador), designa qual avaliador cuida de cada obra, acompanha tudo em "Gerenciar obras" e gera relatórios por período.

Login é único para os três perfis: cada usuário escolhe, na tela de login, com qual perfil quer entrar (um mesmo usuário pode acumular mais de um perfil).

## Pré-requisitos

Antes de começar, tenha instalado:

| Ferramenta | Versão mínima | Para que serve |
|---|---|---|
| [JDK (Java)](https://www.oracle.com/java/technologies/downloads/) | 21 | Compilar e rodar o projeto |
| [Maven](https://maven.apache.org/download.cgi) | 3.9+ | Baixar as dependências e empacotar o projeto |
| [MySQL Server](https://dev.mysql.com/downloads/mysql/) | 8.0+ | Banco de dados |
| Uma IDE (opcional, mas recomendado) | — | [IntelliJ IDEA](https://www.jetbrains.com/idea/) facilita bastante |

> Se você usa o **MySQL Workbench**, **DBeaver** ou o **MySQL Shell**, qualquer um deles serve para os passos do banco — use o que já tiver instalado.

## 1. Baixando o projeto

Clone o repositório:

```bash
git clone https://github.com/Hercul4n0/Editora_LibreFox.git
cd Editora_LibreFox
```

Ou baixe o `.zip` do projeto e extraia em uma pasta de sua preferência.

## 2. Configurando o banco de dados

Os scripts SQL ficam na pasta **`sql/`**, na raiz do projeto. Eles precisam ser executados **nesta ordem exata**, pois cada um depende do anterior:

| Ordem | Arquivo | O que faz |
|---|---|---|
| 1 | `schema.sql` | Cria o banco `librefox_editora` e todas as tabelas |
| 2 | `seed.sql` | Insere 3 contas de teste (um Autor, um Avaliador, um Gerente) e algumas obras de exemplo |
| 3 | `migracao_usuario_perfil.sql` | Cria a tabela que controla os perfis de cada usuário |
| 4 | `migracao_arquivo_obra.sql` | Adiciona a coluna para guardar o arquivo anexado da obra |
| 5 | `migracao_feedback_obra.sql` | Adiciona a coluna para guardar o feedback do avaliador |

### Opção A — Pelo terminal

```bash
mysql -u root -p < sql/schema.sql
mysql -u root -p librefox_editora < sql/seed.sql
mysql -u root -p librefox_editora < sql/migracao_usuario_perfil.sql
mysql -u root -p librefox_editora < sql/migracao_arquivo_obra.sql
mysql -u root -p librefox_editora < sql/migracao_feedback_obra.sql
```

(vai pedir sua senha do MySQL a cada comando)

### Opção B — Pelo MySQL Shell

```sql
\sql
\source sql/schema.sql
\source sql/seed.sql
\source sql/migracao_usuario_perfil.sql
\source sql/migracao_arquivo_obra.sql
\source sql/migracao_feedback_obra.sql
```

### Opção C — Pelo MySQL Workbench / DBeaver

Abra cada arquivo (na ordem da tabela acima) em uma aba de query nova e execute todo o conteúdo, um arquivo por vez.

> **Como confirmar que funcionou:** a última migração (`migracao_feedback_obra.sql`) termina com um `DESCRIBE obra;` — se aparecer a lista de colunas da tabela, incluindo `feedback` no final, está tudo certo.

> Se em algum momento o app der erro de `Table 'usuario_perfil' doesn't exist` ou de chave estrangeira ao salvar algo, existe um script de pronto-socorro em `sql/sincronizar_perfis.sql` — ele resincroniza tudo sem apagar dados. Veja [Problemas comuns](#problemas-comuns).

## 3. Configurando a conexão no projeto

Abra o arquivo:

```
src/main/java/br/edu/ufersa/LibreFox/util/Conexao.java
```

E ajuste usuário e senha para os do **seu** MySQL local:

```java
private static final String URL  = "jdbc:mysql://localhost:3306/librefox_editora";
private static final String USER = "root";
private static final String PASS = "root"; // <- troque para a sua senha real
```

> Se você não souber sua senha do MySQL, teste no terminal: `mysql -u root -p` — a senha que funcionar aí é a que deve entrar em `PASS`. Se seu MySQL não tiver senha (comum em instalações via XAMPP/WAMP), deixe `PASS = "";`.

## 4. Executando o sistema

### Opção A — Pela IDE (mais simples)

1. Abra a pasta do projeto na IntelliJ IDEA (ou outra IDE com suporte a Maven).
2. Espere o Maven baixar as dependências automaticamente (ícone de carregamento no canto).
3. Abra o arquivo `src/main/java/br/edu/ufersa/LibreFox/TesteJavaFX.java`.
4. Clique no botão ▶️ (Run) ao lado do método `main`.

### Opção B — Pelo terminal, com Maven

Na raiz do projeto:

```bash
mvn clean javafx:run
```

Isso baixa as dependências (na primeira vez), compila e abre a tela de login.

> **Não execute a classe `Teste.java`** — ela só testa se a conexão com o banco está funcionando, mas não abre a interface. Para usar o sistema de fato, é sempre o **`TesteJavaFX.java`**.

## 5. Contas de teste já cadastradas

Se você rodou o `seed.sql`, já existem estas contas prontas para explorar cada perfil (senha igual para todas: **`123456`**):

| Perfil | Login | Senha |
|---|---|---|
| Autor | `pedro.autor` | `123456` |
| Avaliador | `ana.avaliadora` | `123456` |
| Gerente | `lucas.gerente` | `123456` |

Na tela de login, **selecione o botão do perfil correspondente** antes de clicar em "Entrar".

Também é possível criar uma nova conta de Autor pela própria tela de login ("Crie uma conta!"). Para cadastrar um Avaliador, é preciso logar como Gerente e usar a tela "Gerenciar avaliadores".

## Estrutura do projeto

```
Editora_LibreFox/
├── sql/                          → scripts de criação e migração do banco
├── obras_arquivos/                → arquivos anexados pelos autores (criada automaticamente)
└── src/main/
    ├── java/br/edu/ufersa/LibreFox/
    │   ├── TesteJavaFX.java       → ponto de entrada da aplicação (execute este)
    │   ├── Controller/            → um controller por tela (FXML)
    │   ├── Model/
    │   │   ├── entities/          → Usuario, Autor, Avaliador, Gerente, Obra...
    │   │   ├── DAO/                → acesso ao banco (um DAO por entidade)
    │   │   ├── service/            → regras de negócio e autorização por perfil
    │   │   └── exceptions/         → exceções próprias do domínio
    │   └── util/                   → conexão com o banco, ícones, manipulação de arquivos
    └── resources/
        ├── Views/                  → telas (.fxml)
        ├── CSS/                    → estilo visual (style.css)
        └── Images/                 → ícones usados na interface
```
## Diagrama UML

Para ver o diagrama UML, basta abrir a pasta *docs* e abrir o diagrama-uml.md no proprio github

## Problemas comuns

**`Access denied for user 'root'@'localhost'`**
A senha em `Conexao.java` não é a senha real do seu MySQL. Revise o passo 3.

**`Unknown database 'librefox_editora'`**
O `schema.sql` ainda não foi executado, ou foi executado em outro servidor MySQL. Revise o passo 2.

**`Table 'librefox_editora.usuario_perfil' doesn't exist`**
Faltou rodar `migracao_usuario_perfil.sql`. Revise o passo 2.

**`Cannot add or update a child row: a foreign key constraint fails ... autor_id`**
Algum usuário tem perfil de Autor cadastrado, mas está sem a linha correspondente na tabela `autor` (mesma lógica vale para Avaliador/Gerente). Rode `sql/sincronizar_perfis.sql` — ele corrige isso sem apagar nada.

**A tela abre, mas sem nenhuma cor/estilo (tudo cinza padrão)**
O CSS não foi encontrado. Confirme que está executando `TesteJavaFX.java` pela raiz do projeto (não por um caminho/diretório de trabalho diferente).

**Erro ao anexar o arquivo da obra**
Confira se a migração `migracao_arquivo_obra.sql` foi executada — sem ela, a coluna que guarda o caminho do arquivo não existe no banco.
