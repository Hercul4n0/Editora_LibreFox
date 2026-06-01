package main.java.br.edu.ufersa.LibreFox.editora.entities;
import java.util.ArrayList;
import java.util.List;

public class Avaliador extends Usuario{
    List<Obra> ObrasparaAvaliar = new ArrayList<>();
    //Construtor

    public List<Obra> getObrasparaAvaliar(){
        return ObrasparaAvaliar;
    }
    public Avaliador (String nome, String cpf, String endereco, String login, String senha,
                      boolean isAutor, boolean isAvaliador, boolean isGerente){
        super(nome, cpf, endereco, login, senha, isAutor, isAvaliador, isGerente);
    }

}
