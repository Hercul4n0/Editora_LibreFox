package br.edu.ufersa.LibreFox.Model.DAO;

import br.edu.ufersa.LibreFox.Model.entities.Notificacao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacaoDAO {

    private final Connection connection;

    public NotificacaoDAO(Connection connection) {
        this.connection = connection;
    }

    public void inserir(Notificacao notificacao) throws SQLException {
        String sql = """
                INSERT INTO notificacao (usuario_id, mensagem, lida, data_criacao)
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, notificacao.getUsuarioId());
            stmt.setString(2, notificacao.getMensagem());
            stmt.setBoolean(3, notificacao.isLida());
            stmt.setTimestamp(4, Timestamp.valueOf(notificacao.getDataCriacao()));
            stmt.executeUpdate();
        }
    }

    public List<Notificacao> listarNaoLidas(long usuarioId) throws SQLException {
        String sql = """
                SELECT * FROM notificacao
                WHERE usuario_id = ? AND lida = FALSE
                ORDER BY data_criacao DESC
                """;
        List<Notificacao> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public void marcarTodasComoLidas(long usuarioId) throws SQLException {
        String sql = "UPDATE notificacao SET lida = TRUE WHERE usuario_id = ? AND lida = FALSE";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, usuarioId);
            stmt.executeUpdate();
        }
    }

    private Notificacao mapear(ResultSet rs) throws SQLException {
        Notificacao notificacao = new Notificacao(rs.getLong("usuario_id"), rs.getString("mensagem"));
        notificacao.setId(rs.getLong("id"));
        notificacao.setLida(rs.getBoolean("lida"));
        notificacao.setDataCriacao(rs.getTimestamp("data_criacao").toLocalDateTime());
        return notificacao;
    }
}
