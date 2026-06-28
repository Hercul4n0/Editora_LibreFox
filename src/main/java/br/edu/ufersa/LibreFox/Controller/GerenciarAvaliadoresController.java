package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.DAO.AvaliadorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.ObraDAO;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Endereco;
import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.util.Conexao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.HBox;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class GerenciarAvaliadoresController implements DashboardController {

    @FXML private TextField campoBusca;
    @FXML private TableView<Avaliador> tblAvaliadores;
    @FXML private TableColumn<Avaliador, String> colNome;
    @FXML private TableColumn<Avaliador, String> colCpf;
    @FXML private TableColumn<Avaliador, String> colEndereco;
    @FXML private TableColumn<Avaliador, String> colObrasAtribuidas;
    @FXML private TableColumn<Avaliador, String> colAcoes;

    private Sessao sessao;
    private ObservableList<Avaliador> avaliadoresList = FXCollections.observableArrayList();

    @Override
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
        carregarDados();
    }

    @Override
    public void carregarDados() {
        try (Connection conn = Conexao.getConnection()) {
            AvaliadorDAO avaliadorDAO = new AvaliadorDAO(conn);
            List<Avaliador> avaliadores = avaliadorDAO.listar();

            // Carregar obras atribuídas para cada avaliador
            ObraDAO obraDAO = new ObraDAO(conn);
            for (Avaliador avaliador : avaliadores) {
                List<Obra> obras = obraDAO.buscarPorAvaliador(avaliador.getId());
                avaliador.getObrasParaAvaliar().addAll(obras);
            }

            avaliadoresList.setAll(avaliadores);
            configurarTabela();
            tblAvaliadores.setItems(avaliadoresList);

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
        colEndereco.setCellValueFactory(cell -> {
            Endereco e = cell.getValue().getEndereco();
            return new SimpleStringProperty(
                    e.getLogradouro() + ", " + e.getNumero() + " - " + e.getCidade() + "/" + e.getUf()
            );
        });
        colObrasAtribuidas.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().getObrasParaAvaliar().size())));

        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("✏️ Editar");
            private final Button btnExcluir = new Button("🗑️ Excluir");
            private final Button btnVerObras = new Button("📚 Obras");
            {
                btnEditar.getStyleClass().addAll("btn-acao", "btn-acao-azul");
                btnExcluir.getStyleClass().addAll("btn-acao", "btn-acao-vermelho");
                btnVerObras.getStyleClass().addAll("btn-acao", "btn-acao-verde");

                btnEditar.setOnAction(e -> {
                    Avaliador avaliador = getTableView().getItems().get(getIndex());
                    editarAvaliador(avaliador);
                });
                btnExcluir.setOnAction(e -> {
                    Avaliador avaliador = getTableView().getItems().get(getIndex());
                    confirmarExclusao(avaliador);
                });
                btnVerObras.setOnAction(e -> {
                    Avaliador avaliador = getTableView().getItems().get(getIndex());
                    mostrarObrasAtribuidas(avaliador);
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                HBox box = new HBox(8, btnEditar, btnVerObras, btnExcluir);
                setGraphic(box);
            }
        });
    }

    private void editarAvaliador(Avaliador avaliador) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Avaliador");
        dialog.setHeaderText("Alterar dados do avaliador: " + avaliador.getNome());

        // Botões
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField campoNome = new TextField(avaliador.getNome());
        TextField campoCpf = new TextField(avaliador.getCpf());
        TextField campoLogin = new TextField(avaliador.getLogin());
        PasswordField campoSenha = new PasswordField();
        campoSenha.setText(avaliador.getSenha());

        Endereco end = avaliador.getEndereco();
        TextField campoLogradouro = new TextField(end.getLogradouro());
        TextField campoNumero = new TextField(end.getNumero());
        TextField campoBairro = new TextField(end.getBairro());
        TextField campoCidade = new TextField(end.getCidade());
        TextField campoUf = new TextField(end.getUf());

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(campoNome, 1, 0);
        grid.add(new Label("CPF:"), 0, 1);
        grid.add(campoCpf, 1, 1);
        grid.add(new Label("Login:"), 0, 2);
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

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = Conexao.getConnection()) {
                    // Atualizar endereço
                    end.setLogradouro(campoLogradouro.getText().trim());
                    end.setNumero(campoNumero.getText().trim());
                    end.setBairro(campoBairro.getText().trim());
                    end.setCidade(campoCidade.getText().trim());
                    end.setUf(campoUf.getText().trim());

                    // Atualizar avaliador
                    avaliador.setNome(campoNome.getText().trim());
                    avaliador.setCpf(campoCpf.getText().trim());
                    avaliador.setLogin(campoLogin.getText().trim());
                    if (!campoSenha.getText().isEmpty()) {
                        avaliador.setSenha(campoSenha.getText().trim());
                    }

                    new AvaliadorDAO(conn).atualizar(avaliador);
                    carregarDados();
                    mostrarAlertaInfo("Sucesso", "Avaliador atualizado com sucesso!");

                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao atualizar: " + e.getMessage());
                }
            }
        });
    }

    private void confirmarExclusao(Avaliador avaliador) {
        // Verificar se o avaliador tem obras atribuídas
        if (!avaliador.getObrasParaAvaliar().isEmpty()) {
            mostrarAlerta("Aviso",
                    "Não é possível excluir este avaliador pois ele possui obras atribuídas.\n" +
                            "Reatribua as obras antes de excluí-lo.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText("Excluir avaliador");
        alert.setContentText("Tem certeza que deseja excluir " + avaliador.getNome() + "?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = Conexao.getConnection()) {
                    new AvaliadorDAO(conn).deletar(avaliador);
                    avaliadoresList.remove(avaliador);
                    mostrarAlertaInfo("Sucesso", "Avaliador excluído com sucesso!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao excluir: " + e.getMessage());
                }
            }
        });
    }

    private void mostrarObrasAtribuidas(Avaliador avaliador) {
        StringBuilder obras = new StringBuilder();
        for (Obra obra : avaliador.getObrasParaAvaliar()) {
            obras.append("• ").append(obra.getTitulo())
                    .append(" (").append(obra.getAno()).append(")")
                    .append(" - ").append(obra.getAutor().getNome())
                    .append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Obras atribuídas");
        alert.setHeaderText("Obras atribuídas a " + avaliador.getNome());
        alert.setContentText(obras.toString().isEmpty() ?
                "Nenhuma obra atribuída." : obras.toString());
        alert.getDialogPane().setMinHeight(300);
        alert.showAndWait();
    }

    @FXML
    private void handleBuscar() {
        String termo = campoBusca.getText().trim();
        if (termo.isEmpty()) {
            carregarDados();
            return;
        }

        try (Connection conn = Conexao.getConnection()) {
            AvaliadorDAO avaliadorDAO = new AvaliadorDAO(conn);
            List<Avaliador> resultados = avaliadorDAO.buscarPorNome(termo);
            avaliadoresList.setAll(resultados);
            tblAvaliadores.setItems(avaliadoresList);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro na busca: " + e.getMessage());
        }
    }

    @FXML
    private void handleNovoAvaliador() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Novo Avaliador");
        dialog.setHeaderText("Cadastrar novo avaliador");

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField campoNome = new TextField();
        campoNome.setPromptText("Nome completo");
        TextField campoCpf = new TextField();
        campoCpf.setPromptText("CPF (apenas números)");
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

        grid.add(new Label("Nome:"), 0, 0);
        grid.add(campoNome, 1, 0);
        grid.add(new Label("CPF:"), 0, 1);
        grid.add(campoCpf, 1, 1);
        grid.add(new Label("Login:"), 0, 2);
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

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                String nome = campoNome.getText().trim();
                String cpf = campoCpf.getText().trim();
                String login = campoLogin.getText().trim();
                String senha = campoSenha.getText().trim();

                if (nome.isEmpty() || cpf.isEmpty() || login.isEmpty() || senha.isEmpty()) {
                    mostrarAlerta("Aviso", "Preencha todos os campos obrigatórios!");
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

                    Avaliador avaliador = new Avaliador(nome, cpf, endereco, login, senha);
                    new AvaliadorDAO(conn).inserir(avaliador);

                    carregarDados();
                    mostrarAlertaInfo("Sucesso", "Avaliador cadastrado com sucesso!");

                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao cadastrar: " + e.getMessage());
                }
            }
        });
    }

    // Métodos de navegação
    @FXML private void navegarHome() { navegarPara("GerenteDashboardView.fxml"); }
    @FXML private void navegarAutores() { navegarPara("GerenciarAutoresView.fxml"); }
    @FXML private void navegarObras() { navegarPara("GerenciarObrasView.fxml"); }
    @FXML private void navegarRelatorios() { navegarPara("RelatoriosView.fxml"); }

    @FXML
    private void handleSair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblAvaliadores.getScene().getWindow();
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
            Stage stage = (Stage) tblAvaliadores.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource("/CSS/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();
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