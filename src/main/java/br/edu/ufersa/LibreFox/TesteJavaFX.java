package br.edu.ufersa.LibreFox;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Ponto de entrada da aplicação LibreFox.
 *
 * O fluxo real do sistema sempre começa pela tela de login — é o login
 * (LoginController) que cria a Sessao e a propaga para os dashboards e
 * telas de gestão através de DashboardController.setSessao(...).
 *
 * Carregar qualquer outra tela diretamente aqui (como acontecia antes,
 * pulando o login) faz a sessão nunca ser inicializada, quebrando a
 * navegação e qualquer funcionalidade que dependa do usuário logado.
 */
public class TesteJavaFX extends Application {

    private static final String CSS_PATH = "/CSS/style.css";
    private static final String VIEW_LOGIN = "/Views/LoginView.fxml";

    @Override
    public void start(Stage stage) {
        try {
            URL fxmlUrl = getClass().getResource(VIEW_LOGIN);
            if (fxmlUrl == null) {
                throw new IOException("Recurso não encontrado: " + VIEW_LOGIN);
            }

            // Usar FXMLLoader(URL) (em vez de loader.load(InputStream)) garante
            // que o loader conhece a "location" do FXML — é isso que permite
            // resolver caminhos relativos como stylesheets="@../CSS/style.css".
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = new Scene(root, 1200, 800);
            URL cssUrl = getClass().getResource(CSS_PATH);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }

            stage.setTitle("LibreFox — Gestão de editoras");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            System.err.println("Erro ao carregar a tela de login (" + VIEW_LOGIN + "):");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
