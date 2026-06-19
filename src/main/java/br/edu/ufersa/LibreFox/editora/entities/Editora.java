package br.edu.ufersa.LibreFox.editora.entities;

import java.util.List;
import java.util.ArrayList;

/**
 * @deprecated Esta classe era usada para agregar o estado da editora em memória.
 * Com a introdução dos DAOs e da camada de serviço, toda a persistência e
 * gerenciamento de estado passou a ser responsabilidade dos DAOs e da Sessao.
 * Mantida apenas para não quebrar referências existentes.
 */
@Deprecated
public class Editora {
    private String name;
    private Gerente gerente;
    private List<Avaliador> avaliadores = new ArrayList<>();
    private List<Autor> autores = new ArrayList<>();
    private List<Obra> obras = new ArrayList<>();

    public Editora(String name, Gerente gerente) {
        this.name = name;
        this.gerente = gerente;
    }

    public String getName() { return name; }
    public Gerente getGerente() { return gerente; }
    public String getGerenteCpf() { return gerente.getCpf(); }
    public List<Avaliador> getAvaliadores() { return avaliadores; }
    public List<Autor> getAutores() { return autores; }
    public List<Obra> getObras() { return obras; }
}
