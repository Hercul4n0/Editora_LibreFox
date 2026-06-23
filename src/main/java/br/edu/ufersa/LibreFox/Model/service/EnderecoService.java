package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.DAO.EnderecoDAO;
import br.edu.ufersa.LibreFox.Model.entities.Endereco;
import java.sql.SQLException;

public class EnderecoService {
    private EnderecoDAO enderecoDAO;

    public EnderecoService(EnderecoDAO enderecoDAO){
        this.enderecoDAO = enderecoDAO;
    }

    public void cadastrarEndereco(Endereco endereco){
        if(endereco == null){
            System.out.println("Endereço inválido.");
            return;
        }

        try {
            enderecoDAO.salvar(endereco);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void buscarEndereco(long id){
        try {
            enderecoDAO.buscarPorId(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
