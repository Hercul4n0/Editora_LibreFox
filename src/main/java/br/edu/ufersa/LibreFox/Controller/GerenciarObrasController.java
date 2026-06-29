package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.DAO.AvaliadorDAO;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.Model.exceptions.AcessoNegadoException;
import br.edu.ufersa.LibreFox.Model.exceptions.OperacaoInvalidaException;
import br.edu.ufersa.LibreFox.Model.service.IObraService;
import br.edu.ufersa.LibreFox.Model.service.ObraServiceProxy;
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
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class GerenciarObrasController implements DashboardController {

    private static final String CSS_PATH = "/CSS/style.css";
    private static final String VIEWS_DIR = "/Views/";
    private static final String VIEW_LOGIN = VIEWS_DIR + "LoginView.fxml";

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
    @FXML private TableColumn<Obra, String> colFeedback;
    @FXML private TableColumn<Obra, String> colAcoes;

    private Sessao sessao;
    private final ObservableList<Obra> obrasList = FXCollections.observableArrayList();

    @Override
    public void setSessao(Sessao sessao) {
        this.sessao = sessao;
        carregarDados();
    }

    @Override
    public void carregarDados() {
        try (Connection conn = Conexao.getConnection()) {
            IObraService obraService = new ObraServiceProxy(conn);
            List<Obra> obras = obraService.listar(sessao);

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
        } catch (AcessoNegadoException e) {
            mostrarAlerta("Acesso negado", e.getMessage());
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
        colAvaliador.setCellValueFactory(cell -> {
            if (cell.getValue().getAvaliador() != null) {
                return new SimpleStringProperty(cell.getValue().getAvaliador().getNome());
            }
            return new SimpleStringProperty("Sem avaliador");
        });

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
            private final Button btnDesignar = new Button();
            private final Button btnEditar = new Button();
            private final Button btnExcluir = new Button();
            private final Button btnBaixar = new Button();

            {
                btnDesignar.setGraphic(Icones.icone("designar.png", 16));
                btnEditar.setGraphic(Icones.icone("editar.png", 16));
                btnExcluir.setGraphic(Icones.icone("deletar-lixeira.png", 16));
                btnBaixar.setGraphic(Icones.icone("baixar.png", 16));
                btnDesignar.getStyleClass().addAll("btn-acao", "btn-acao-azul");
                btnEditar.getStyleClass().addAll("btn-acao", "btn-acao-verde");
                btnExcluir.getStyleClass().addAll("btn-acao", "btn-acao-vermelho");
                btnBaixar.getStyleClass().addAll("btn-acao", "btn-acao-azul");
                btnDesignar.setTooltip(new Tooltip("Designar avaliador"));
                btnEditar.setTooltip(new Tooltip("Editar"));
                btnExcluir.setTooltip(new Tooltip("Excluir"));
                btnBaixar.setTooltip(new Tooltip("Baixar arquivo"));

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

                // Baixar disponível sempre, independente do status, para o
                // gerente poder ler a obra a qualquer momento.
                box.getChildren().add(btnBaixar);

                // Designar: só aparece se a obra estiver em análise e sem avaliador
                if (obra.getStatus() == 0 && obra.getAvaliador() == null) {
                    box.getChildren().add(btnDesignar);
                }

                // Editar: aparece para obras em análise (apenas dados descritivos —
                // o status é alterado exclusivamente pelo fluxo de avaliação)
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

            // O autor de uma obra nunca pode ser o avaliador dela mesma —
            // nem oferecemos essa opção na lista, para deixar a regra clara
            // já na interface (a checagem real continua em ObraService).
            avaliadores = avaliadores.stream()
                    .filter(a -> obra.getAutor() == null || a.getId() != obra.getAutor().getId())
                    .collect(java.util.stream.Collectors.toList());

            if (avaliadores.isEmpty()) {
                mostrarAlerta("Aviso",
                        "Não há avaliadores disponíveis para designar a esta obra.\n" +
                                "(o autor da obra não pode avaliar a própria obra)");
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

            dialog.setResultConverter(button -> {
                if (button == ButtonType.OK) {
                    return cmbAvaliador.getSelectionModel().getSelectedItem();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(avaliador -> {
                if (avaliador != null) {
                    try (Connection conn2 = Conexao.getConnection()) {
                        // Regra f): somente o gerente pode designar avaliadores —
                        // verificação garantida dentro do ObraService.
                        new ObraServiceProxy(conn2).designarAvaliador(obra, avaliador, sessao);
                        tblObras.refresh();
                        mostrarAlertaInfo("Sucesso",
                                "Avaliador " + avaliador.getNome() + " designado com sucesso!");
                    } catch (SQLException e) {
                        e.printStackTrace();
                        mostrarAlerta("Erro", "Erro ao designar avaliador: " + e.getMessage());
                    } catch (AcessoNegadoException e) {
                        mostrarAlerta("Acesso negado", e.getMessage());
                    } catch (OperacaoInvalidaException e) {
                        mostrarAlerta("Aviso", e.getMessage());
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

        grid.add(new Label("Título:"), 0, 0);
        grid.add(campoTitulo, 1, 0);
        grid.add(new Label("Gênero:"), 0, 1);
        grid.add(campoGenero, 1, 1);
        grid.add(new Label("Ano:"), 0, 2);
        grid.add(cmbAnoEdit, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(new Label("Em análise (alterado apenas pela avaliação do avaliador designado)"), 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = Conexao.getConnection()) {
                    obra.setTitulo(campoTitulo.getText().trim());
                    obra.setGenero(campoGenero.getText().trim());
                    obra.setAno(Short.parseShort(cmbAnoEdit.getValue()));
                    // Status NÃO é alterado aqui — somente via ObraService.avaliar(),
                    // chamado pelo avaliador designado.

                    new ObraServiceProxy(conn).alterar(obra, sessao);
                    carregarDados();
                    mostrarAlertaInfo("Sucesso", "Obra atualizada com sucesso!");

                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao atualizar: " + e.getMessage());
                } catch (AcessoNegadoException e) {
                    mostrarAlerta("Acesso negado", e.getMessage());
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
                    new ObraServiceProxy(conn).excluir(obra, sessao);
                    obrasList.remove(obra);
                    mostrarAlertaInfo("Sucesso", "Obra excluída com sucesso!");
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarAlerta("Erro", "Erro ao excluir: " + e.getMessage());
                } catch (AcessoNegadoException e) {
                    mostrarAlerta("Acesso negado", e.getMessage());
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
            IObraService obraService = new ObraServiceProxy(conn);
            List<Obra> resultados;

            if (!termo.isEmpty()) {
                resultados = obraService.buscarPorTitulo(termo, sessao);
            } else {
                resultados = obraService.listar(sessao);
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
        } catch (AcessoNegadoException e) {
            mostrarAlerta("Acesso negado", e.getMessage());
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "Ano inválido!");
        }
    }

    // Métodos de navegação
    @FXML private void navegarHome() { navegarPara("GerenteDashboardView.fxml"); }
    @FXML private void navegarAutores() { navegarPara("GerenciarAutoresView.fxml"); }
    @FXML private void navegarAvaliadores() { navegarPara("GerenciarAvaliadoresView.fxml"); }
    @FXML private void navegarRelatorios() { navegarPara("RelatoriosView.fxml"); }

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
            // Troca apenas o root da Scene atual, preservando o modo maximizado.
            tblObras.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Baixa (copia para onde o gerente escolher) o arquivo anexado à obra —
     * mesmo fluxo usado na tela do Avaliador.
     */
    private void baixarArquivo(Obra obra) {
        String caminho = obra.getArquivo();
        if (caminho == null || caminho.isBlank()) {
            mostrarAlerta("Aviso", "Esta obra não possui arquivo anexado.");
            return;
        }
        File origem = new File(caminho);
        if (!origem.exists()) {
            mostrarAlerta("Aviso", "O arquivo da obra não foi encontrado:\n" + caminho);
            return;
        }

        FileChooser fc = new FileChooser();
        fc.setTitle("Baixar arquivo da obra");
        fc.setInitialFileName(nomeSugerido(origem.getName()));
        File destino = fc.showSaveDialog(tblObras.getScene().getWindow());
        if (destino == null) return;

        try {
            Files.copy(origem.toPath(), destino.toPath(), StandardCopyOption.REPLACE_EXISTING);
            mostrarAlertaInfo("Sucesso", "Arquivo salvo em:\n" + destino.getAbsolutePath());
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