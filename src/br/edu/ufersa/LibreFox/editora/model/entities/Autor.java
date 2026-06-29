package br.edu.ufersa.LibreFox.editora.model.entities;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;


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
        return Collections.unmodifiableList(obrasEnviadas);
    }
}
