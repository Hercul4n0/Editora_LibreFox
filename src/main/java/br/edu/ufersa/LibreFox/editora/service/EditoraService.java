package main.java.br.edu.ufersa.LibreFox.editora.service;

import main.java.br.edu.ufersa.LibreFox.editora.entities.*;

import java.util.ArrayList;
import java.util.List;

public class EditoraService {
    //MÉTODOS DE GERENTE
    //Restringe o cadastro e exclusão dos avaliadores ao gerente

    public void cadastrarAvaliador(Editora editora, Gerente g, Avaliador avaliador){
        if (editora.getGerente().getCpf().equals(g.getCpf())) {
            editora.getAvaliadores().add(avaliador);
        }
    }
    public void excluirAvaliador (Editora editora, Gerente g, Avaliador avaliador){
        if (editora.getGerenteCpf().equals(g.getCpf())) {
            editora.getAvaliadores().remove(avaliador);
        }
    }

    // Restringe a exclusão de autores ao gerente
    public void excluirAutor (Editora editora, Gerente g, Autor autor){
        if (editora.getGerenteCpf().equals(g.getCpf())) {
            editora.getAutores().remove(autor);
        }
    }


    public void cadastrarAutor (Editora editora, Autor autor){
        editora.getAutores().add(autor);
    }

    //MÉTODOS DE AVALIADOR
    // Restringe a avaliação de obras aos avaliadores cadastrados
    public void avaliarObra (Editora editora, Avaliador avaliador, Obra obra, Short status){
        for (Avaliador a : editora.getAvaliadores()) {
            if (a.getCpf().equals(avaliador.getCpf())) {
                obra.setStatus(status);
            }
            else {System.out.println("Avaliador não cadastrado");}

        }
    }

    //MÉTODOS DE OBRA
    public void cadastrarObra (Editora editora, Obra obra){
        editora.getObras().add(obra);
    }

    //MÉTODOS DE BUSCA DE OBRAS

    public List<Obra> buscarObraTitulo (Editora editora, String titulo){
        List<Obra> obrasEncontradas = new ArrayList<>();
        if (titulo == null){
            System.out.println("Título não pode ser vazio");
        }
        for (Obra o : editora.getObras()) {
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

    public List<Obra> buscarObraStatus (Editora editora, String status){
        List<Obra> obrasEncontradas = new ArrayList<>();
        if (status == null){
            System.out.println("Status não pode ser vazio");
        }
        for (Obra o : editora.getObras()) {
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

    public List<Obra> buscarObraAno (Editora editora, Short ano){
        List<Obra> obrasEncontradas = new ArrayList<>();
        if (ano == null){
            System.out.println("Ano não pode ser vazio");
        }
        for (Obra o : editora.getObras()) {
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

    public List<Obra> buscarObraAutor (Editora editora, String nomeAutor){
        List<Obra> obrasEncontradas = new ArrayList<>();
        if (nomeAutor == null){
            System.out.println("Nome do autor não pode ser vazio");
        }
        for (Obra o : editora.getObras()) {
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

    public List<Avaliador> buscarAvaliadorNome (Editora editora, String nome){
        List<Avaliador> avaliadoresEncontrados = new ArrayList<>();
        if (nome == null){
            System.out.println("Nome do avaliador não pode ser vazio");
        }
        for (Avaliador a : editora.getAvaliadores()) {
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

    public List<Avaliador> buscarAvaliadorObra (Editora editora, String tituloObra){
        List<Avaliador> avaliadoresEncontrados = new ArrayList<>();
        if (tituloObra == null){
            System.out.println("Título da obra não pode ser vazio");
        }
        for (Avaliador a : editora.getAvaliadores()) {
            for (Obra o : a.getObrasparaAvaliar()) {
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

    public List<Autor> buscarAutorNome (Editora editora, String nome){
        List<Autor> autoresEncontrados = new ArrayList<>();
        if (nome == null){
            System.out.println("Nome do autor não pode ser vazio");
        }
        for (Autor a : editora.getAutores()) {
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

    public List<Autor> buscarAutorObra (Editora editora, String tituloObra){
        List<Autor> autoresEncontrados = new ArrayList<>();
        if (tituloObra == null){
            System.out.println("Título da obra não pode ser vazio");
        }
        for (Autor a : editora.getAutores()) {
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
