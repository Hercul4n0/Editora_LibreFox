package br.edu.ufersa.LibreFox.editora.model.entities;
import java.time.LocalDate;

public class Obra {
    private String titulo, genero;
    private Short ano, status;
    private Autor autor;
    private Avaliador avaliador;
    private String id;
    private LocalDate dataDeCadastro;
    private LocalDate dataDeAprovacao;
    
    //Construtores

    public Obra (String titulo, String genero, Short ano, Short status, Autor autor, String id){
        setTitulo(titulo); setGenero(genero); setAno(ano); setStatus(status); setAutor(autor); setId(id);

        
    }
    public Obra (String titulo, Short status, Autor autor, String id){
        setTitulo(titulo); setStatus(status); setAutor(autor); setId(id);
    }

    //Setters e Getters

    public void setTitulo (String titulo){
        if (this.titulo!= null) {
            this.titulo = titulo; }
    }

    public String getTitulo (){
        return titulo;
    }

    public void setGenero (String genero){
        if (this.genero!= null) {
            this.genero = genero; }
    }

    public String getGenero (){
        return genero;
    }

    public void setAno (Short ano){
        if (this.ano!= null) {
            this.ano = ano; }
    }   

    public Short getAno (){
        return ano;
    }   

    public void setStatus (Short status){
        if (this.status!= null) {
            this.status = status; }
    }   

    public Short getStatus (){
        return status;
    } 
    
    public void setAutor (Autor autor){
        if (this.autor!= null) {
            this.autor = autor; }
    }   

    public Autor getAutor (){
        return autor;
    }   

    public void setAvaliador (Avaliador avaliador){ 
        this.avaliador = avaliador;
    }

    public Avaliador getAvaliador () {
        return avaliador;
    }
    
    public void setId (String id){
        if (this.id!= null) {
            this.id = id; }
    }
    
    public String getId (){
        return id;
    }

    public void setDataDeCadastro (){
        this.dataDeCadastro = LocalDate.now();
    }

    public LocalDate getDataDeCadastro (){
        return  dataDeCadastro;
    }

    public void setDataDeAprovacao (){
        this.dataDeAprovacao = LocalDate.now();
    }

    public LocalDate getDataDeAprovacao (){
        return dataDeAprovacao;
    }


}
