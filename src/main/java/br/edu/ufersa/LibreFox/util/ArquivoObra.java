package br.edu.ufersa.LibreFox.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

public final class ArquivoObra {

    public static final List<String> EXTENSOES = List.of("txt", "pdf", "docx");

    private static final Path DIR =
            Paths.get(System.getProperty("user.dir"), "obras_arquivos");

    private ArquivoObra() {}

    // Verifica se o arquivo tem uma extensão aceita (txt, pdf ou docx).
    public static boolean extensaoValida(File arquivo) {
        if (arquivo == null) return false;
        String nome = arquivo.getName().toLowerCase();
        return EXTENSOES.stream().anyMatch(ext -> nome.endsWith("." + ext));
    }

    public static String armazenar(File origem) throws IOException {
        Files.createDirectories(DIR);
        String nomeUnico = System.currentTimeMillis() + "_" + origem.getName();
        Path destino = DIR.resolve(nomeUnico);
        Files.copy(origem.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return destino.toAbsolutePath().toString();
    }
}
