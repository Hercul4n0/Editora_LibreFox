package main.java.br.edu.ufersa.LibreFox.editora.entities;
import main.java.br.edu.ufersa.LibreFox.editora.service.AutorService;
import java.util.ArrayList;
import java.util.List;


public class Autor extends Usuario {
    private List<Obra> obrasEnviadas = new ArrayList<>();

    //Construtor
    public Autor (String nome, String cpf, String endereco, String login, String senha,
                  boolean isAutor, boolean isAvaliador, boolean isGerente){
        super(nome, cpf, endereco, login, senha, isAutor, isAvaliador, isGerente);
    }

    public List<Obra> getObrasEnviadas(){
        return obrasEnviadas;
    }

//    public void enviarObra (Obra obra, Editora editora){
//        obrasEnviadas.add(obra);
//    }
//    public List<Obra> getObrasEnviadas (){
//        return obrasEnviadas;
//    }
}
