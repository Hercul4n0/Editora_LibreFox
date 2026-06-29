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
    private LocalDate dataSubmissao;
    private LocalDate dataAvaliacao;
    private String arquivo;
    private String feedback;


    public Obra(String titulo, String genero, Short ano, Short status, Autor autor, String id) {
        setTitulo(titulo);
        setGenero(genero);
        setAno(ano);
        setStatus(status);
        setAutor(autor);
        setId(id);
        this.dataSubmissao = LocalDate.now();
    }

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

    public String getArquivo() { return arquivo; }
    public void setArquivo(String arquivo) { this.arquivo = arquivo; }

    /**
     * Comentário curto que o avaliador deixa ao aceitar ou rejeitar a obra,
     * sugerindo ajustes e correções ao autor. Visível para Autor, Avaliador
     * e Gerente — todos podem acompanhar o motivo da decisão.
     */
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}