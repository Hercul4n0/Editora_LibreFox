package br.edu.ufersa.LibreFox.editora.DAO;

import br.edu.ufersa.LibreFox.editora.entities.Endereco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EnderecoDAO {

    private final Connection connection;

    public EnderecoDAO(Connection connection) {
        this.connection = connection;
    }

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    public void salvar(Endereco endereco) throws SQLException {
        String sql = """
                INSERT INTO endereco (numero, bairro, logradouro, cidade, uf)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(
                sql, Statement.RETURN_GENERATED_KEYS)) {

            preencherStatement(stmt, endereco);
            stmt.executeUpdate();

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) endereco.setId(rs.getLong(1));
            }
        }
    }

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------

    public Endereco buscarPorId(long id) throws SQLException {
        String sql = "SELECT * FROM endereco WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public List<Endereco> listarTodos() throws SQLException {
        List<Endereco> lista = new ArrayList<>();
        String sql = "SELECT * FROM endereco";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    public void atualizar(Endereco endereco) throws SQLException {
        String sql = """
                UPDATE endereco
                SET numero = ?, bairro = ?, logradouro = ?, cidade = ?, uf = ?
                WHERE id = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            preencherStatement(stmt, endereco);
            stmt.setLong(6, endereco.getId());
            stmt.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    public void deletar(long id) throws SQLException {
        String sql = "DELETE FROM endereco WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // HELPERS
    // -------------------------------------------------------------------------

    private void preencherStatement(PreparedStatement stmt, Endereco e) throws SQLException {
        stmt.setString(1, e.getNumero());
        stmt.setString(2, e.getBairro());
        stmt.setString(3, e.getLogradouro());
        stmt.setString(4, e.getCidade());
        stmt.setString(5, e.getUf());
    }

    Endereco mapear(ResultSet rs) throws SQLException {
        Endereco e = new Endereco(
                rs.getString("numero"),
                rs.getString("bairro"),
                rs.getString("logradouro"),
                rs.getString("cidade"),
                rs.getString("uf")
        );
        e.setId(rs.getLong("id"));
        return e;
    }
}
