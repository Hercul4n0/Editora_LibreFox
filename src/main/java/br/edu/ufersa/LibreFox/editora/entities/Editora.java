package main.java.br.edu.ufersa.LibreFox.editora.entities;
import java.util.ArrayList;
import java.util.List;

public class Editora {
    String name;
    Gerente gerente;
    List<Avaliador> avaliadores = new ArrayList<>();
    List<Autor> autores = new ArrayList<>();
    List<Obra> obras = new ArrayList<>();

    //Construtor
    public Editora(String nomeEditora, String nomeGerente, String cpf, String endereco, String login, String senha,
                   boolean isAutor, boolean isAvaliador, boolean isGerente) {
        setName(nomeEditora);
        setGerente(nomeGerente, cpf, endereco, login, senha, isAutor, isAvaliador, isGerente);
    }

    //GETTERS E SETTERS
    public void setName(String name) {
        if (name != null) {
            this.name = name;
        }
    }

    public String getName() {
        return name;
    }

    public void setGerente(String nome, String cpf, String endereco, String login, String senha,
                           boolean isAutor, boolean isAvaliador, boolean isGerente) {
        Gerente g = new Gerente(nome, cpf, endereco, login, senha, isAutor, isAvaliador, isGerente);
        gerente = g;
    }


    public Gerente getGerente() {
        return gerente;
    }

    public List<Avaliador> getAvaliadores() {
        return avaliadores;
    }

    public List<Autor> getAutores() {
        return autores;
    }

    public List<Obra> getObras() {
        return obras;
    }


    public String getGerenteCpf() {
        return gerente.getCpf();
    }
}



        
   
