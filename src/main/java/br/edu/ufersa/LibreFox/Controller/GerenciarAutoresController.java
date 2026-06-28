package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.DAO.AutorDAO;
import br.edu.ufersa.LibreFox.Model.entities.Autor;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
            private final Button btnEditar = new Button("✏️ Editar");
            private final Button btnExcluir = new Button("🗑️ Excluir");
            {
                btnEditar.getStyleClass().addAll("btn-acao", "btn-acao-azul");
                btnExcluir.getStyleClass().addAll("btn-acao", "btn-acao-vermelho");
                btnEditar.setOnAction(e -> {
                    Autor autor = getTableView().getItems().get(getIndex());
                    // TODO: Abrir diálogo de edição
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
        // TODO: Abrir diálogo para criar novo autor
        mostrarAlertaInfo("Info", "Funcionalidade de criação de autor em desenvolvimento.");
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
            Stage stage = (Stage) tblAutores.getScene().getWindow();
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