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
    
    //MÉTODOS DE OBRA
        public void cadastrarObra (Obra obra){
            obras.add(obra); 
        }

    //MÉTODOS DE BUSCA DE OBRAS
    
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
    //MÉTODOS DE BUSCA DE AVALIADORES

    public List<Avaliador> buscarAvaliadorNome (String nome){
        List<Avaliador> avaliadoresEncontrados = new ArrayList<>();
        if (nome == null){
                System.out.println("Nome do avaliador não pode ser vazio");
        }
        for (Avaliador a : avaliadores) {
            if (a.getNome().equals(nome)) {
                avaliadoresEncontrados.add(a);
                System.out.println("Avaliador encontrado: " + a.getNome());}
        }
        if (avaliadoresEncontrados.isEmpty()) {
            System.out.println("Nenhum avaliador encontrado com esse nome " + nome);
        }
        //Se for vazio, retorna uma lista vazia
        return avaliadoresEncontrados;
    }

    public List<Avaliador> buscarAvaliadorObra (String tituloObra){
        List<Avaliador> avaliadoresEncontrados = new ArrayList<>();
        if (tituloObra == null){
                System.out.println("Título da obra não pode ser vazio");
        }
        for (Avaliador a : avaliadores) {
            for (Obra o : a.ObrasparaAvaliar) {
                if (o.getTitulo().equals(tituloObra)) {
                    avaliadoresEncontrados.add(a);
                    System.out.println("Avaliador encontrado: " + a.getNome());}
            }
        }
        if (avaliadoresEncontrados.isEmpty()) {
            System.out.println("Nenhum avaliador encontrado para a obra " + tituloObra);
        }
        //Se for vazio, retorna uma lista vazia
        return avaliadoresEncontrados;
    }
    //MÉTODOS DE BUSCA DE AUTORES

    public List<Autor> buscarAutorNome (String nome){
        List<Autor> autoresEncontrados = new ArrayList<>();
        if (nome == null){
                System.out.println("Nome do autor não pode ser vazio");
        }
        for (Autor a : autores) {
            if (a.getNome().equals(nome)) {
                autoresEncontrados.add(a);
                System.out.println("Autor encontrado: " + a.getNome());}
        }
        if (autoresEncontrados.isEmpty()) {
            System.out.println("Nenhum autor encontrado com esse nome " + nome);
        }
        //Se for vazio, retorna uma lista vazia
        return autoresEncontrados;
    }

    public List<Autor> buscarAutorObra (String tituloObra){
        List<Autor> autoresEncontrados = new ArrayList<>();
        if (tituloObra == null){
                System.out.println("Título da obra não pode ser vazio");
        }
        for (Autor a : autores) {
            for (Obra o : a.getObrasEnviadas()) {
                if (o.getTitulo().equals(tituloObra)) {
                    autoresEncontrados.add(a);
                    System.out.println("Autor encontrado: " + a.getNome());}
            }
        }
        if (autoresEncontrados.isEmpty()) {
            System.out.println("Nenhum autor encontrado para a obra " + tituloObra);
        }
        //Se for vazio, retorna uma lista vazia
        return autoresEncontrados;
    }   
}


        
   
