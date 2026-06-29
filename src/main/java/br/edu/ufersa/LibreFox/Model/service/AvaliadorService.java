package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.DAO.AvaliadorDAO;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.Model.exceptions.AcessoNegadoException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class AvaliadorService {

    private final AvaliadorDAO avaliadorDAO;

    public AvaliadorService(Connection connection) {
        this.avaliadorDAO = new AvaliadorDAO(connection);
    }

    public Avaliador cadastrar(Avaliador avaliador, Sessao sessao) throws SQLException, AcessoNegadoException {
        if (avaliador == null) {
            throw new IllegalArgumentException("Avaliador não pode ser nulo.");
        }
        exigirGerente(sessao, "cadastrar avaliadores");
        return avaliadorDAO.inserir(avaliador);
    }

    public void alterar(Avaliador avaliador, Sessao sessao) throws SQLException, AcessoNegadoException {
        if (avaliador == null) {
            throw new IllegalArgumentException("Avaliador não pode ser nulo.");
        }
        exigirGerente(sessao, "alterar avaliadores");
        avaliadorDAO.atualizar(avaliador);
    }

    public void excluir(Avaliador avaliador, Sessao sessao) throws SQLException, AcessoNegadoException {
        if (avaliador == null) {
            throw new IllegalArgumentException("Avaliador não pode ser nulo.");
        }
        exigirGerente(sessao, "excluir avaliadores");
        avaliadorDAO.deletar(avaliador);
    }


    public ArrayList<Avaliador> listar() throws SQLException {
        return avaliadorDAO.listar();
    }

    public Avaliador buscarPorId(long id) throws SQLException {
        return avaliadorDAO.buscarPorId(id);
    }

    public Avaliador buscarPorCpf(String cpf) throws SQLException {
        return avaliadorDAO.buscarPorCpf(cpf);
    }

    public ArrayList<Avaliador> buscarPorNome(String nome) throws SQLException {
        return avaliadorDAO.buscarPorNome(nome);
    }

    public Avaliador buscarPorLogin(String login) throws SQLException {
        return avaliadorDAO.buscarPorLogin(login);
    }

    public Avaliador buscarPorObra(String obraId) throws SQLException {
        return avaliadorDAO.buscarPorObra(obraId);
    }

    public ArrayList<Avaliador> listarComObrasPendentes() throws SQLException {
        return avaliadorDAO.listarComObrasPendentes();
    }


    private void exigirGerente(Sessao sessao, String acao) throws AcessoNegadoException {
        if (sessao == null || !sessao.podeGerenciar()) {
            throw new AcessoNegadoException("Apenas o gerente pode " + acao + ".");
        }
    }
}