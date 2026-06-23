package br.edu.ufersa.LibreFox.Controllers;

import br.edu.ufersa.LibreFox.Model.DAO.AvaliadorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.ObraDAO;
import br.edu.ufersa.LibreFox.Model.DAO.RelatorioDAO;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Relatorio;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class RelatoriosController implements DashboardController {

    @FXML private DatePicker dtpDataInicial;
    @FXML private DatePicker dtpDataFinal;
    @FXML private ComboBox<Avaliador> cmbAvaliador;
    @FXML private Label lblTotalObras;
    @FXML private Label lblAprovadas;
    @FXML private Label lblRejeitadas;
    @FXML private TableView<Obra> tblRelatorio;
    @FXML private TableColumn<Obra, String> colTitulo;
    @FXML private TableColumn<Obra, String> colAutor;
    @FXML private TableColumn<Obra, String> colGenero;
    @FXML private TableColumn<Obra, String> colStatus;
    @FXML private TableColumn<Obra, String> colDataAvaliacao;
    @FXML private TableColumn<Obra, String> colAvaliador;

    private Sessao sessao;
    private ObservableList<Obra> obrasList = FXCollections.observableArrayList();
    private Relatorio relatorioAtual;

    @Override
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
        carregarDados();
    }

    @Override
    public void carregarDados() {
        // Configurar datas padrão (últimos 30 dias)
        dtpDataInicial.setValue(LocalDate.now().minusDays(30));
        dtpDataFinal.setValue(LocalDate.now());

        // Carregar avaliadores para o combo
        try (Connection conn = Conexao.getConnection()) {
            AvaliadorDAO avaliadorDAO = new AvaliadorDAO(conn);
            List<Avaliador> avaliadores = avaliadorDAO.listar();

            // Adicionar opção "Todos"
            cmbAvaliador.getItems().clear();
            cmbAvaliador.getItems().add(null); // Opção "Todos"
            cmbAvaliador.getItems().addAll(avaliadores);

            // Configurar display do combo
            cmbAvaliador.setCellFactory(param -> new ListCell<Avaliador>() {
                @Override
                protected void updateItem(Avaliador item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Todos os avaliadores");
                    } else {
                        setText(item.getNome());
                    }
                }
            });
            cmbAvaliador.setButtonCell(new ListCell<Avaliador>() {
                @Override
                protected void updateItem(Avaliador item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Todos os avaliadores");
                    } else {
                        setText(item.getNome());
                    }
                }
            });

            cmbAvaliador.getSelectionModel().selectFirst(); // Selecionar "Todos"

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao carregar avaliadores: " + e.getMessage());
        }

        configurarTabela();
        gerarRelatorio();
    }

    private void configurarTabela() {
        colTitulo.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitulo()));
        colAutor.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAutor().getNome()));
        colGenero.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getGenero()));
        colStatus.setCellValueFactory(cell -> {
            short status = cell.getValue().getStatus();
            String texto = switch (status) {
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
                if (item.equals("Aprovado")) {
                    label.getStyleClass().add("badge-aprovado");
                } else if (item.equals("Rejeitado")) {
                    label.getStyleClass().add("badge-rejeitado");
                }
                setGraphic(label);
                setText(null);
            }
        });
        colDataAvaliacao.setCellValueFactory(cell -> {
            if (cell.getValue().getDataAvaliacao() != null) {
                return new SimpleStringProperty(
                        cell.getValue().getDataAvaliacao().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            }
            return new SimpleStringProperty("-");
        });
        colAvaliador.setCellValueFactory(cell -> {
            if (cell.getValue().getAvaliador() != null) {
                return new SimpleStringProperty(cell.getValue().getAvaliador().getNome());
            }
            return new SimpleStringProperty("-");
        });
    }

    @FXML
    private void handleGerarRelatorio() {
        gerarRelatorio();
    }

    private void gerarRelatorio() {
        LocalDate dataInicial = dtpDataInicial.getValue();
        LocalDate dataFinal = dtpDataFinal.getValue();

        if (dataInicial == null || dataFinal == null) {
            mostrarAlerta("Aviso", "Selecione as datas inicial e final.");
            return;
        }

        if (dataFinal.isBefore(dataInicial)) {
            mostrarAlerta("Aviso", "A data final não pode ser anterior à data inicial.");
            return;
        }

        Avaliador avaliadorSelecionado = cmbAvaliador.getSelectionModel().getSelectedItem();

        try (Connection conn = Conexao.getConnection()) {
            RelatorioDAO relatorioDAO = new RelatorioDAO(conn);

            if (avaliadorSelecionado != null) {
                relatorioAtual = relatorioDAO.gerarPorPeriodoEAvaliador(
                        dataInicial, dataFinal, avaliadorSelecionado);
            } else {
                relatorioAtual = relatorioDAO.gerarPorPeriodo(dataInicial, dataFinal);
            }

            // Atualizar tabela
            obrasList.setAll(relatorioAtual.getObras());
            tblRelatorio.setItems(obrasList);

            // Atualizar resumo
            long aprovadas = relatorioAtual.getObras().stream()
                    .filter(o -> o.getStatus() == 1).count();
            long rejeitadas = relatorioAtual.getObras().stream()
                    .filter(o -> o.getStatus() == 2).count();

            lblTotalObras.setText(String.valueOf(relatorioAtual.getNumDeObras()));
            lblAprovadas.setText(String.valueOf(aprovadas));
            lblRejeitadas.setText(String.valueOf(rejeitadas));

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao gerar relatório: " + e.getMessage());
        }
    }

    @FXML
    private void handleExportar() {
        if (relatorioAtual == null || relatorioAtual.getObras().isEmpty()) {
            mostrarAlerta("Aviso", "Não há dados para exportar. Gere um relatório primeiro.");
            return;
        }

        // TODO: Implementar exportação (CSV, PDF, etc.)
        mostrarAlertaInfo("Info", "Funcionalidade de exportação em desenvolvimento.");
    }

    // Métodos de navegação
    @FXML private void navegarHome() { navegarPara("GerenteDashboardView.fxml"); }
    @FXML private void navegarAutores() { navegarPara("GerenciarAutoresView.fxml"); }
    @FXML private void navegarAvaliadores() { navegarPara("GerenciarAvaliadoresView.fxml"); }
    @FXML private void navegarObras() { navegarPara("GerenciarObrasView.fxml"); }

    @FXML
    private void handleSair() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/LibreFox/view/LoginView.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tblRelatorio.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/br/edu/ufersa/LibreFox/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(false);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navegarPara(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/LibreFox/view/" + fxml));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).setSessao(sessao);
            }
            Stage stage = (Stage) tblRelatorio.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/br/edu/ufersa/LibreFox/view/style.css").toExternalForm());
            stage.setScene(scene);
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