package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.DAO.GerenteDAO;
import br.edu.ufersa.LibreFox.Model.entities.Gerente;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Casos de uso ligados a Gerentes.
 *
 * O cadastro de gerente é uma operação de bootstrap do sistema (alguém precisa
 * existir como gerente antes de qualquer regra "somente gerente" poder ser
 * aplicada), portanto não exige uma {@link br.edu.ufersa.LibreFox.Model.entities.Sessao}.
 */
public class GerenteService {

    private final GerenteDAO gerenteDAO;

    public GerenteService(Connection connection) {
        this.gerenteDAO = new GerenteDAO(connection);
    }

    // -------------------------------------------------------------------------
    // CADASTRO / ALTERAÇÃO / EXCLUSÃO
    // -------------------------------------------------------------------------

    public Gerente cadastrar(Gerente gerente) throws SQLException {
        if (gerente == null) {
            throw new IllegalArgumentException("Gerente não pode ser nulo.");
        }
        return gerenteDAO.inserir(gerente);
    }

    public void alterar(Gerente gerente) throws SQLException {
        if (gerente == null) {
            throw new IllegalArgumentException("Gerente não pode ser nulo.");
        }
        gerenteDAO.atualizar(gerente);
    }

    public void excluir(Gerente gerente) throws SQLException {
        if (gerente == null) {
            throw new IllegalArgumentException("Gerente não pode ser nulo.");
        }
        gerenteDAO.deletar(gerente);
    }

    // -------------------------------------------------------------------------
    // BUSCAS
    // -------------------------------------------------------------------------

    public ArrayList<Gerente> listar() throws SQLException {
        return gerenteDAO.listar();
    }

    public Gerente buscarPorId(long id) throws SQLException {
        return gerenteDAO.buscarPorId(id);
    }

    public Gerente buscarPorCpf(String cpf) throws SQLException {
        return gerenteDAO.buscarPorCpf(cpf);
    }

    public Gerente buscarPorLogin(String login) throws SQLException {
        return gerenteDAO.buscarPorLogin(login);
    }
}
