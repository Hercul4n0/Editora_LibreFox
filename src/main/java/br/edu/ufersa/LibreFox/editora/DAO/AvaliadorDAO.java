package br.edu.ufersa.LibreFox.editora.DAO;

import br.edu.ufersa.LibreFox.editora.entities.Avaliador;
import br.edu.ufersa.LibreFox.editora.entities.Endereco;

import java.sql.*;
import java.util.ArrayList;

public class AvaliadorDAO extends UsuarioDAO<Avaliador> {

    public AvaliadorDAO(Connection connection) {
        super(connection);
    }

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — CREATE
    // -------------------------------------------------------------------------

    @Override
    public Avaliador inserir(Avaliador avaliador) throws SQLException {
        enderecoDAO.salvar(avaliador.getEndereco());
        long id = inserirUsuario(avaliador);
        avaliador.setId(id);
        salvarPerfis(id, avaliador.getPerfis());
        return avaliador;
    }

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — READ
    // -------------------------------------------------------------------------

    @Override
    public ArrayList<Avaliador> listar() throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE up.perfil = 'AVALIADOR'
                ORDER BY u.nome
                """;
        ArrayList<Avaliador> lista = new ArrayList<>();

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
    public void atualizar(Avaliador avaliador) throws SQLException {
        enderecoDAO.atualizar(avaliador.getEndereco());
        atualizarUsuario(avaliador);
        atualizarPerfis(avaliador.getId(), avaliador.getPerfis());
    }

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deletar(Avaliador avaliador) throws SQLException {
        deletarUsuario(avaliador.getId());
    }

    // -------------------------------------------------------------------------
    // BUSCAS ESPECÍFICAS
    // -------------------------------------------------------------------------

    public Avaliador buscarPorId(long id) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.id = ? AND up.perfil = 'AVALIADOR'
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Avaliador buscarPorCpf(String cpf) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.cpf = ? AND up.perfil = 'AVALIADOR'
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    // ERRO CORRIGIDO 7: buscarPorNome retornava apenas o primeiro resultado (if rs.next).
    // Corrigido para retornar lista com todos os resultados.
    public ArrayList<Avaliador> buscarPorNome(String nome) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.nome LIKE ? AND up.perfil = 'AVALIADOR'
                ORDER BY u.nome
                """;
        ArrayList<Avaliador> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nome + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public Avaliador buscarPorLogin(String login) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.login = ? AND up.perfil = 'AVALIADOR'
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Avaliador buscarPorObra(String obraId) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                JOIN obra o ON o.avaliador_id = u.id
                WHERE o.id = ? AND up.perfil = 'AVALIADOR'
                """;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obraId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public ArrayList<Avaliador> listarComObrasPendentes() throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                JOIN obra o ON o.avaliador_id = u.id
                WHERE up.perfil = 'AVALIADOR'
                  AND o.status = 0
                ORDER BY u.nome
                """;
        ArrayList<Avaliador> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // MAPEAMENTO
    // -------------------------------------------------------------------------

    @Override
    protected Avaliador mapear(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        Endereco endereco = enderecoDAO.buscarPorId(rs.getLong("endereco_id"));

        Avaliador avaliador = new Avaliador(
                rs.getString("nome"),
                rs.getString("cpf"),
                endereco,
                rs.getString("login"),
                rs.getString("senha"),
                buscarPerfis(id)
        );
        avaliador.setId(id);
        return avaliador;
    }
}
