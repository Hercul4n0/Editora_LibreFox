package br.edu.ufersa.LibreFox.editora;

public class Obra {
    private String titulo, genero;
    private Short ano, status;
    private Autor autor;
    private Avaliador avaliador;
    
    //Construtores

    public Obra (String titulo, String genero, Short ano, Short status, Autor autor){
        setTitulo(titulo); setGenero(genero); setAno(ano); setStatus(status); setAutor(autor);
    }
    public Obra (String titulo, Short status, Autor autor){
        setTitulo(titulo); setStatus(status); setAutor(autor);
    }

    //Alterar

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

}
