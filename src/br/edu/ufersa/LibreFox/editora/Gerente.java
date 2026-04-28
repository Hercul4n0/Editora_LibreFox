package br.edu.ufersa.LibreFox.editora;

import java.util.ArrayList;

public class Gerente {

    // Armazena o nome do gerente
    private String nome;

    // Armazena o endereço do gerente
    private String endereco;

    // Armazena o CPF do gerente
    private String cpf;

    // Lista de autores cadastrados
    private ArrayList<Autor> autores;

    // Lista de avaliadores cadastrados
    private ArrayList<Avaliador> avaliadores;

    // Lista de obras cadastradas
    private ArrayList<Obra> obras;

    // Construtor da classe
    public Gerente(String nome, String endereco, String cpf) {
        this.nome = nome;
        this.endereco = endereco;
        this.cpf = cpf;

        // Inicializa as listas
        autores = new ArrayList<>();
        avaliadores = new ArrayList<>();
        obras = new ArrayList<>();
    }

    // Retorna o nome do gerente
    public String getNome() {
        return nome;
    }

    // Altera o nome do gerente
    public void setNome(String nome) {
        this.nome = nome;
    }

    // Retorna o endereço do gerente
    public String getEndereco() {
        return endereco;
    }

    // Altera o endereço do gerente
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    // Retorna o CPF do gerente
    public String getCpf() {
        return cpf;
    }

    // Altera o CPF do gerente
    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    // Cadastra um novo autor e o adiciona à lista
    public Autor CadastrarAutor(String nome, String endereco, String cpf) {
        Autor autor = new Autor(nome, endereco, cpf);
        autores.add(autor);
        return autor;
    }

    // Cadastra um novo avaliador e o adiciona à lista
    public Avaliador CadastrarAvaliador(String nome, String endereco, String cpf) {
        Avaliador avaliador = new Avaliador(nome, endereco, cpf);
        avaliadores.add(avaliador);
        return avaliador;
    }

    // Cadastra uma nova obra e a adiciona à lista
    public Obra CadastrarObra(String titulo, String genero, short ano, short status, Autor autor, Avaliador avaliador) {
        Obra obra = new Obra(titulo, genero, ano, status, autor, avaliador);
        obras.add(obra);
        return obra;
    }

    // Define ou altera o avaliador responsável por uma obra
    public void DefinirAvaliador(Obra obra, Avaliador avaliador) {
        obra.setAvaliador(avaliador);
    }

    // Gera um relatório com base no objeto informado
    public void GerarRelatorio(Relatorio relatorio) {
        relatorio.gerarRelatorio();
    }

    // Busca um autor pelo nome
    public Autor BuscarAutorPorNome(String nome) {
        for (Autor autor : autores) {
            if (autor.getNome().equalsIgnoreCase(nome)) {
                return autor;
            }
        }
        return null;
    }

    // Busca um avaliador pelo nome
    public Avaliador BuscarAvaliadorPorNome(String nome) {
        for (Avaliador avaliador : avaliadores) {
            if (avaliador.getNome().equalsIgnoreCase(nome)) {
                return avaliador;
            }
        }
        return null;
    }

    // Busca uma obra pelo título
    public Obra BuscarObraPorTitulo(String titulo) {
        for (Obra obra : obras) {
            if (obra.getTitulo().equalsIgnoreCase(titulo)) {
                return obra;
            }
        }
        return null;
    }
}