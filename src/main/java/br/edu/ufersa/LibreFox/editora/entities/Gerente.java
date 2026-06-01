package main.java.br.edu.ufersa.LibreFox.editora.entities;

public class Gerente extends Usuario{
    public Gerente (String nome, String cpf, String endereco, String login, String senha,
                    boolean isAutor, boolean isAvaliador, boolean isGerente){
        super(nome, cpf, endereco, login, senha, isAutor, isAvaliador, isGerente);
    }
}