package br.edu.ufersa.LibreFox.editora;
import java.util.ArrayList;
import java.util.List;

public class Avaliador extends Usuario{
    List<Obra> ObrasparaAvaliar = new ArrayList<>();
    
    
    //Construtor

    public Avaliador (String nome, String cpf, String endereco){
        super(nome, cpf, endereco);
    }

}
