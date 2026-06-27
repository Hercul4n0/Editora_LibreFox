package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.DAO.AutorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.AvaliadorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.GerenteDAO;
import br.edu.ufersa.LibreFox.Model.entities.*;
import br.edu.ufersa.LibreFox.util.Conexao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {

    // -------------------------------------------------------------------------
    // Caminhos de recurso — relativos à raiz do classpath (src/main/resources).
    // Os FXML moram em "Views/" e o CSS em "CSS/", NÃO em "br/edu/ufersa/...".
    // -------------------------------------------------------------------------
    private static final String CSS_PATH = "/CSS/style.css";
    private static final String VIEW_LOGIN = "/Views/LoginView.fxml";
    private static final String VIEW_CADASTRO = "/Views/CadastroView.fxml";
    private static final String VIEW_AUTOR_DASHBOARD = "/Views/AutorDashboardView.fxml";
    private static final String VIEW_AVALIADOR_DASHBOARD = "/Views/AvaliadorDashboardView.fxml";
    private static final String VIEW_GERENTE_DASHBOARD = "/Views/GerenteDashboardView.fxml";

    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private Button btnPerfilAutor;
    @FXML private Button btnPerfilAvaliador;
    @FXML private Button btnPerfilGerente;
    @FXML private Label lblMensagemErro;

    private Perfil perfilSelecionado = Perfil.AUTOR;

    @FXML
    public void initialize() {
        atualizarEstiloPerfil();
    }

    @FXML
    private void selecionarPerfil(javafx.event.ActionEvent event) {
        Button btn = (Button) event.getSource();
        if (btn == btnPerfilAutor) {
            perfilSelecionado = Perfil.AUTOR;
        } else if (btn == btnPerfilAvaliador) {
            perfilSelecionado = Perfil.AVALIADOR;
        } else if (btn == btnPerfilGerente) {
            perfilSelecionado = Perfil.GERENTE;
        }
        atualizarEstiloPerfil();
    }

    private void atualizarEstiloPerfil() {
        btnPerfilAutor.getStyleClass().remove("btn-primario");
        btnPerfilAvaliador.getStyleClass().remove("btn-primario");
        btnPerfilGerente.getStyleClass().remove("btn-primario");
        btnPerfilAutor.getStyleClass().add("btn-azul-claro");
        btnPerfilAvaliador.getStyleClass().add("btn-azul-claro");
        btnPerfilGerente.getStyleClass().add("btn-azul-claro");

        switch (perfilSelecionado) {
            case AUTOR:
                btnPerfilAutor.getStyleClass().remove("btn-azul-claro");
                btnPerfilAutor.getStyleClass().add("btn-primario");
                break;
            case AVALIADOR:
                btnPerfilAvaliador.getStyleClass().remove("btn-azul-claro");
                btnPerfilAvaliador.getStyleClass().add("btn-primario");
                break;
            case GERENTE:
                btnPerfilGerente.getStyleClass().remove("btn-azul-claro");
                btnPerfilGerente.getStyleClass().add("btn-primario");
                break;
        }
    }

    @FXML
    private void handleLogin() {
        String login = campoLogin.getText().trim();
        String senha = campoSenha.getText().trim();

        if (login.isEmpty() || senha.isEmpty()) {
            lblMensagemErro.setText("Preencha todos os campos!");
            return;
        }

        try (Connection conn = Conexao.getConnection()) {
            Usuario usuario = null;

            switch (perfilSelecionado) {
                case AUTOR:
                    usuario = new AutorDAO(conn).buscarPorLogin(login);
                    break;
                case AVALIADOR:
                    usuario = new AvaliadorDAO(conn).buscarPorLogin(login);
                    break;
                case GERENTE:
                    usuario = new GerenteDAO(conn).buscarPorLogin(login);
                    break;
            }

            if (usuario == null) {
                lblMensagemErro.setText("Usuário não encontrado!");
                return;
            }

            if (!usuario.getSenha().equals(senha)) {
                lblMensagemErro.setText("Senha incorreta!");
                return;
            }

            // Criar sessão
            Sessao sessao = new Sessao(usuario, perfilSelecionado);
            abrirDashboard(sessao);

        } catch (SQLException e) {
            lblMensagemErro.setText("Erro ao conectar: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            lblMensagemErro.setText(e.getMessage());
        }
    }

    private void abrirDashboard(Sessao sessao) {
        try {
            String fxml;
            switch (sessao.getPerfilAtivo()) {
                case AUTOR:
                    fxml = VIEW_AUTOR_DASHBOARD;
                    break;
                case AVALIADOR:
                    fxml = VIEW_AVALIADOR_DASHBOARD;
                    break;
                case GERENTE:
                    fxml = VIEW_GERENTE_DASHBOARD;
                    break;
                default:
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = loader.load();

            // Passar sessão para o controller
            Object controller = loader.getController();
            if (controller instanceof DashboardController) {
                ((DashboardController) controller).setSessao(sessao);
            }

            Stage stage = (Stage) campoLogin.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());
            stage.setScene(scene);
            stage.setMaximized(true);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            lblMensagemErro.setText("Erro ao carregar dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void handleIrCadastro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_CADASTRO));
            Parent root = loader.load();

            Stage stage = (Stage) campoLogin.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}