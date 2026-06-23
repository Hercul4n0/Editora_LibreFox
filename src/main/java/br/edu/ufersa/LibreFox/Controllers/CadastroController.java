package br.edu.ufersa.LibreFox.Controllers;

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
import java.util.EnumSet;

public class CadastroController {

    @FXML private TextField campoNome;
    @FXML private TextField campoEmail;
    @FXML private PasswordField campoSenha;
    @FXML private Button btnPerfilAutor;
    @FXML private Button btnPerfilAvaliador;
    @FXML private Button btnPerfilGerente;
    @FXML private Label lblMensagem;

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
    private void handleCadastro() {
        String nome = campoNome.getText().trim();
        String email = campoEmail.getText().trim();
        String senha = campoSenha.getText().trim();

        if (nome.isEmpty() || email.isEmpty() || senha.isEmpty()) {
            lblMensagem.setText("Preencha todos os campos!");
            return;
        }

        // Criar endereço padrão para o cadastro
        Endereco endereco = new Endereco("S/N", "Centro", "Rua Principal", "Cidade", "UF");

        try (Connection conn = Conexao.getConnection()) {
            switch (perfilSelecionado) {
                case AUTOR:
                    Autor autor = new Autor(nome, "CPF", endereco, email, senha);
                    new AutorDAO(conn).inserir(autor);
                    break;
                case AVALIADOR:
                    Avaliador avaliador = new Avaliador(nome, "CPF", endereco, email, senha);
                    new AvaliadorDAO(conn).inserir(avaliador);
                    break;
                case GERENTE:
                    Gerente gerente = new Gerente(nome, "CPF", endereco, email, senha);
                    new GerenteDAO(conn).inserir(gerente);
                    break;
            }

            lblMensagem.setStyle("-fx-text-fill: #28a745;");
            lblMensagem.setText("Conta criada com sucesso! Faça login.");

            // Limpar campos
            campoNome.clear();
            campoEmail.clear();
            campoSenha.clear();

        } catch (SQLException e) {
            lblMensagem.setStyle("-fx-text-fill: #dc3545;");
            lblMensagem.setText("Erro ao cadastrar: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleIrLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/br/edu/ufersa/LibreFox/view/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) campoNome.getScene().getWindow();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/br/edu/ufersa/LibreFox/view/style.css").toExternalForm());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}