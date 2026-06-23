package br.edu.ufersa.LibreFox.Controllers;

import br.edu.ufersa.LibreFox.Model.DAO.AutorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.AvaliadorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.ObraDAO;
import br.edu.ufersa.LibreFox.Model.entities.Autor;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class GerenciarObrasController implements DashboardController {

    @FXML private TextField campoBusca;
    @FXML private ComboBox<String> cmbStatus;
    @FXML private ComboBox<String> cmbAno;
    @FXML private TableView<Obra> tblObras;
    @FXML private TableColumn<Obra, String> colTitulo;
    @FXML private TableColumn<Obra, String> colAutor;
    @FXML private TableColumn<Obra, String> colGenero;
    @FXML private TableColumn<Obra, String> colStatus;
    @FXML private TableColumn<Obra, String> colAno;
    @FXML private TableColumn<Obra, String> colAvaliador;
    @FXML private TableColumn<Obra, String> colAcoes;

    private Sessao sessao;
    private ObservableList<Obra> obrasList = FXCollections.observableArrayList();

    @Override
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
        carregarDados();
    }

    @Override
    public void carregarDados() {
        try (Connection conn = Conexao.getConnection()) {
            ObraDAO obraDAO = new ObraDAO(conn);
            List<Obra> obras = obraDAO.listar();

            obrasList.setAll(obras);
            configurarTabela();
            tblObras.setItems(obrasList);

            // Carregar anos para o combo
            cmbAno.getItems().clear();
            cmbAno.getItems().add("Todos");
            for (int ano = LocalDate.now().getYear(); ano >= 2020; ano--) {
                cmbAno.getItems().add(String.valueOf(ano));
            }
            cmbAno.getSelectionModel().selectFirst();

            // Carregar status para o combo
            cmbStatus.getItems().clear();
            cmbStatus.getItems().addAll("Todos", "Em análise", "Aprovado", "Rejeitado");
            cmbStatus.getSelectionModel().selectFirst();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao carregar dados: " + e.getMessage());
        }
    }

    private void configurarTabela() {
        colTitulo.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTitulo()));
        colAutor.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getAutor().getNome()));
        colGenero.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getGenero()));
        colAno.setCellValueFactory(cell ->
                new SimpleStringProperty(String.valueOf(cell.getValue().getAno())));
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
        colAvaliador.setCellValueFactory(cell -> {
            if (cell.getValue().getAvaliador() != null) {
                return new SimpleStringProperty(cell.getValue().getAvaliador().getNome());
            }
            return new SimpleStringProperty("Sem avaliador");
        });

        colAcoes.setCellFactory(col -> new TableCell<>() {
            private final Button btnDesignar = new Button("👤 Designar");
            private final Button btnEditar = new Button("✏️ Editar");
            private final Button btnExcluir = new Button("🗑️ Excluir");

            {
                btnDesignar.getStyleClass().addAll("btn-acao", "btn-acao-azul");
                btnEditar.getStyleClass().addAll("btn-acao", "btn-acao-verde");
                btnExcluir.getStyleClass().addAll("btn-acao", "btn-acao-vermelho");

                btnDesignar.setOnAction(e -> {
                    Obra obra = getTableView().getItems().get(getIndex());
                    designarAvaliador(obra);
                });
                btnEditar.setOnAction(e -> {
                    Obra obra = getTableView().getItems().get(getIndex());
                    editarObra(obra);
                });
                btnExcluir.setOnAction(e -> {
                    Obra obra = getTableView().getItems().get(getIndex());
                    confirmarExclusao(obra);
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

                // Designar: só aparece se a obra estiver em análise e sem avaliador
                if (obra.getStatus() == 0 && obra.getAvaliador() == null) {
                    box.getChildren().add(btnDesignar);
                }

                // Editar: aparece para obras em análise
                if (obra.getStatus() == 0) {
                    box.getChildren().add(btnEditar);
                }

                // Excluir: sempre aparece (com confirmação)
                box.getChildren().add(btnExcluir);

                setGraphic(box.getChildren().isEmpty() ? null : box);
            }
        });
    }

    private void designarAvaliador(Obra obra) {
        try (Connection conn = Conexao.getConnection()) {
            AvaliadorDAO avaliadorDAO = new AvaliadorDAO(conn);
            List<Avaliador> avaliadores = avaliadorDAO.listar();

            if (avaliadores.isEmpty()) {
                mostrarAlerta("Aviso", "Não há avaliadores cadastrados para designar.");
                return;
            }

            // Criar diálogo para selecionar avaliador
            Dialog<Avaliador> dialog = new Dialog<>();
            dialog.setTitle("Designar Avaliador");
            dialog.setHeaderText("Selecione um avaliador para: " + obra.getTitulo());

            ComboBox<Avaliador> cmbAvaliador = new ComboBox<>();
            cmbAvaliador.getItems().addAll(avaliadores);
            cmbAvaliador.setCellFactory(param -> new ListCell<Avaliador>() {
                @Override
                protected void updateItem(Avaliador item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item.getNome() + " (" + item.getObrasParaAvaliar().size() + " obras)");
                    }
                }
            });
            cmbAvaliador.setButtonCell(new ListCell<Avaliador>() {
                @Override
                protected void updateItem(Avaliador item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText("Selecione um avaliador");
                    } else {
                        setText(item.getNome());
                    }
                }
            });

            dialog.getDialogPane().setContent(cmbAvaliador);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            // Converter o resultado
            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    return cmbAvaliador.getSelectionModel().getSelectedItem();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(avaliador -> {
                if (avaliador != null) {
                    try (Connection conn2 = Conexao.getConnection()) {
                        ObraDAO obraDAO = new ObraDAO(conn2);
                        obraDAO.definirAvaliador(obra, avaliador);
                        obra.setAvaliador(avaliador);
                        tblObras.refresh();
                        mostrarAlertaInfo("Sucesso",
                                "Avaliador " + avaliador.getNome() + " designado com sucesso!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        mostrarAlerta("Erro", "Erro ao designar avaliador: " + e.getMessage());
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro ao carregar avaliadores: " + e.getMessage());
        }
    }

    private void editarObra(Obra obra) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Obra");
        dialog.setHeaderText("Alterar dados da obra: " + obra.getTitulo());

        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField campoTitulo = new TextField(obra.getTitulo());
        TextField campoGenero = new TextField(obra.getGenero());
        ComboBox<String> cmbAnoEdit = new ComboBox<>();
        for (int ano = LocalDate.now().getYear(); ano >= 2020; ano--) {
            cmbAnoEdit.getItems().add(String.valueOf(ano));
        }
        cmbAnoEdit.setValue(String.valueOf(obra.getAno()));

        // Status
        ComboBox<String> cmbStatusEdit = new ComboBox<>();
        cmbStatusEdit.getItems().addAll("Em análise", "Aprovado", "Rejeitado");
        cmbStatusEdit.setValue(switch (obra.getStatus()) {
            case 0 -> "Em análise";
            case 1 -> "Aprovado";
            case 2 -> "Rejeitado";
            default -> "Em análise";
        });

        grid.add(new Label("Título:"), 0, 0);
        grid.add(campoTitulo, 1, 0);
        grid.add(new Label("Gênero:"), 0, 1);
        grid.add(campoGenero, 1, 1);
        grid.add(new Label("Ano:"), 0, 2);
        grid.add(cmbAnoEdit, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(cmbStatusEdit, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = Conexao.getConnection()) {
                    obra.setTitulo(campoTitulo.getText().trim());
                    obra.setGenero(campoGenero.getText().trim());
                    obra.setAno(Short.parseShort(cmbAnoEdit.getValue()));

                    short novoStatus = switch (cmbStatusEdit.getValue()) {
                        case "Aprovado" -> 1;
                        case "Rejeitado" -> 2;
                        default -> 0;
                    };
                    obra.setStatus(novoStatus);

                    new ObraDAO(conn).atualizar(obra);
                    carregarDados();
                    mostrarAlertaInfo("Sucesso", "Obra atualizada com sucesso!");

                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao atualizar: " + e.getMessage());
                } catch (NumberFormatException e) {
                    mostrarAlerta("Erro", "Ano inválido!");
                }
            }
        });
    }

    private void confirmarExclusao(Obra obra) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar exclusão");
        alert.setHeaderText("Excluir obra");
        alert.setContentText("Tem certeza que deseja excluir \"" + obra.getTitulo() + "\"?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = Conexao.getConnection()) {
                    new ObraDAO(conn).deletar(obra);
                    obrasList.remove(obra);
                    mostrarAlertaInfo("Sucesso", "Obra excluída com sucesso!");
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
        String statusFiltro = cmbStatus.getSelectionModel().getSelectedItem();
        String anoFiltro = cmbAno.getSelectionModel().getSelectedItem();

        try (Connection conn = Conexao.getConnection()) {
            ObraDAO obraDAO = new ObraDAO(conn);
            List<Obra> resultados;

            if (!termo.isEmpty()) {
                resultados = obraDAO.buscarPorTitulo(termo);
            } else {
                resultados = obraDAO.listar();
            }

            // Aplicar filtros
            if (statusFiltro != null && !statusFiltro.equals("Todos")) {
                short status = switch (statusFiltro) {
                    case "Aprovado" -> 1;
                    case "Rejeitado" -> 2;
                    default -> 0;
                };
                resultados = resultados.stream()
                        .filter(o -> o.getStatus() == status)
                        .toList();
            }

            if (anoFiltro != null && !anoFiltro.equals("Todos")) {
                short ano = Short.parseShort(anoFiltro);
                resultados = resultados.stream()
                        .filter(o -> o.getAno().equals(ano))
                        .toList();
            }

            obrasList.setAll(resultados);
            tblObras.setItems(obrasList);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Erro", "Erro na busca: " + e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Ano inválido!");
        }
    }

    @FXML
    private void handleNovaObra() {
        mostrarAlertaInfo("Info", "Funcionalidade de criação de obra em desenvolvimento.\n" +
                "Use a tela 'Minhas obras' do Autor para criar novas obras.");
    }

    // Métodos de navegação
    @FXML private void navegarHome() { navegarPara("GerenteDashboardView.fxml"); }
    @FXML private void navegarAutores() { navegarPara("GerenciarAutoresView.fxml"); }
    @FXML private void navegarAvaliadores() { navegarPara("GerenciarAvaliadoresView.fxml"); }
    @FXML private void navegarRelatorios() { navegarPara("RelatoriosView.fxml"); }

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

    private void navegarPara(String fxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/LibreFox/view/" + fxml));
            Parent root = loader.load();
            Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).setSessao(sessao);
            }
            Stage stage = (Stage) tblObras.getScene().getWindow();
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