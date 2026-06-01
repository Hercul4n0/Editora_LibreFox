package main.java.br.edu.ufersa.LibreFox.editora.entities;

public class Usuario {
    
    private long id;
    private String nome;
    private String cpf;
    private String endereco;
    private String login;
    private String senha;

    private boolean isAutor;
    private boolean isAvaliador;
    private boolean isGerente;

    //CONSTRUTORES
    public Usuario (String nome, String cpf, String endereco,
    String login, String senha, boolean isAutor, boolean isAvaliador, boolean isGerente){
       setNome(nome); setCpf(cpf); setEndereco(endereco); setLogin(login); setSenha(senha);
       setIsAutor(isAutor); setIsAvaliador(isAvaliador); setIsGerente(isGerente);

    }
    
    //SETTERS E GETTERS
    //checam se os campos estão vazios
    public void setNome (String nome){ //Encap corrigido
        if (nome != null) {
            this.nome = nome; }
    }
    public String getNome (){
        return nome;
    }

    public void setCpf (String cpf){ //Encap corrigido
        if (cpf != null) {
            this.cpf = cpf;}
        }
    public String getCpf (){
        return cpf;
    }

    public void setEndereco (String endereco){ // Encap corrigido
        if (endereco != null) {
            this.endereco = endereco;}
        }
    public String getEndereco (){
        return endereco;
    }

    public void setLogin(String login){
        if(login != null){
            this.login = login;
        }
    } // Encap corrigido
    public String getLogin (){
        return login;
    }

    public void setSenha(String senha){ // Encap corrigido
        if(senha != null){
            this.senha = senha;
        }
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


    public void setIsAutor(boolean isAutor){
        this.isAutor = isAutor;
    }
    public boolean getIsAutor(){
        return this.isAutor;
    }

    public void setIsAvaliador(boolean isAvaliador){
        this.isAvaliador = isAvaliador;
    }
    public boolean getIsAvaliador(){
        return this.isAvaliador;
    }

    public void setIsGerente(boolean isGerente){
        this.isGerente = isGerente;
    }
}

