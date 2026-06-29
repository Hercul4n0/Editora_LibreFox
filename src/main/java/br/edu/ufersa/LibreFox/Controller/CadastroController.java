package br.edu.ufersa.LibreFox.Controller;

import br.edu.ufersa.LibreFox.Model.entities.Autor;
import br.edu.ufersa.LibreFox.Model.entities.Endereco;
import br.edu.ufersa.LibreFox.Model.service.AutorService;
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

public class CadastroController {

    private static final String CSS_PATH = "/CSS/style.css";
    private static final String VIEW_LOGIN = "/Views/LoginView.fxml";

    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private TextField campoCpf;
    @FXML private PasswordField campoSenha;
    @FXML private Label lblMensagem;

    @FXML
    private void handleCadastro() {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String cpf = campoCpf.getText().trim().replaceAll("\\D", ""); // remove pontuação, fica só números
        String senha = campoSenha.getText().trim();

        if (nome.isEmpty() || email.isEmpty() || cpf.isEmpty() || senha.isEmpty()) {
            lblMensagem.setStyle("-fx-text-fill: #dc3545;");
            lblMensagem.setText("Preencha todos os campos, incluindo o CPF!");
            return;
        }

        if (cpf.length() != 11) {
            lblMensagem.setStyle("-fx-text-fill: #dc3545;");
            lblMensagem.setText("CPF inválido — deve ter 11 números.");
            return;
        }

        // Endereço padrão — o autor pode editar os dados completos depois,
        // junto com o gerente, se necessário.
        Endereco endereco = new Endereco("S/N", "Centro", "Não informado", "Não informado", "XX");

        try (Connection conn = Conexao.getConnection()) {
            AutorService autorService = new AutorService(conn);
            Autor autor = new Autor(nome, cpf, endereco, email, senha);
            autorService.cadastrar(autor);

            lblMensagem.setStyle("-fx-text-fill: #1e7e34;");
            lblMensagem.setText("Conta criada com sucesso! Faça login.");

            campoNome.clear();
            campoEmail.clear();
            campoCpf.clear();
            campoSenha.clear();

        } catch (SQLException e) {
            lblMensagem.setStyle("-fx-text-fill: #dc3545;");
            if (e.getMessage() != null && e.getMessage().contains("usuario.cpf")) {
                lblMensagem.setText("Já existe uma conta cadastrada com este CPF.");
            } else if (e.getMessage() != null && e.getMessage().contains("usuario.login")) {
                lblMensagem.setText("Já existe uma conta cadastrada com este e-mail.");
            } else {
                lblMensagem.setText("Erro ao cadastrar: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    @FXML
    private void handleIrLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(VIEW_LOGIN));
            Parent root = loader.load();

            Stage stage = (Stage) campoNome.getScene().getWindow();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(getClass().getResource(CSS_PATH).toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}