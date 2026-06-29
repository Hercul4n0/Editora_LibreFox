package br.edu.ufersa.LibreFox.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Armazenamento dos arquivos das obras (o conteúdo que será avaliado).
 *
 * Os arquivos enviados pelo autor são copiados para uma pasta local
 * ("obras_arquivos", ao lado do diretório de execução) e o caminho resultante
 * é guardado na coluna {@code arquivo} da tabela obra. Assim o arquivo continua
 * disponível mesmo que o autor mova/apague o original.
 */
public final class ArquivoObra {

    /** Extensões aceitas para o arquivo da obra. */
    public static final List<String> EXTENSOES = List.of("txt", "pdf", "docx");

    private static final Path DIR =
            Paths.get(System.getProperty("user.dir"), "obras_arquivos");

    private ArquivoObra() {}

    /** Verifica se o arquivo tem uma extensão aceita (txt, pdf ou docx). */
    public static boolean extensaoValida(File arquivo) {
        if (arquivo == null) return false;
        String nome = arquivo.getName().toLowerCase();
        return EXTENSOES.stream().anyMatch(ext -> nome.endsWith("." + ext));
    }

    /**
     * Copia o arquivo de origem para a pasta de armazenamento, com um nome
     * único, e devolve o caminho absoluto da cópia (a ser salvo no banco).
     */
    public static String armazenar(File origem) throws IOException {
        Files.createDirectories(DIR);
        String nomeUnico = System.currentTimeMillis() + "_" + origem.getName();
        Path destino = DIR.resolve(nomeUnico);
        Files.copy(origem.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
        return destino.toAbsolutePath().toString();
    }
}
