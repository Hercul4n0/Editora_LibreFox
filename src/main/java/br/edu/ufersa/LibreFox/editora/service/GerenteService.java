package main.java.br.edu.ufersa.LibreFox.editora.service;

import main.java.br.edu.ufersa.LibreFox.editora.DAO.GerenteDAO;
import main.java.br.edu.ufersa.LibreFox.editora.entities.Gerente;
import java.util.ArrayList;

public class GerenteService {
    private GerenteDAO gerenteDAO;

    public GerenteService(GerenteDAO gerenteDAO){
        this.gerenteDAO = gerenteDAO;
    }

    public Gerente cadastrarGerente(Gerente gerente){
        if(gerente == null){
            System.out.println("Gerente inválido!");
            return null;
        }
        return gerenteDAO.inserir(gerente);
    }

    public void excluirGerente(Gerente gerente){
        if(gerente == null){
            System.out.println("Gerente inválido!");
            return;
        }
        gerenteDAO.deletar(gerente);
    }

    public void atualizarGerente(Gerente gerente){
        if(gerente == null){
            System.out.println("Gerente inválido!");
            return;
        }
        gerenteDAO.atualizar(gerente);
    }

    public Gerente buscarGerente(String parametro){
        if(parametro == null || parametro.isBlank()){
            System.out.println("Parâmetro inválido!");
            return null;
        }
        return gerenteDAO.buscar(parametro);
    }

    public ArrayList<Gerente> listarGerente(){
        return gerenteDAO.listar();
    }

}
