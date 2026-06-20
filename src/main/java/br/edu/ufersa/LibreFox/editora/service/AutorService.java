package br.edu.ufersa.LibreFox.editora.service;

import br.edu.ufersa.LibreFox.editora.DAO.AutorDAO;
import br.edu.ufersa.LibreFox.editora.entities.Autor;
import br.edu.ufersa.LibreFox.editora.entities.Sessao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Casos de uso ligados a Autores.
 *
 * Orquestra o {@link AutorDAO} e aplica a autorização via {@link Sessao}:
 * cadastro de autor é aberto (auto-cadastro), mas a exclusão é uma operação
 * administrativa restrita ao gerente.
 */
public class AutorService {

    private final AutorDAO autorDAO;

    public AutorService(Connection connection) {
        this.autorDAO = new AutorDAO(connection);
    }

    // -------------------------------------------------------------------------
    // CADASTRO / ALTERAÇÃO / EXCLUSÃO
    // -------------------------------------------------------------------------

    /** Cadastra um novo autor (auto-cadastro — não exige perfil de gerente). */
    public Autor cadastrar(Autor autor) throws SQLException {
        if (autor == null) {
            throw new IllegalArgumentException("Autor não pode ser nulo.");
        }
        return autorDAO.inserir(autor);
    }

    /** Atualiza os dados de um autor existente. */
    public void alterar(Autor autor) throws SQLException {
        if (autor == null) {
            throw new IllegalArgumentException("Autor não pode ser nulo.");
        }
        autorDAO.atualizar(autor);
    }

    /** Exclui um autor. Operação administrativa — somente o gerente. */
    public void excluir(Autor autor, Sessao sessao) throws SQLException {
        if (autor == null) {
            throw new IllegalArgumentException("Autor não pode ser nulo.");
        }
        if (!sessao.podeGerenciar()) {
            throw new SecurityException("Apenas o gerente pode excluir autores.");
        }
        autorDAO.deletar(autor);
    }

    // -------------------------------------------------------------------------
    // BUSCAS
    // -------------------------------------------------------------------------

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

    /** Autor responsável por uma obra (busca de autores por obra). */
    public Autor buscarPorObra(String obraId) throws SQLException {
        return autorDAO.buscarPorObra(obraId);
    }
}
