package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Perfil;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.Model.entities.Usuario;
import br.edu.ufersa.LibreFox.Model.exceptions.AcessoNegadoException;
import br.edu.ufersa.LibreFox.Model.service.IObraService;
import br.edu.ufersa.LibreFox.Model.service.ObraService;
import br.edu.ufersa.LibreFox.Model.service.ObraServiceProxy;
import br.edu.ufersa.LibreFox.util.Conexao;
import br.edu.ufersa.LibreFox.util.Icones;
import br.edu.ufersa.LibreFox.util.SeletorPerfil;
import br.edu.ufersa.LibreFox.util.UsuarioLookup;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    @FXML private Button btnTrocarPerfil;

    private Sessao sessao;
    private final ObservableList<Obra> obrasList = FXCollections.observableArrayList();

    @Override
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
        lblSaudacao.setText("Olá, " + sessao.getNomeUsuario() + "!");
        if (btnTrocarPerfil != null) {
            boolean temMaisDeUmPerfil = sessao.getUsuario().getPerfis().size() > 1;
            btnTrocarPerfil.setVisible(temMaisDeUmPerfil);
            btnTrocarPerfil.setManaged(temMaisDeUmPerfil);
        }
        carregarDados();
    }

    /** Permite trocar para outro perfil da mesma conta, sem precisar logar de novo. */
    @FXML
    private void handleTrocarPerfil() {
        Set<Perfil> outros = new HashSet<>(sessao.getUsuario().getPerfis());
        outros.remove(sessao.getPerfilAtivo());
        if (outros.isEmpty()) return;

        Perfil destino = outros.size() == 1 ? outros.iterator().next() : SeletorPerfil.escolher(outros);
        if (destino == null) return;

        String fxml = switch (destino) {
            case AUTOR -> "/Views/AutorDashboardView.fxml";
            case AVALIADOR -> "/Views/AvaliadorDashboardView.fxml";
            case GERENTE -> "/Views/GerenteDashboardView.fxml";
        };

        try (Connection conn = Conexao.getConnection()) {
            Usuario usuarioTipado = UsuarioLookup.buscarPorLoginEPerfil(conn, sessao.getUsuario().getLogin(), destino);
            Sessao novaSessao = new Sessao(usuarioTipado, destino);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof DashboardController dc) {
                dc.setSessao(novaSessao);
            }
            tblObras.getScene().setRoot(root);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao trocar de perfil: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao abrir o painel: " + e.getMessage());
        }
    }

    @Override
    public void carregarDados() {
        try (Connection conn = Conexao.getConnection()) {
            IObraService obraService = new ObraServiceProxy(conn);
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
            String feedback = cell.getValue().getFeedback();
            if (feedback == null || feedback.isBlank()) {
                feedback = (cell.getValue().getStatus() == 0) ? "—" : "Sem feedback";
            }
            return new SimpleStringProperty(feedback);
        });
        colFeedback.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    return;
                }
                setText(item);
                setTooltip(new Tooltip(item));
            }
        });
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnAvaliar = new Button();
            private final Button btnBaixar = new Button();
            {
                btnAvaliar.setGraphic(Icones.icone("avaliar.png", 16));
                btnAvaliar.getStyleClass().addAll("btn-acao", "btn-acao-verde");
                btnAvaliar.setTooltip(new Tooltip("Avaliar"));
                btnAvaliar.setOnAction(e -> {
                    Obra obra = getTableView().getItems().get(getIndex());
                    abrirDialogoAvaliacao(obra);
                });
                btnBaixar.setGraphic(Icones.icone("baixar.png", 16));
                btnBaixar.getStyleClass().addAll("btn-acao", "btn-acao-azul");
                btnBaixar.setTooltip(new Tooltip("Baixar arquivo"));
                btnBaixar.setOnAction(e -> {
                    Obra obra = getTableView().getItems().get(getIndex());
                    baixarArquivo(obra);
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
                HBox box = new HBox(8);
                // Baixar disponível sempre (para ler a obra, mesmo já avaliada).
                box.getChildren().add(btnBaixar);
                // Avaliar só enquanto a obra está em avaliação.
                if (obra.getStatus() == 0) {
                    box.getChildren().add(btnAvaliar);
                }
                setGraphic(box);
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

        Button btnAbrir = new Button("Abrir arquivo da obra");
        btnAbrir.setOnAction(ev -> abrirArquivo(obra));

        VBox content = new VBox(10,
                btnAbrir,
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
                IObraService obraService = new ObraServiceProxy(conn);
                String feedback = campoFeedback.getText().trim();
                obraService.avaliar(obra, novoStatus, feedback.isEmpty() ? null : feedback, sessao);
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

    /** Abre o arquivo da obra no aplicativo padrão do sistema. */
    private void abrirArquivo(Obra obra) {
        String caminho = obra.getArquivo();
        if (caminho == null || caminho.isBlank()) {
            mostrarAlerta("Aviso", "Esta obra não possui arquivo anexado.");
            return;
        }
        File arquivo = new File(caminho);
        if (!arquivo.exists()) {
            mostrarAlerta("Aviso", "O arquivo da obra não foi encontrado:\n" + caminho);
            //mostrarAlerta("Aviso",caminho);
            return;
        }
        if (!Desktop.isDesktopSupported()) {
            mostrarAlerta("Aviso", "Abertura de arquivos não é suportada neste sistema.");
            return;
        }
        try {
            Desktop.getDesktop().open(arquivo);
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível abrir o arquivo: " + e.getMessage());
        }
    }

    /** Baixa (salva uma cópia) o arquivo da obra em um local escolhido pelo avaliador. */
    private void baixarArquivo(Obra obra) {
        String caminho = obra.getArquivo();
        if (caminho == null || caminho.isBlank()) {
            mostrarAlerta("Aviso", "Esta obra não possui arquivo anexado.");
            return;
        }
        File origem = new File(caminho); // Recebe o pathname como parametro
        if (!origem.exists()) {
            mostrarAlerta("Aviso", "O arquivo da obra não foi encontrado:\n" + caminho);
            //mostrarAlerta("Aviso",  caminho);
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Baixar arquivo da obra");
        fc.setInitialFileName(nomeSugerido(origem.getName()));
        File destino = fc.showSaveDialog(tblObras.getScene().getWindow());
        if (destino == null) return;

        try {
            Files.copy(origem.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            mostrarAlerta("Sucesso", "Arquivo salvo em:\n" + destino.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Não foi possível baixar o arquivo: " + e.getMessage());
        }
    }

    /**
     * Remove o prefixo de unicidade ("<millis>_") usado no armazenamento,
     * sugerindo o nome original do arquivo no diálogo de download.
     */
    private String nomeSugerido(String nomeArmazenado) {
        int sep = nomeArmazenado.indexOf('_');
        if (sep > 0 && nomeArmazenado.substring(0, sep).matches("\\d+")) {
            return nomeArmazenado.substring(sep + 1);
        }
        return nomeArmazenado;
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}