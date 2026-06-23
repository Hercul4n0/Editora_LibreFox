package br.edu.ufersa.LibreFox;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TesteJavaFX extends Application{
    @Override
    public void start(Stage stage) {
        // Criar um label com mensagem
        Label label = new Label("🎉 JavaFX está funcionando corretamente!");
        label.setStyle("-fx-font-size: 18px; -fx-text-fill: #16213e;");

        // Criar um botão
        Button btnFechar = new Button("Fechar");
        btnFechar.setStyle("-fx-background-color: #16213e; -fx-text-fill: white; -fx-padding: 10 20;");
        btnFechar.setOnAction(e -> stage.close());

        // Organizar em um VBox
        VBox root = new VBox(20, label, btnFechar);
        root.setStyle("-fx-alignment: center; -fx-padding: 40; -fx-background-color: #f0f2f5;");

        // Criar a cena
        Scene scene = new Scene(root, 500, 300);

        // Configurar a janela
        stage.setTitle("Teste JavaFX - LibreFox");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
