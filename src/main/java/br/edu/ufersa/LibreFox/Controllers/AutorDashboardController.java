package br.edu.ufersa.LibreFox.Controllers;

import br.edu.ufersa.LibreFox.Model.DAO.ObraDAO;
import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.util.Conexao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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

public class AutorDashboardController implements DashboardController {

    @FXML private Label lblSaudacao;
    @FXML private Label lblEmAvaliacao;
    @FXML private Label lblAceitas;
    @FXML private Label lblRejeitadas;
    @FXML private TextField campoBusca;
    @FXML private TableView<Obra> tblObras;
    @FXML private TableColumn<Obra, String> colTitulo;
    @FXML private TableColumn<Obra, String> colAno;
    @FXML private TableColumn<Obra, String> colGenero;
    @FXML private TableColumn<Obra, String> colStatus;
    @FXML private TableColumn<Obra, String> colFeedback;
    @FXML private TableColumn<Obra, String> colAcoes;

    private Sessao sessao;
    private ObservableList<Obra> obrasList = FXCollections.observableArrayList();

    @Override
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
        lblSaudacao.setText("Olá, " + sessao.getNomeUsuario() + "!");
        carregarDados();
    }

    @Override
    public void carregarDados() {
        try (Connection conn = Conexao.getConnection()) {
            ObraDAO obraDAO = new ObraDAO(conn);
            List<Obra> obras = obraDAO.buscarPorAutor(sessao.getUsuarioId());

            long emAvaliacao = obras.stream().filter(o -> o.getStatus() == 0).count();
            long aceitas = obras.stream().filter(o -> o.getStatus() == 1).count();
            long rejeitadas = obras.stream().filter(o -> o.getStatus() == 2).count();

            lblEmAvaliacao.setText(String.valueOf(emAvaliacao));
            lblAceitas.setText(String.valueOf(aceitas));
            lblRejeitadas.setText(String.valueOf(rejeitadas));

            obrasList.setAll(obras);
            configurarTabela();
            tblObras.setItems(obrasList);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void configurarTabela() {
        colTitulo.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitulo()));
        colAno.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().getAno())));
        colGenero.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getGenero()));
        colStatus.setCellValueFactory(cell -> {
            short status = cell.getValue().getStatus();
            String texto = switch (status) {
                case 0 -> "Em análise";
                case 1 -> "Aprovado";
                case 2 -> "Rejeitado";
                default -> "Desconhecido";
            };
            return new SimpleStringProperty(texto);
        });
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }
                Label label = new Label(item);
                label.getStyleClass().add("badge-status");
                if (item.equals("Em análise")) {
                    label.getStyleClass().add("badge-avaliacao");
                } else if (item.equals("Aprovado")) {
                    label.getStyleClass().add("badge-aprovado");
                } else if (item.equals("Rejeitado")) {
                    label.getStyleClass().add("badge-rejeitado");
                }
                setGraphic(label);
                setText(null);
            }
        });
        colFeedback.setCellValueFactory(cell -> {
            String feedback = "Não";
            if (cell.getValue().getStatus() == 1) {
                feedback = "Aceito";
            } else if (cell.getValue().getStatus() == 2) {
                feedback = "Rejeitado";
            }
            return new SimpleStringProperty(feedback);
        });
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnEditar = new Button("✏️");
            {
                btnEditar.getStyleClass().add("btn-acao");
                btnEditar.setOnAction(e -> {
                    Obra obra = getTableView().getItems().get(getIndex());
                    // TODO: Abrir tela de edição
                });
            }
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                Obra obra = getTableView().getItems().get(getIndex());
                if (obra.getStatus() == 0) {
                    setGraphic(btnEditar);
                } else {
                    setGraphic(null);
                }
            }
        });
    }

    @FXML
    private void navegarHome() {
        // Já está na home
    }

    @FXML
    private void navegarMinhasObras() {
        // Já está na página de minhas obras
    }

    @FXML
    private void handleNovaObra() {
        // TODO: Abrir diálogo para criar nova obra
        mostrarAlerta("Info", "Funcionalidade de criação de obra em desenvolvimento.");
    }

    @FXML
    private void handleSair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/LibreFox/view/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) tblObras.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/br/edu/ufersa/LibreFox/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}
