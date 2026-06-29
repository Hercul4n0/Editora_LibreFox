package br.edu.ufersa.LibreFox.Model.DAO;

import br.edu.ufersa.LibreFox.Model.entities.Perfil;
import br.edu.ufersa.LibreFox.Model.entities.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Set;

public abstract class UsuarioDAO<T extends Usuario> implements BaseDAO<T> {

    protected final Connection connection;
    protected final EnderecoDAO enderecoDAO;

    protected UsuarioDAO(Connection connection) {
        this.connection = connection;
        this.enderecoDAO = new EnderecoDAO(connection);
    }

    @Override
    public abstract T inserir(T objeto) throws SQLException;

    @Override
    public abstract void deletar(T objeto) throws SQLException;

    @Override
    public abstract void atualizar(T objeto) throws SQLException;

    @Override
    public abstract ArrayList<T> listar() throws SQLException;

    protected long inserirUsuario(T usuario) throws SQLException {
        String sql = """
                INSERT INTO usuario (nome, cpf, endereco_id, login, senha)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setLong(3, usuario.getEndereco().getId());
            stmt.setString(4, usuario.getLogin());
            stmt.setString(5, usuario.getSenha());
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Falha ao obter ID gerado para o usuário.");
    }

    protected void atualizarUsuario(T usuario) throws SQLException {
        String sql = """
                UPDATE usuario
                SET nome = ?, cpf = ?, endereco_id = ?, login = ?, senha = ?
                WHERE id = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setLong(3, usuario.getEndereco().getId());
            stmt.setString(4, usuario.getLogin());
            stmt.setString(5, usuario.getSenha());
            stmt.setLong(6, usuario.getId());
            stmt.executeUpdate();
        }
    }

    protected void deletarUsuario(long id) throws SQLException {
        long enderecoId = buscarEnderecoId(id);
        deletarPerfis(id);

        String sql = "DELETE FROM usuario WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }

        if (enderecoId >= 0) enderecoDAO.deletar(enderecoId);
    }


    protected void salvarPerfis(long usuarioId, Set<Perfil> perfis) throws SQLException {
        String sql = "INSERT INTO usuario_perfil (usuario_id, perfil) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (Perfil perfil : perfis) {
                stmt.setLong(1, usuarioId);
                stmt.setString(2, perfil.name());
                stmt.addBatch();
            }
            stmt.executeBatch();
        }

        for (Perfil perfil : perfis) {
            salvarVinculoTabelaDePerfil(usuarioId, perfil);
        }
    }

    private void salvarVinculoTabelaDePerfil(long usuarioId, Perfil perfil) throws SQLException {
        String tabela = switch (perfil) {
            case AUTOR -> "autor";
            case AVALIADOR -> "avaliador";
            case GERENTE -> "gerente";
        };
        String sql = "INSERT IGNORE INTO " + tabela + " (id) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            stmt.executeUpdate();
        }
    }

    protected Set<Perfil> buscarPerfis(long usuarioId) throws SQLException {
        String sql = "SELECT perfil FROM usuario_perfil WHERE usuario_id = ?";
        Set<Perfil> perfis = EnumSet.noneOf(Perfil.class);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    perfis.add(Perfil.valueOf(rs.getString("perfil")));
                }
            }
        }
        return perfis;
    }

    protected void atualizarPerfis(long usuarioId, Set<Perfil> perfis) throws SQLException {
        deletarPerfis(usuarioId);
        salvarPerfis(usuarioId, perfis);
    }

    protected void deletarPerfis(long usuarioId) throws SQLException {
        String sql = "DELETE FROM usuario_perfil WHERE usuario_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            stmt.executeUpdate();
        }
    }

    protected long buscarEnderecoId(long usuarioId) throws SQLException {
        String sql = "SELECT endereco_id FROM usuario WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getLong("endereco_id");
            }
        }
        return -1;
    }

    // MAPEAMENTO — cada subDAO implementa para o seu tipo

    protected abstract T mapear(ResultSet rs) throws SQLException;
}