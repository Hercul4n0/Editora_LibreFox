package br.edu.ufersa.LibreFox.editora.DAO;

import br.edu.ufersa.LibreFox.editora.entities.Endereco;
import br.edu.ufersa.LibreFox.editora.entities.Gerente;

import java.sql.*;
import java.util.ArrayList;

public class GerenteDAO extends UsuarioDAO<Gerente> {

    public GerenteDAO(Connection connection) {
        super(connection);
    }

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — CREATE
    // -------------------------------------------------------------------------

    @Override
    public Gerente inserir(Gerente gerente) throws SQLException {
        enderecoDAO.salvar(gerente.getEndereco());
        long id = inserirUsuario(gerente);
        gerente.setId(id);
        salvarPerfis(id, gerente.getPerfis());
        return gerente;
    }

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — READ
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — UPDATE
    // -------------------------------------------------------------------------

    @Override
    public void atualizar(Gerente gerente) throws SQLException {
        enderecoDAO.atualizar(gerente.getEndereco());
        atualizarUsuario(gerente);
        atualizarPerfis(gerente.getId(), gerente.getPerfis());
    }

    // -------------------------------------------------------------------------
    // br.edu.ufersa.LibreFox.editora.DAO.BaseDAO — DELETE
    // -------------------------------------------------------------------------

    @Override
    public void deletar(Gerente gerente) throws SQLException {
        deletarUsuario(gerente.getId());
    }

    // -------------------------------------------------------------------------
    // BUSCAS ESPECÍFICAS
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // MAPEAMENTO
    // -------------------------------------------------------------------------

    // ERRO CORRIGIDO 9: mapear() usava o construtor de perfil único, sempre
    // hardcoded para {GERENTE}, descartando silenciosamente qualquer outro
    // perfil (AUTOR/AVALIADOR) que o usuário tivesse na tabela usuario_perfil.
    // Isso também tornava o construtor multi-perfil do Gerente inútil, pois
    // nada nunca lia de volta esses dados. Corrigido para chamar buscarPerfis,
    // no mesmo padrão de br.edu.ufersa.LibreFox.editora.DAO.AutorDAO e br.edu.ufersa.LibreFox.editora.DAO.AvaliadorDAO.
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
