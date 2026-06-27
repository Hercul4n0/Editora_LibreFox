package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.Model.exceptions.AcessoNegadoException;
import br.edu.ufersa.LibreFox.Model.service.ObraService;
import br.edu.ufersa.LibreFox.util.Conexao;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class AvaliadorDashboardController implements DashboardController {

    private static final String CSS_PATH = "/CSS/style.css";
    private static final String VIEW_LOGIN = "/Views/LoginView.fxml";

    @FXML private Label lblSaudacao;
    @FXML private Label lblEmAvaliacao;
    @FXML private Label lblAceitas;
    @FXML private Label lblRejeitadas;
    @FXML private TextField campoBusca;
    @FXML private TableView<Obra> tblObras;
    @FXML private TableColumn<Obra, String> colTitulo;
    @FXML private TableColumn<Obra, String> colAno;
    @FXML private TableColumn<Obra, String> colGenero;
    @FXML private TableColumn<Obra, String> colAutor;
    @FXML private TableColumn<Obra, String> colFeedback;
    @FXML private TableColumn<Obra, String> colAcoes;

    private Sessao sessao;
    private final ObservableList<Obra> obrasList = FXCollections.observableArrayList();

    @Override
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
        lblSaudacao.setText("Olá, " + sessao.getNomeUsuario() + "!");
        carregarDados();
    }

    @Override
    public void carregarDados() {
        try (Connection conn = Conexao.getConnection()) {
            ObraService obraService = new ObraService(conn);
            // Regra de negócio g): o avaliador só visualiza as obras designadas a ele.
            List<Obra> obras = obraService.listarObrasDoAvaliador(sessao);

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
        } catch (AcessoNegadoException e) {
            mostrarAlerta("Acesso negado", e.getMessage());
        }
    }

    private void configurarTabela() {
        colTitulo.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitulo()));
        colAno.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().getAno())));
        colGenero.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getGenero()));
        colAutor.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAutor().getNome()));
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
            private final Button btnAvaliar = new Button("📋 Avaliar");
            {
                btnAvaliar.getStyleClass().addAll("btn-acao", "btn-acao-verde");
                btnAvaliar.setOnAction(e -> {
                    Obra obra = getTableView().getItems().get(getIndex());
                    abrirDialogoAvaliacao(obra);
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
                setGraphic(obra.getStatus() == 0 ? btnAvaliar : null);
            }
        });
    }

    /**
     * Abre o diálogo "Aceitar obra / Rejeitar obra" (espelha a tela do
     * protótipo) e persiste o veredicto através do ObraService, que garante
     * que somente o avaliador designado pode avaliar a obra.
     */
    private void abrirDialogoAvaliacao(Obra obra) {
        Dialog<Short> dialog = new Dialog<>();
        dialog.setTitle("Avaliar obra");
        dialog.setHeaderText("Avaliar: " + obra.getTitulo());

        ButtonType btAceitar = new ButtonType("Aceitar obra", ButtonBar.ButtonData.OK_DONE);
        ButtonType btRejeitar = new ButtonType("Rejeitar obra", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btAceitar, btRejeitar, ButtonType.CANCEL);

        TextArea campoFeedback = new TextArea();
        campoFeedback.setPromptText("Escreva um feedback curto sugerindo ajustes e correções ao autor (opcional)");
        campoFeedback.setPrefRowCount(4);

        VBox content = new VBox(10,
                new Label("Feedback (opcional)"),
                campoFeedback);
        dialog.getDialogPane().setContent(content);

        dialog.setResultConverter(button -> {
            if (button == btAceitar) return ObraService.ACEITA;
            if (button == btRejeitar) return ObraService.REJEITADA;
            return null;
        });

        dialog.showAndWait().ifPresent(novoStatus -> {
            try (Connection conn = Conexao.getConnection()) {
                ObraService obraService = new ObraService(conn);
                obraService.avaliar(obra, novoStatus, sessao);
                carregarDados();
                mostrarAlerta("Sucesso", novoStatus == ObraService.ACEITA
                        ? "Obra aceita com sucesso!"
                        : "Obra rejeitada com sucesso!");
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Erro ao registrar avaliação: " + e.getMessage());
            } catch (AcessoNegadoException e) {
                mostrarAlerta("Acesso negado", e.getMessage());
            } catch (IllegalStateException e) {
                mostrarAlerta("Aviso", e.getMessage());
            }
        });
    }

    @FXML
    private void navegarHome() {
        // Já está na home
    }

    @FXML
    private void navegarObrasAtribuidas() {
        // Já está na página de obras atribuídas
    }

    @FXML
    private void handleSair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_LOGIN));
            Parent root = loader.load();

            Stage stage = (Stage) tblObras.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());
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