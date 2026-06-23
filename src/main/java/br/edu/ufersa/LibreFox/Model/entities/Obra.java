package br.edu.ufersa.LibreFox.Model.entities;

import java.time.LocalDate;

public class Obra {
    private String id;
    private String titulo;
    private String genero;
    private Short ano;
    private Short status;
    private Autor autor;
    private Avaliador avaliador;
    private LocalDate dataSubmissao;   // quando o autor enviou a obra
    private LocalDate dataAvaliacao;   // quando o avaliador deu o veredicto

    // ERRO CORRIGIDO 10: havia um segundo construtor que deixava "genero" e "ano"
    // sempre nulos. Como esses campos são obrigatórios pelo mini mundo (toda Obra
    // precisa de Título, Gênero, Ano, Autor e Status), esse construtor permitia
    // criar uma Obra em estado inválido — e ao tentar persistir essa Obra, o
    // br.edu.ufersa.LibreFox.editora.DAO.ObraDAO.inserir() sofreria NullPointerException ao converter Short -> short.
    // Removido por não ser usado em nenhum lugar do código e por violar a regra
    // de negócio. Use sempre o construtor completo abaixo.
    public Obra(String titulo, String genero, Short ano, Short status, Autor autor, String id) {
        setTitulo(titulo);
        setGenero(genero);
        setAno(ano);
        setStatus(status);
        setAutor(autor);
        setId(id);
        this.dataSubmissao = LocalDate.now();
    }

    // -------------------------------------------------------------------------
    // GETTERS E SETTERS
    // -------------------------------------------------------------------------

    public String getId() { return id; }
    public void setId(String id) {
        if (id != null) this.id = id;
    }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) {
        if (titulo != null) this.titulo = titulo;
    }

    public String getGenero() { return genero; }
    public void setGenero(String genero) {
        if (genero != null) this.genero = genero;
    }

    public Short getAno() { return ano; }
    public void setAno(Short ano) {
        if (ano != null) this.ano = ano;
    }

    public Short getStatus() { return status; }
    public void setStatus(Short status) {
        if (status != null) this.status = status;
    }

    public Autor getAutor() { return autor; }
    public void setAutor(Autor autor) {
        if (autor != null) this.autor = autor;
    }

    public Avaliador getAvaliador() { return avaliador; }
    public void setAvaliador(Avaliador avaliador) {
        this.avaliador = avaliador;
        if (avaliador != null) {
            avaliador.getObrasParaAvaliar().add(this);
        }
    }

    public LocalDate getDataSubmissao() { return dataSubmissao; }
    public void setDataSubmissao(LocalDate dataSubmissao) {
        if (dataSubmissao != null) this.dataSubmissao = dataSubmissao;
    }

    public LocalDate getDataAvaliacao() { return dataAvaliacao; }
    public void setDataAvaliacao(LocalDate dataAvaliacao) {
        if (dataAvaliacao != null) this.dataAvaliacao = dataAvaliacao;
    }
}
