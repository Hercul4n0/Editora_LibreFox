package br.edu.ufersa.LibreFox.editora.DAO;

import br.edu.ufersa.LibreFox.editora.entities.Avaliador;
import br.edu.ufersa.LibreFox.editora.entities.Obra;
import br.edu.ufersa.LibreFox.editora.entities.Relatorio;

import java.sql.*;
import java.time.LocalDate;

public class RelatorioDAO {

    private final Connection connection;
    private final ObraDAO obraDAO;

    public RelatorioDAO(Connection connection) {
        this.connection = connection;
        this.obraDAO = new ObraDAO(connection);
    }

    // -------------------------------------------------------------------------
    // CONSULTA: obras avaliadas em um dado período.
    //
    // A autorização (somente o gerente gera relatórios) fica no RelatorioService.
    // -------------------------------------------------------------------------

    /**
     * Todas as obras avaliadas (aceitas ou rejeitadas) no período.
     */
    public Relatorio gerarPorPeriodo(LocalDate dataInicial,
                                     LocalDate dataFinal) throws SQLException {
        String sql = """
                SELECT * FROM obra
                WHERE status IN (1, 2)
                  AND data_avaliacao BETWEEN ? AND ?
                ORDER BY data_avaliacao
                """;

        Relatorio relatorio = new Relatorio(dataInicial, dataFinal, null);
        preencherRelatorio(relatorio, sql, stmt -> {
            stmt.setDate(1, Date.valueOf(dataInicial));
            stmt.setDate(2, Date.valueOf(dataFinal));
        });
        return relatorio;
    }

    /**
     * Obras avaliadas por um avaliador específico no período.
     */
    public Relatorio gerarPorPeriodoEAvaliador(LocalDate dataInicial, LocalDate dataFinal,
                                               Avaliador avaliador) throws SQLException {
        String sql = """
                SELECT * FROM obra
                WHERE status IN (1, 2)
                  AND avaliador_id = ?
                  AND data_avaliacao BETWEEN ? AND ?
                ORDER BY data_avaliacao
                """;

        Relatorio relatorio = new Relatorio(dataInicial, dataFinal, avaliador);
        preencherRelatorio(relatorio, sql, stmt -> {
            stmt.setLong(1, avaliador.getId());
            stmt.setDate(2, Date.valueOf(dataInicial));
            stmt.setDate(3, Date.valueOf(dataFinal));
        });
        return relatorio;
    }

    // -------------------------------------------------------------------------
    // HELPER
    // -------------------------------------------------------------------------

    @FunctionalInterface
    private interface StatementFiller {
        void fill(PreparedStatement stmt) throws SQLException;
    }

    private void preencherRelatorio(Relatorio relatorio, String sql,
                                    StatementFiller filler) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            filler.fill(stmt);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Obra obra = obraDAO.mapear(rs);
                    relatorio.getObras().add(obra);
                }
            }
        }
    }
}
