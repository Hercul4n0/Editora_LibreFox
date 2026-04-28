package br.edu.ufersa.LibreFox.editora;
import java.util.ArrayList;
import java.util.List;

public class Editora{
    String name;
    Gerente gerente;
    List<Avaliador> avaliadores = new ArrayList<>();
    List<Autor> autores = new ArrayList<>();
    List<Obra> obras = new ArrayList<>();

    //Construtor
    public Editora (String nomeEditora, String nomeGerente, String cpf, String endereco){
        setName(nomeEditora); setGerente(nomeGerente, cpf, endereco);
    }


    public void setName (String name){
        if (this.name!= null) {
            this.name = name; }
    }

    public String getName (){
        return name;
    }

    public void setGerente (String nome, String cpf, String endereco){
        Gerente g = new Gerente(nome, cpf, endereco);
        gerente = g;
    }

    public String getGerenteCpf (){
        return gerente.getCpf();
    }

    //Restringe o cadastro e exclusão dos avaliadores ao gerente
    public void cadastrarAvaliador(Gerente g, Avaliador avaliador){
       if (gerente.getCpf().equals(g.getCpf())) {
            avaliadores.add(avaliador);
       } 
    }
     public void excluirAvaliador (Gerente g, Avaliador avaliador){
         if (gerente.getCpf().equals(g.getCpf())) {
            avaliadores.remove(avaliador);
       } 
     }

     // Restringe a exclusão de autores ao gerente
     public void excluirAutor (Gerente g, Autor autor){
         if (gerente.getCpf().equals(g.getCpf())) {
             autores.remove(autor);
            } 
        }
      
     
        public void cadastrarAutor (Autor autor){
            autores.add(autor);
        }
        
    // Restringe a avaliação de obras aos avaliadores cadastrados
    public void avaliarObra (Avaliador avaliador, Obra obra, Short status){
       for (Avaliador a : avaliadores) { 
            if (a.getCpf().equals(avaliador.getCpf())) {
                obra.setStatus(status);
            }
            else {System.out.println("Avaliador não cadastrado");}
        
     }
    }


}