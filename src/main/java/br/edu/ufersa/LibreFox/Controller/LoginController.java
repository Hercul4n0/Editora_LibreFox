package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.entities.*;
import br.edu.ufersa.LibreFox.util.Conexao;
import br.edu.ufersa.LibreFox.util.SeletorPerfil;
import br.edu.ufersa.LibreFox.util.UsuarioLookup;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

public class LoginController {

    private static final String CSS_PATH = "/CSS/style.css";
    private static final String VIEW_LOGIN = "/Views/LoginView.fxml";
    private static final String VIEW_CADASTRO = "/Views/CadastroView.fxml";
    private static final String VIEW_AUTOR_DASHBOARD = "/Views/AutorDashboardView.fxml";
    private static final String VIEW_AVALIADOR_DASHBOARD = "/Views/AvaliadorDashboardView.fxml";
    private static final String VIEW_GERENTE_DASHBOARD = "/Views/GerenteDashboardView.fxml";

    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private Label lblMensagemErro;

    @FXML
    private void handleLogin() {
        String login = campoLogin.getText().trim();
        String senha = campoSenha.getText().trim();

        if (login.isEmpty() || senha.isEmpty()) {
            lblMensagemErro.setText("Preencha todos os campos!");
            return;
        }

        try (Connection conn = Conexao.getConnection()) {
            // A conta é localizada tentando cada perfil
            Usuario usuario = null;
            for (Perfil perfil : Perfil.values()) {
                usuario = UsuarioLookup.buscarPorLoginEPerfil(conn, login, perfil);
                if (usuario != null) break;
            }

            if (usuario == null) {
                lblMensagemErro.setText("Usuário não encontrado!");
                return;
            }
            if (!usuario.getSenha().equals(senha)) {
                lblMensagemErro.setText("Senha incorreta!");
                return;
            }

            Set<Perfil> perfisDisponiveis = usuario.getPerfis();
            if (perfisDisponiveis.size() <= 1) {
                Sessao sessao = new Sessao(usuario, perfisDisponiveis.iterator().next());
                abrirDashboard(sessao);
                return;
            }

            // Conta com mais de um perfil: deixa o usuário escolher como entrar.
            Perfil perfilEscolhido = SeletorPerfil.escolher(perfisDisponiveis);
            if (perfilEscolhido == null) return;

            Usuario usuarioTipado = UsuarioLookup.buscarPorLoginEPerfil(conn, login, perfilEscolhido);
            Sessao sessao = new Sessao(usuarioTipado, perfilEscolhido);
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