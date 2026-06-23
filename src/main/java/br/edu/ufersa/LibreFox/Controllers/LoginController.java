package br.edu.ufersa.LibreFox.Controllers;

import br.edu.ufersa.LibreFox.Model.DAO.AutorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.AvaliadorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.GerenteDAO;
import br.edu.ufersa.LibreFox.Model.entities.*;
import br.edu.ufersa.LibreFox.util.Conexao; // TALVEZ ERRADO
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class LoginController {

    @FXML private TextField campoLogin;
    @FXML private PasswordField campoSenha;
    @FXML private Button btnPerfilAutor;
    @FXML private Button btnPerfilAvaliador;
    @FXML private Button btnPerfilGerente;
    @FXML private Label lblMensagemErro;

    private Perfil perfilSelecionado = Perfil.AUTOR;

    @FXML
    public void initialize() {
        // Estilo inicial para o perfil Autor
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
                    fxml = "/br/edu/ufersa/LibreFox/view/AutorDashboardView.fxml";
                    break;
                case AVALIADOR:
                    fxml = "/br/edu/ufersa/LibreFox/view/AvaliadorDashboardView.fxml";
                    break;
                case GERENTE:
                    fxml = "/br/edu/ufersa/LibreFox/view/GerenteDashboardView.fxml";
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
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/br/edu/ufersa/LibreFox/view/style.css").toExternalForm());
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/LibreFox/view/CadastroView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) campoLogin.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/br/edu/ufersa/LibreFox/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
