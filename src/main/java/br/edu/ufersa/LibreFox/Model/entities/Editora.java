package br.edu.ufersa.LibreFox.Model.entities;

import java.util.List;
import java.util.ArrayList;

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
