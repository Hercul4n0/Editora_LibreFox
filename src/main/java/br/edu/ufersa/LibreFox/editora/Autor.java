package br.edu.ufersa.LibreFox.editora;
import java.util.ArrayList;
import java.util.List;


public class Autor extends Usuario {
    private List<Obra> obrasEnviadas = new ArrayList<>();

    //Construtor
    public Autor (String nome, String cpf, String endereco){
        super(nome, cpf, endereco);
    }

    public void enviarObra (Obra obra, Editora editora){
        obrasEnviadas.add(obra);
    }
    
    public List<Obra> getObrasEnviadas (){
        return obrasEnviadas;
    }
}
