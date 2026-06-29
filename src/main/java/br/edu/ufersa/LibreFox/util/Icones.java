package br.edu.ufersa.LibreFox.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public final class Icones {

    private static final String BASE = "/Images/";
    private static final Map<String, Image> CACHE = new HashMap<>();

    private Icones() {}


    public static Image imagem(String nome) {
        return CACHE.computeIfAbsent(nome, n -> {
            InputStream is = Icones.class.getResourceAsStream(BASE + n);
            return is == null ? null : new Image(is);
        });
    }


     //Cria um ImageView quadrado de {@code tamanho} px para o ícone, ou null se o arquivo não for encontrado.
    public static ImageView icone(String nome, double tamanho) {
        Image img = imagem(nome);
        if (img == null) return null;
        ImageView iv = new ImageView(img);
        iv.setFitWidth(tamanho);
        iv.setFitHeight(tamanho);
        iv.setPreserveRatio(true);
        return iv;
    }
}
