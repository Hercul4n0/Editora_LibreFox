package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.DAO.AvaliadorDAO;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Casos de uso ligados a Avaliadores.
 *
 * Regra de negócio: <b>somente o gerente</b> pode cadastrar, alterar e excluir
 * avaliadores. As buscas são abertas (usadas, por exemplo, na designação de
 * avaliadores e em relatórios).
 */
public class AvaliadorService {

    private final AvaliadorDAO avaliadorDAO;

    public AvaliadorService(Connection connection) {
        this.avaliadorDAO = new AvaliadorDAO(connection);
    }

    // -------------------------------------------------------------------------
    // CADASTRO / ALTERAÇÃO / EXCLUSÃO — restritos ao gerente
    // -------------------------------------------------------------------------

    public Avaliador cadastrar(Avaliador avaliador, Sessao sessao) throws SQLException {
        if (avaliador == null) {
            throw new IllegalArgumentException("Avaliador não pode ser nulo.");
        }
        exigirGerente(sessao, "cadastrar avaliadores");
        return avaliadorDAO.inserir(avaliador);
    }

    public void alterar(Avaliador avaliador, Sessao sessao) throws SQLException {
        if (avaliador == null) {
            throw new IllegalArgumentException("Avaliador não pode ser nulo.");
        }
        exigirGerente(sessao, "alterar avaliadores");
        avaliadorDAO.atualizar(avaliador);
    }

    public void excluir(Avaliador avaliador, Sessao sessao) throws SQLException {
        if (avaliador == null) {
            throw new IllegalArgumentException("Avaliador não pode ser nulo.");
        }
        exigirGerente(sessao, "excluir avaliadores");
        avaliadorDAO.deletar(avaliador);
    }

    // -------------------------------------------------------------------------
    // BUSCAS
    // -------------------------------------------------------------------------

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

    /** Avaliador designado para uma obra (busca de avaliadores por obra). */
    public Avaliador buscarPorObra(String obraId) throws SQLException {
        return avaliadorDAO.buscarPorObra(obraId);
    }

    /** Avaliadores que ainda têm obras pendentes de avaliação. */
    public ArrayList<Avaliador> listarComObrasPendentes() throws SQLException {
        return avaliadorDAO.listarComObrasPendentes();
    }

    // -------------------------------------------------------------------------
    // AUTORIZAÇÃO
    // -------------------------------------------------------------------------

    private void exigirGerente(Sessao sessao, String acao) {
        if (sessao == null || !sessao.podeGerenciar()) {
            throw new SecurityException("Apenas o gerente pode " + acao + ".");
        }
    }
}
