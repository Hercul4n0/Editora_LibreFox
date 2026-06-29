package br.edu.ufersa.LibreFox.Model.DAO;

import br.edu.ufersa.LibreFox.Model.entities.Endereco;
import br.edu.ufersa.LibreFox.Model.entities.Gerente;

import java.sql.*;
import java.util.ArrayList;

public class GerenteDAO extends UsuarioDAO<Gerente> {

    public GerenteDAO(Connection connection) {
        super(connection);
    }


    @Override
    public Gerente inserir(Gerente gerente) throws SQLException {
        enderecoDAO.salvar(gerente.getEndereco());
        long id = inserirUsuario(gerente);
        gerente.setId(id);
        salvarPerfis(id, gerente.getPerfis());
        return gerente;
    }

    @Override
    public ArrayList<Gerente> listar() throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE up.perfil = 'GERENTE'
                ORDER BY u.nome
                """;
        ArrayList<Gerente> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public void atualizar(Gerente gerente) throws SQLException {
        enderecoDAO.atualizar(gerente.getEndereco());
        atualizarUsuario(gerente);
        atualizarPerfis(gerente.getId(), gerente.getPerfis());
    }

    @Override
    public void deletar(Gerente gerente) throws SQLException {
        deletarUsuario(gerente.getId());
    }

    // BUSCAS ESPECÍFICAS

    public Gerente buscarPorId(long id) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.id = ? AND up.perfil = 'GERENTE'
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Gerente buscarPorCpf(String cpf) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.cpf = ? AND up.perfil = 'GERENTE'
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public Gerente buscarPorLogin(String login) throws SQLException {
        String sql = """
                SELECT DISTINCT u.* FROM usuario u
                JOIN usuario_perfil up ON u.id = up.usuario_id
                WHERE u.login = ? AND up.perfil = 'GERENTE'
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, login);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    @Override
    protected Gerente mapear(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        Endereco endereco = enderecoDAO.buscarPorId(rs.getLong("endereco_id"));

        Gerente gerente = new Gerente(
                rs.getString("nome"),
                rs.getString("cpf"),
                endereco,
                rs.getString("login"),
                rs.getString("senha"),
                buscarPerfis(id)
        );
        gerente.setId(id);
        return gerente;
    }
}
