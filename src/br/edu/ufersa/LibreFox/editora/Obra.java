package br.edu.ufersa.LibreFox.editora;

public class Obra {
    private String titulo;
    private String genero;
    private short ano;
    private short status;
    private Autor autor;
    private Avaliador avaliador;

    public Obra(String titulo, String genero, short ano, short status, Autor autor, Avaliador avaliador) {
        this.titulo = titulo;
        this.genero = genero;
        this.ano = ano;
        this.status = status;
        this.autor = autor;
        this.avaliador = avaliador;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public short getAno() {
        return ano;
    }

    public void setAno(short ano) {
        this.ano = ano;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
        this.status = status;
    }

    public Autor getAutor() {
        return autor;
    }

    public void setAutor(Autor autor) {
        this.autor = autor;
    }

    public Avaliador getAvaliador() {
        return avaliador;
    }

    public void setAvaliador(Avaliador avaliador) {
        this.avaliador = avaliador;
    }

    public void Alterar(String titulo, String genero, short ano, short status) {
        this.titulo = titulo;
        this.genero = genero;
        this.ano = ano;
        this.status = status;
    }

    public void Excluir() {
        this.titulo = null;
        this.genero = null;
        this.ano = 0;
        this.status = 0;
        this.autor = null;
        this.avaliador = null;
    }

    public void Buscar() {
        System.out.println("Título: " + titulo);
        System.out.println("Gênero: " + genero);
        System.out.println("Ano: " + ano);
        System.out.println("Status: " + status);

        if (autor != null) {
            System.out.println("Autor: " + autor.getNome());
        }

        if (avaliador != null) {
            System.out.println("Avaliador: " + avaliador.getNome());
        }
    }
}
