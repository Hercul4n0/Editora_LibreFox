package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.DAO.AutorDAO;
import br.edu.ufersa.LibreFox.Model.entities.Autor;
import br.edu.ufersa.LibreFox.Model.entities.Endereco;
import br.edu.ufersa.LibreFox.Model.entities.Perfil;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.util.Conexao;
import br.edu.ufersa.LibreFox.util.Icones;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

public class GerenciarAutoresController implements DashboardController {

    @FXML private TextField campoBusca;
    @FXML private TableView<Autor> tblAutores;
    @FXML private TableColumn<Autor, String> colNome;
    @FXML private TableColumn<Autor, String> colCpf;
    @FXML private TableColumn<Autor, String> colEmail;
    @FXML private TableColumn<Autor, String> colObras;
    @FXML private TableColumn<Autor, String> colAcoes;

    private Sessao sessao;
    private ObservableList<Autor> autoresList = FXCollections.observableArrayList();
    private AutorDAO autorDAO;

    @Override
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
        carregarDados();
    }

    @Override
    public void carregarDados() {
        try (Connection conn = Conexao.getConnection()) {
            autorDAO = new AutorDAO(conn);
            List<Autor> autores = autorDAO.listar();

            autoresList.setAll(autores);
            configurarTabela();
            tblAutores.setItems(autoresList);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void configurarTabela() {
        colNome.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getNome()));
        colCpf.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getCpf()));
        colEmail.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getLogin()));
        colObras.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().getObrasEnviadas().size())));

        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button();
            private final Button btnExcluir = new Button();
            {
                btnEditar.setGraphic(Icones.icone("editar.png", 16));
                btnExcluir.setGraphic(Icones.icone("deletar-lixeira.png", 16));
                btnEditar.getStyleClass().addAll("btn-acao", "btn-acao-azul");
                btnExcluir.getStyleClass().addAll("btn-acao", "btn-acao-vermelho");
                btnEditar.setTooltip(new Tooltip("Editar"));
                btnExcluir.setTooltip(new Tooltip("Excluir"));
                btnEditar.setOnAction(e -> {
                    Autor autor = getTableView().getItems().get(getIndex());
                    editarAutor(autor);
                });
                btnExcluir.setOnAction(e -> {
                    Autor autor = getTableView().getItems().get(getIndex());
                    confirmarExclusao(autor);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                HBox box = new HBox(8, btnEditar, btnExcluir);
                setGraphic(box);
            }
        });
    }

    private void confirmarExclusao(Autor autor) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText("Excluir autor");
        alert.setContentText("Tem certeza que deseja excluir " + autor.getNome() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = Conexao.getConnection()) {
                    new AutorDAO(conn).deletar(autor);
                    autoresList.remove(autor);
                    mostrarAlertaInfo("Sucesso", "Autor excluído com sucesso!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao excluir: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleBuscar() {
        String termo = campoBusca.getText().trim();
        if (termo.isEmpty()) {
            carregarDados();
            return;
        }

        try (Connection conn = Conexao.getConnection()) {
            autorDAO = new AutorDAO(conn);
            List<Autor> resultados = autorDAO.buscarPorNome(termo);
            autoresList.setAll(resultados);
            tblAutores.setItems(autoresList);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro na busca: " + e.getMessage());
        }
    }

    @FXML
    private void handleNovoAutor() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Novo Autor");
        dialog.setHeaderText("Cadastrar novo autor");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome completo");
        TextField campoCpf = new TextField();
        campoCpf.setPromptText("Somente números");
        TextField campoLogin = new TextField();
        campoLogin.setPromptText("E-mail");
        PasswordField campoSenha = new PasswordField();
        campoSenha.setPromptText("Senha");
        TextField campoLogradouro = new TextField();
        campoLogradouro.setPromptText("Logradouro");
        TextField campoNumero = new TextField();
        campoNumero.setPromptText("Número");
        TextField campoBairro = new TextField();
        campoBairro.setPromptText("Bairro");
        TextField campoCidade = new TextField();
        campoCidade.setPromptText("Cidade");
        TextField campoUf = new TextField();
        campoUf.setPromptText("UF");
        CheckBox checkTambemAvaliador = new CheckBox("Também é avaliador");

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(campoNome, 1, 0);
        grid.add(new Label("CPF:"), 0, 1);
        grid.add(campoCpf, 1, 1);
        grid.add(new Label("E-mail:"), 0, 2);
        grid.add(campoLogin, 1, 2);
        grid.add(new Label("Senha:"), 0, 3);
        grid.add(campoSenha, 1, 3);
        grid.add(new Label("Logradouro:"), 0, 4);
        grid.add(campoLogradouro, 1, 4);
        grid.add(new Label("Número:"), 0, 5);
        grid.add(campoNumero, 1, 5);
        grid.add(new Label("Bairro:"), 0, 6);
        grid.add(campoBairro, 1, 6);
        grid.add(new Label("Cidade:"), 0, 7);
        grid.add(campoCidade, 1, 7);
        grid.add(new Label("UF:"), 0, 8);
        grid.add(campoUf, 1, 8);
        grid.add(checkTambemAvaliador, 1, 9);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response != ButtonType.OK) return;

            String nome = campoNome.getText().trim();
            String cpf = campoCpf.getText().trim().replaceAll("\\D", "");
            String login = campoLogin.getText().trim();
            String senha = campoSenha.getText().trim();

            if (nome.isEmpty() || cpf.isEmpty() || login.isEmpty() || senha.isEmpty()
                    || campoLogradouro.getText().trim().isEmpty()
                    || campoNumero.getText().trim().isEmpty()
                    || campoBairro.getText().trim().isEmpty()
                    || campoCidade.getText().trim().isEmpty()
                    || campoUf.getText().trim().isEmpty()) {
                mostrarAlerta("Aviso", "Preencha todos os campos obrigatórios!");
                return;
            }
            if (cpf.length() != 11) {
                mostrarAlerta("Aviso", "CPF inválido — deve ter 11 números.");
                return;
            }

            try (Connection conn = Conexao.getConnection()) {
                Endereco endereco = new Endereco(
                        campoNumero.getText().trim(),
                        campoBairro.getText().trim(),
                        campoLogradouro.getText().trim(),
                        campoCidade.getText().trim(),
                        campoUf.getText().trim()
                );

                Set<Perfil> perfis = EnumSet.of(Perfil.AUTOR);
                if (checkTambemAvaliador.isSelected()) perfis.add(Perfil.AVALIADOR);

                Autor autor = new Autor(nome, cpf, endereco, login, senha, perfis);
                new AutorDAO(conn).inserir(autor);

                carregarDados();
                mostrarAlertaInfo("Sucesso", "Autor cadastrado com sucesso!");

            } catch (SQLException e) {
                e.printStackTrace();
                if (e.getMessage() != null && e.getMessage().contains("usuario.cpf")) {
                    mostrarAlerta("Erro", "Já existe um usuário cadastrado com este CPF.");
                } else if (e.getMessage() != null && e.getMessage().contains("usuario.login")) {
                    mostrarAlerta("Erro", "Já existe um usuário cadastrado com este e-mail.");
                } else {
                    mostrarAlerta("Erro", "Erro ao cadastrar: " + e.getMessage());
                }
            } catch (RuntimeException e) {
                // Endereco lança RuntimeException se algum campo vier vazio.
                mostrarAlerta("Aviso", e.getMessage());
            }
        });
    }

    /**
     * Edita os dados de um autor já existente. Inclui a opção de também
     * designá-lo como Avaliador — regra de negócio: "apenas o gerente pode
     * cadastrar avaliadores, podendo ser novos funcionários ou autores que
     * já são cadastrados".
     */
    private void editarAutor(Autor autor) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Autor");
        dialog.setHeaderText("Alterar dados de: " + autor.getNome());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField campoNome = new TextField(autor.getNome());
        TextField campoCpf = new TextField(autor.getCpf());
        TextField campoLogin = new TextField(autor.getLogin());
        PasswordField campoSenha = new PasswordField();
        campoSenha.setText(autor.getSenha());

        Endereco end = autor.getEndereco();
        TextField campoLogradouro = new TextField(end.getLogradouro());
        TextField campoNumero = new TextField(end.getNumero());
        TextField campoBairro = new TextField(end.getBairro());
        TextField campoCidade = new TextField(end.getCidade());
        TextField campoUf = new TextField(end.getUf());

        CheckBox checkTambemAvaliador = new CheckBox("Também é avaliador");
        checkTambemAvaliador.setSelected(autor.temPerfil(Perfil.AVALIADOR));

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(campoNome, 1, 0);
        grid.add(new Label("CPF:"), 0, 1);
        grid.add(campoCpf, 1, 1);
        grid.add(new Label("E-mail:"), 0, 2);
        grid.add(campoLogin, 1, 2);
        grid.add(new Label("Senha:"), 0, 3);
        grid.add(campoSenha, 1, 3);
        grid.add(new Label("Logradouro:"), 0, 4);
        grid.add(campoLogradouro, 1, 4);
        grid.add(new Label("Número:"), 0, 5);
        grid.add(campoNumero, 1, 5);
        grid.add(new Label("Bairro:"), 0, 6);
        grid.add(campoBairro, 1, 6);
        grid.add(new Label("Cidade:"), 0, 7);
        grid.add(campoCidade, 1, 7);
        grid.add(new Label("UF:"), 0, 8);
        grid.add(campoUf, 1, 8);
        grid.add(checkTambemAvaliador, 1, 9);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response != ButtonType.OK) return;

            try (Connection conn = Conexao.getConnection()) {
                end.setLogradouro(campoLogradouro.getText().trim());
                end.setNumero(campoNumero.getText().trim());
                end.setBairro(campoBairro.getText().trim());
                end.setCidade(campoCidade.getText().trim());
                end.setUf(campoUf.getText().trim());

                autor.setNome(campoNome.getText().trim());
                autor.setCpf(campoCpf.getText().trim());
                autor.setLogin(campoLogin.getText().trim());
                if (!campoSenha.getText().isEmpty()) {
                    autor.setSenha(campoSenha.getText().trim());
                }

                // Perfis: mantém Autor sempre, soma/remove Avaliador conforme o checkbox.
                Set<Perfil> perfis = EnumSet.of(Perfil.AUTOR);
                if (checkTambemAvaliador.isSelected()) perfis.add(Perfil.AVALIADOR);
                autor.setPerfis(perfis);

                new AutorDAO(conn).atualizar(autor);
                carregarDados();
                mostrarAlertaInfo("Sucesso", "Autor atualizado com sucesso!");

            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Erro ao atualizar: " + e.getMessage());
            } catch (RuntimeException e) {
                mostrarAlerta("Aviso", e.getMessage());
            }
        });
    }

    // Métodos de navegação
    @FXML private void navegarHome() { navegarPara("GerenteDashboardView.fxml"); }
    @FXML private void navegarAvaliadores() { navegarPara("GerenciarAvaliadoresView.fxml"); }
    @FXML private void navegarObras() { navegarPara("GerenciarObrasView.fxml"); }
    @FXML private void navegarRelatorios() { navegarPara("RelatoriosView.fxml"); }

    @FXML
    private void handleSair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblAutores.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navegarPara(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/" + fxml));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).setSessao(sessao);
            }
            // Troca apenas o root da Scene atual, preservando o modo maximizado.
            tblAutores.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAlertaInfo(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}