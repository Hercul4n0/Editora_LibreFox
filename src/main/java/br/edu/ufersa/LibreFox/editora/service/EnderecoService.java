package main.java.br.edu.ufersa.LibreFox.editora.service;

import main.java.br.edu.ufersa.LibreFox.editora.DAO.EnderecoDAO;
import main.java.br.edu.ufersa.LibreFox.editora.entities.Endereco;

public class EnderecoService {
    private EnderecoDAO enderecoDAO;

    public EnderecoService(EnderecoDAO enderecoDAO){
        this.enderecoDAO = enderecoDAO;
    }

    public Endereco cadastrarEndereco(Endereco endereco){
        if(endereco == null){
            System.out.println("Endereço inválido!");
            return null;
        }
        return enderecoDAO.inserir(endereco);
    }
    public Endereco buscarEndereco(long id){
        if(id <= 0){
            System.out.println("Id inválido!");
            return null;
        }
        return enderecoDAO.buscarPorId(id);
    }
}
