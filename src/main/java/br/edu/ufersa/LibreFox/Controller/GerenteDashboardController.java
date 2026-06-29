package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Perfil;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.Model.entities.Usuario;
import br.edu.ufersa.LibreFox.Model.exceptions.AcessoNegadoException;
import br.edu.ufersa.LibreFox.Model.service.IObraService;
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
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class GerenteDashboardController implements DashboardController {

    private static final String CSS_PATH = "/CSS/style.css";
    private static final String VIEWS_DIR = "/Views/";
    private static final String VIEW_LOGIN = VIEWS_DIR + "LoginView.fxml";

    @FXML private Label lblSaudacao;
    @FXML private Label lblEmAvaliacao;
    @FXML private Label lblAceitas;
    @FXML private Label lblRejeitadas;
    @FXML private Label lblSemAvaliador;
    @FXML private Label lblPendencia1;
    @FXML private Label lblPendencia2;
    @FXML private Label lblPendencia3;
    @FXML private TableView<Obra> tblObras;
    @FXML private TableColumn<Obra, String> colTitulo;
    @FXML private TableColumn<Obra, String> colAutor;
    @FXML private TableColumn<Obra, String> colStatus;
    @FXML private TableColumn<Obra, String> colAcoes;
    @FXML private Button btnTrocarPerfil;

    private Sessao sessao;
    private final ObservableList<Obra> obrasList = FXCollections.observableArrayList();

    @Override
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
        if (lblSaudacao != null) {
            lblSaudacao.setText("Olá, " + sessao.getNomeUsuario() + "!");
        }
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
            List<Obra> obras = obraService.listar(sessao);

            long emAvaliacao = obras.stream().filter(o -> o.getStatus() == 0).count();
            long aceitas = obras.stream().filter(o -> o.getStatus() == 1).count();
            long rejeitadas = obras.stream().filter(o -> o.getStatus() == 2).count();
            long semAvaliador = obras.stream().filter(o -> o.getAvaliador() == null && o.getStatus() == 0).count();

            lblEmAvaliacao.setText(String.valueOf(emAvaliacao));
            lblAceitas.setText(String.valueOf(aceitas));
            lblRejeitadas.setText(String.valueOf(rejeitadas));
            lblSemAvaliador.setText(String.valueOf(semAvaliador));

            // Pendências: obras em avaliação e já com avaliador designado
            List<Obra> pendentes = obras.stream()
                    .filter(o -> o.getStatus() == 0 && o.getAvaliador() != null)
                    .limit(3)
                    .collect(Collectors.toList());

            lblPendencia1.setText(pendentes.size() > 0 ?
                    pendentes.get(0).getTitulo() + " - " + pendentes.get(0).getAutor().getNome() :
                    "Nenhuma pendência");
            lblPendencia2.setText(pendentes.size() > 1 ?
                    pendentes.get(1).getTitulo() + " - " + pendentes.get(1).getAutor().getNome() :
                    "");
            lblPendencia3.setText(pendentes.size() > 2 ?
                    pendentes.get(2).getTitulo() + " - " + pendentes.get(2).getAutor().getNome() :
                    "");

            // Últimas obras
            obrasList.setAll(obras.stream().limit(5).collect(Collectors.toList()));
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
        colAutor.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAutor().getNome()));
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
        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnAvaliar = new Button();
            {
                btnAvaliar.setGraphic(Icones.icone("avaliar.png", 16));
                btnAvaliar.getStyleClass().add("btn-acao");
                btnAvaliar.setTooltip(new Tooltip("Apenas o avaliador designado pode avaliar"));
                btnAvaliar.setOnAction(e -> {
                    // A avaliação em si só pode ser feita pelo avaliador designado
                    // (ver AvaliadorDashboardController); aqui o gerente só consulta.
                    mostrarAlerta("Info", "Apenas o avaliador designado pode avaliar a obra.");
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

    @FXML
    private void navegarHome() {
        // Já está na home
    }

    @FXML
    private void navegarAutores() {
        navegarPara("GerenciarAutoresView.fxml");
    }

    @FXML
    private void navegarAvaliadores() {
        navegarPara("GerenciarAvaliadoresView.fxml");
    }

    @FXML
    private void navegarObras() {
        navegarPara("GerenciarObrasView.fxml");
    }

    @FXML
    private void navegarRelatorios() {
        navegarPara("RelatoriosView.fxml");
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

    private void navegarPara(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEWS_DIR + fxml));
            Parent root = loader.load();

            Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).setSessao(sessao);
            }

            // Troca apenas o conteúdo (root) da Scene atual, preservando o
            // estado da janela (tamanho e, principalmente, o modo maximizado).
            tblObras.getScene().setRoot(root);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao navegar: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}