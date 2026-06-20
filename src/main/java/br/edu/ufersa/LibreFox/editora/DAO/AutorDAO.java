package br.edu.ufersa.LibreFox.editora.DAO;

import br.edu.ufersa.LibreFox.editora.entities.Autor;
import br.edu.ufersa.LibreFox.editora.entities.Endereco;

import java.sql.*;
import java.util.ArrayList;

public class AutorDAO extends UsuarioDAO<Autor> {

    public AutorDAO(Connection connection) {
        super(connection);
    }

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — CREATE
    // -------------------------------------------------------------------------

    @Override
    public Autor inserir(Autor autor) throws SQLException {
        enderecoDAO.salvar(autor.getEndereco());
        long id = inserirUsuario(autor);
        autor.setId(id);
        salvarPerfis(id, autor.getPerfis());
        return autor;
    }

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — READ
    // -------------------------------------------------------------------------

    @Override
    public ArrayList<Autor> listar() throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE up.perfil = 'AUTOR'
                ORDER BY u.nome
                """;
        ArrayList<Autor> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — UPDATE
    // -------------------------------------------------------------------------

    @Override
    public void atualizar(Autor autor) throws SQLException {
        enderecoDAO.atualizar(autor.getEndereco());
        atualizarUsuario(autor);
        atualizarPerfis(autor.getId(), autor.getPerfis());
    }

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deletar(Autor autor) throws SQLException {
        deletarUsuario(autor.getId());
    }

    // -------------------------------------------------------------------------
    // BUSCAS ESPECÍFICAS
    // -------------------------------------------------------------------------

    public Autor buscarPorId(long id) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.id = ? AND up.perfil = 'AUTOR'
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Autor buscarPorCpf(String cpf) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.cpf = ? AND up.perfil = 'AUTOR'
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ERRO CORRIGIDO 6: buscarPorNome declarava ArrayList<Autor> lista sem usá-la,
    // e retornava apenas o primeiro resultado. Corrigido para retornar todos os resultados.
    public ArrayList<Autor> buscarPorNome(String nome) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.nome LIKE ? AND up.perfil = 'AUTOR'
                ORDER BY u.nome
                """;
        ArrayList<Autor> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Autor buscarPorLogin(String login) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.login = ? AND up.perfil = 'AUTOR'
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Autor buscarPorObra(String obraId) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                JOIN obra o ON o.autor_id = u.id
                WHERE o.id = ? AND up.perfil = 'AUTOR'
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obraId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // MAPEAMENTO
    // -------------------------------------------------------------------------

    @Override
    protected Autor mapear(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        Endereco endereco = enderecoDAO.buscarPorId(rs.getLong("endereco_id"));

        Autor autor = new Autor(
                rs.getString("nome"),
                rs.getString("cpf"),
                endereco,
                rs.getString("login"),
                rs.getString("senha"),
                buscarPerfis(id)
        );
        autor.setId(id);
        return autor;
    }
}
