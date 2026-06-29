package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.DAO.AutorDAO;
import br.edu.ufersa.LibreFox.Model.entities.Autor;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;


public class AutorService {

    private final AutorDAO autorDAO;

    public AutorService(Connection connection) {
        this.autorDAO = new AutorDAO(connection);
    }

    public Autor cadastrar(Autor autor) throws SQLException {
        if (autor == null) {
            throw new IllegalArgumentException("Autor não pode ser nulo.");
        }
        return autorDAO.inserir(autor);
    }

    public void alterar(Autor autor) throws SQLException {
        if (autor == null) {
            throw new IllegalArgumentException("Autor não pode ser nulo.");
        }
        autorDAO.atualizar(autor);
    }

    public void excluir(Autor autor, Sessao sessao) throws SQLException {
        if (autor == null) {
            throw new IllegalArgumentException("Autor não pode ser nulo.");
        }
        if (!sessao.podeGerenciar()) {
            throw new SecurityException("Apenas o gerente pode excluir autores.");
        }
        autorDAO.deletar(autor);
    }

    public ArrayList<Autor> listar() throws SQLException {
        return autorDAO.listar();
    }

    public Autor buscarPorId(long id) throws SQLException {
        return autorDAO.buscarPorId(id);
    }

    public Autor buscarPorCpf(String cpf) throws SQLException {
        return autorDAO.buscarPorCpf(cpf);
    }

    public ArrayList<Autor> buscarPorNome(String nome) throws SQLException {
        return autorDAO.buscarPorNome(nome);
    }

    public Autor buscarPorLogin(String login) throws SQLException {
        return autorDAO.buscarPorLogin(login);
    }

    public Autor buscarPorObra(String obraId) throws SQLException {
        return autorDAO.buscarPorObra(obraId);
    }
}
