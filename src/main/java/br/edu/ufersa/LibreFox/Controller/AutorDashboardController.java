package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.entities.Autor;
import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.Model.exceptions.AcessoNegadoException;
import br.edu.ufersa.LibreFox.Model.service.ObraService;
import br.edu.ufersa.LibreFox.util.ArquivoObra;
import br.edu.ufersa.LibreFox.util.Conexao;
import br.edu.ufersa.LibreFox.util.Icones;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AutorDashboardController implements DashboardController {

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
    @FXML private TableColumn<Obra, String> colStatus;
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
            // Regra de negócio g): o autor só visualiza as próprias obras.
            List<Obra> obras = obraService.listarObrasDoAutor(sessao);

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
                String icone = null;
                if (item.equals("Em análise")) {
                    label.getStyleClass().add("badge-avaliacao");
                    icone = "status-analise.png";
                } else if (item.equals("Aprovado")) {
                    label.getStyleClass().add("badge-aprovado");
                    icone = "status-aprovado.png";
                } else if (item.equals("Rejeitado")) {
                    label.getStyleClass().add("badge-rejeitado");
                    icone = "status-rejeitado.png";
                }
                if (icone != null) label.setGraphic(Icones.icone(icone, 14));
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
            private final Button btnEditar = new Button();
            {
                btnEditar.setGraphic(Icones.icone("editar.png", 16));
                btnEditar.getStyleClass().add("btn-acao");
                btnEditar.setOnAction(e -> {
                    Obra obra = getTableView().getItems().get(getIndex());
                    mostrarAlerta("Info", "Edição de \"" + obra.getTitulo() + "\" em desenvolvimento.");
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
                setGraphic(obra.getStatus() == 0 ? btnEditar : null);
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
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Nova obra");
        dialog.setHeaderText("Enviar nova obra para avaliação");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField campoTitulo = new TextField();
        campoTitulo.setPromptText("Título da obra");
        TextField campoGenero = new TextField();
        campoGenero.setPromptText("Gênero");
        ComboBox<String> cmbAno = new ComboBox<>();
        for (int ano = LocalDate.now().getYear(); ano >= 2020; ano--) {
            cmbAno.getItems().add(String.valueOf(ano));
        }
        cmbAno.getSelectionModel().selectFirst();

        // Seleção do arquivo da obra (o conteúdo a ser avaliado).
        final File[] arquivoSelecionado = new File[1];
        Button btnEscolher = new Button("Escolher arquivo...");
        Label lblArquivo = new Label("Nenhum arquivo selecionado");
        btnEscolher.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Selecionar arquivo da obra");
            fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(
                    "Documentos (txt, pdf, docx)", "*.txt", "*.pdf", "*.docx"));
            File escolhido = fc.showOpenDialog(tblObras.getScene().getWindow());
            if (escolhido != null) {
                arquivoSelecionado[0] = escolhido;
                lblArquivo.setText(escolhido.getName());
            }
        });
        HBox boxArquivo = new HBox(8, btnEscolher, lblArquivo);

        grid.add(new Label("Título:"), 0, 0);
        grid.add(campoTitulo, 1, 0);
        grid.add(new Label("Gênero:"), 0, 1);
        grid.add(campoGenero, 1, 1);
        grid.add(new Label("Ano:"), 0, 2);
        grid.add(cmbAno, 1, 2);
        grid.add(new Label("Arquivo:"), 0, 3);
        grid.add(boxArquivo, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response != ButtonType.OK) return;

            String titulo = campoTitulo.getText().trim();
            String genero = campoGenero.getText().trim();
            if (titulo.isEmpty() || genero.isEmpty()) {
                mostrarAlerta("Aviso", "Preencha título e gênero.");
                return;
            }
            // Toda obra precisa de um arquivo para poder ser avaliada.
            if (arquivoSelecionado[0] == null) {
                mostrarAlerta("Aviso", "Selecione o arquivo da obra (txt, pdf ou docx).");
                return;
            }
            if (!ArquivoObra.extensaoValida(arquivoSelecionado[0])) {
                mostrarAlerta("Aviso", "O arquivo deve ser do tipo txt, pdf ou docx.");
                return;
            }

            try (Connection conn = Conexao.getConnection()) {
                Autor autor = (Autor) sessao.getUsuario();
                short ano = Short.parseShort(cmbAno.getValue());
                // Status e id são definidos pelo ObraService.submeter (entra "em avaliação").
                Obra obra = new Obra(titulo, genero, ano, ObraService.EM_AVALIACAO, autor, null);
                // Copia o arquivo para o armazenamento local e guarda o caminho.
                obra.setArquivo(ArquivoObra.armazenar(arquivoSelecionado[0]));

                new ObraService(conn).submeter(obra, sessao);
                carregarDados();
                mostrarAlerta("Sucesso", "Obra enviada para avaliação com sucesso!");

            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Erro ao salvar o arquivo da obra: " + e.getMessage());
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Erro", "Erro ao enviar obra: " + e.getMessage());
            } catch (AcessoNegadoException e) {
                mostrarAlerta("Acesso negado", e.getMessage());
            } catch (NumberFormatException e) {
                mostrarAlerta("Erro", "Ano inválido!");
            }
        });
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