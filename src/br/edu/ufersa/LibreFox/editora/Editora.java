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

    //GETTERS E SETTERS
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

    //MÉTODOS DE GERENTE
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
     
    //MÉTODOS DE AVALIADOR
    // Restringe a avaliação de obras aos avaliadores cadastrados
    public void avaliarObra (Avaliador avaliador, Obra obra, Short status){
       for (Avaliador a : avaliadores) { 
            if (a.getCpf().equals(avaliador.getCpf())) {
                obra.setStatus(status);
            }
            else {System.out.println("Avaliador não cadastrado");}
        
     }
    }
    //MÉTODOS DE BUSCA
    
    public List<Obra> buscarObraTitulo (String titulo){
        List<Obra> obrasEncontradas = new ArrayList<>();
        if (titulo == null){
                System.out.println("Título não pode ser vazio");
        } 
        for (Obra o : obras) {
            if (o.getTitulo().equals(titulo)) {
                obrasEncontradas.add(o);
                System.out.println("Obra encontrada: " + o.getTitulo());
            }
        }
        if (obrasEncontradas.isEmpty()) {
            System.out.println("Nenhuma obra encontrada para o título " + titulo);
        }
        return obrasEncontradas;
    }

    public List<Obra> buscarObraStatus (String status){
        List<Obra> obrasEncontradas = new ArrayList<>();
        if (status == null){
                System.out.println("Status não pode ser vazio");
        }
        for (Obra o : obras) {
            if (o.getStatus().equals(status)) {
                obrasEncontradas.add(o);
                System.out.println("Obra encontrada: " + o.getTitulo());
            }  
        }
        if (obrasEncontradas.isEmpty()) {
            System.out.println("Nenhuma obra encontrada para o status " + status);
        }
        //Se for vazio, retorna uma lista vazia
        return obrasEncontradas;
        }   

    public List<Obra> buscarObraAno (Short ano){
        List<Obra> obrasEncontradas = new ArrayList<>();
        if (ano == null){
                System.out.println("Ano não pode ser vazio");
        }
        for (Obra o : obras) {
            if (o.getAno().equals(ano)) {
                obrasEncontradas.add(o);
                System.out.println("Obra encontrada: " + o.getTitulo());}
        }
        if (obrasEncontradas.isEmpty()) {
            System.out.println("Nenhuma obra encontrada para o ano " + ano);
        }
        //Se for vazio, retorna uma lista vazia
        return obrasEncontradas; 
        }

    public List<Obra> buscarObraAutor (String nomeAutor){
        List<Obra> obrasEncontradas = new ArrayList<>();
        if (nomeAutor == null){
                System.out.println("Nome do autor não pode ser vazio");
        }
        for (Obra o : obras) {
            if (o.getAutor().getNome().equals(nomeAutor)) {
                obrasEncontradas.add(o);
                System.out.println("Obra encontrada: " + o.getTitulo());}
        }
        if (obrasEncontradas.isEmpty()) {
            System.out.println("Nenhuma obra encontrada para o autor " + nomeAutor);
        }
        //Se for vazio, retorna uma lista vazia
        return obrasEncontradas;
    }
}


        
   
