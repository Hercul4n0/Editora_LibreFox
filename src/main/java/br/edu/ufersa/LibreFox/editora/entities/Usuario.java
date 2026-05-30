package main.java.br.edu.ufersa.LibreFox.editora.entities;


public class Usuario {
    
    private long id;
    private String nome;
    private String cpf;
    private String endereco;
    private String login;
    private String senha;


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

    public String getLogin (){
        return login;
    }
    public String getSenha (){
        return senha;
    }

    public long getId (){
        return id;
    }

    public void setId (long id){
        this.id = id;
    }

}

