package br.edu.ufersa.LibreFox.editora.model.entities;


public class Usuario {
    

    private String nome;
    private String cpf;
    private String endereco;


    //CONSTRUTORES
    public Usuario (String nome, String cpf, String endereco){
       setNome(nome); setCpf(cpf); setEndereco(endereco);
    }
    
    //SETTERS E GETTERS
    //checam se os campos estão vazios
    public void setNome (String nome){
        if (this.nome != null) {
            this.nome = nome; }
    }
    public String getNome (){
        return nome;
    }
    public void setCpf (String cpf){
        if (this.cpf != null) {
            this.cpf = cpf;}
        }
    public String getCpf (){
        return cpf;
    }
    public void setEndereco (String endereco){
        if (this.endereco != null) {
            this.endereco = endereco;}
        }
    
    public String getEndereco (){
        return endereco;
    }

}

