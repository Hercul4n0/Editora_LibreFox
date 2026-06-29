package br.edu.ufersa.LibreFox.Model.DAO;

import br.edu.ufersa.LibreFox.Model.entities.Autor;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Obra;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class ObraDAO implements BaseDAO<Obra> {

    private final Connection connection;
    private final AutorDAO autorDAO;
    private final AvaliadorDAO avaliadorDAO;

    public ObraDAO(Connection connection) {
        this.connection = connection;
        this.autorDAO = new AutorDAO(connection);
        this.avaliadorDAO = new AvaliadorDAO(connection);
    }

    @Override
    public Obra inserir(Obra obra) throws SQLException {
        validarCamposObrigatorios(obra);

        String sql = """
                INSERT INTO obra (id, titulo, genero, ano, status, autor_id, data_submissao, arquivo)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obra.getId());
            stmt.setString(2, obra.getTitulo());
            stmt.setString(3, obra.getGenero());
            stmt.setShort(4, obra.getAno());
            stmt.setShort(5, obra.getStatus());
            stmt.setLong(6, obra.getAutor().getId());
            stmt.setDate(7, Date.valueOf(obra.getDataSubmissao()));
            stmt.setString(8, obra.getArquivo());
            stmt.executeUpdate();
        }
        return obra;
    }

    @Override
    public ArrayList<Obra> listar() throws SQLException {
        String sql = "SELECT * FROM obra ORDER BY titulo";
        ArrayList<Obra> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(mapear(rs));
        }
        return lista;
    }

    @Override
    public void atualizar(Obra obra) throws SQLException {
        validarCamposObrigatorios(obra);

        String sql = """
                UPDATE obra
                SET titulo = ?, genero = ?, ano = ?, status = ?,
                    autor_id = ?, avaliador_id = ?,
                    data_submissao = ?, data_avaliacao = ?, arquivo = ?, feedback = ?
                WHERE id = ?
                """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obra.getTitulo());
            stmt.setString(2, obra.getGenero());
            stmt.setShort(3, obra.getAno());
            stmt.setShort(4, obra.getStatus());
            stmt.setLong(5, obra.getAutor().getId());

            if (obra.getAvaliador() != null)
                stmt.setLong(6, obra.getAvaliador().getId());
            else
                stmt.setNull(6, Types.BIGINT);

            stmt.setDate(7, Date.valueOf(obra.getDataSubmissao()));

            if (obra.getDataAvaliacao() != null)
                stmt.setDate(8, Date.valueOf(obra.getDataAvaliacao()));
            else
                stmt.setNull(8, Types.DATE);

            stmt.setString(9, obra.getArquivo());
            stmt.setString(10, obra.getFeedback());
            stmt.setString(11, obra.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void deletar(Obra obra) throws SQLException {
        String sql = "DELETE FROM obra WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, obra.getId());
            stmt.executeUpdate();
        }
    }

    public Obra buscarPorId(String id) throws SQLException {
        String sql = "SELECT * FROM obra WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapear(rs);
            }
        }
        return null;
    }

    public ArrayList<Obra> buscarPorTitulo(String titulo) throws SQLException {
        String sql = "SELECT * FROM obra WHERE titulo LIKE ? ORDER BY titulo";
        ArrayList<Obra> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + titulo + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Obra> buscarPorAutor(long autorId) throws SQLException {
        String sql = "SELECT * FROM obra WHERE autor_id = ? ORDER BY titulo";
        ArrayList<Obra> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, autorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Obra> buscarPorStatus(short status) throws SQLException {
        String sql = "SELECT * FROM obra WHERE status = ? ORDER BY titulo";
        ArrayList<Obra> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setShort(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Obra> buscarPorAno(short ano) throws SQLException {
        String sql = "SELECT * FROM obra WHERE ano = ? ORDER BY titulo";
        ArrayList<Obra> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setShort(1, ano);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    public ArrayList<Obra> buscarPorAvaliador(long avaliadorId) throws SQLException {
        String sql = "SELECT * FROM obra WHERE avaliador_id = ? ORDER BY titulo";
        ArrayList<Obra> lista = new ArrayList<>();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avaliadorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(mapear(rs));
            }
        }
        return lista;
    }

    // -------------------------------------------------------------------------
    // PERSISTÊNCIA DE WORKFLOW
    //
    // Estes métodos são apenas escrita no banco. A autorização (quem pode
    // designar/avaliar) e as regras de negócio (avaliador designado, status
    // válido, etc.) ficam na camada de service (ObraService).
    // -------------------------------------------------------------------------

    /** Persiste a designação de um avaliador para a obra. */
    public void definirAvaliador(Obra obra, Avaliador avaliador) throws SQLException {
        String sql = "UPDATE obra SET avaliador_id = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, avaliador.getId());
            stmt.setString(2, obra.getId());
            stmt.executeUpdate();
        }
    }

    /** Persiste o veredicto de uma avaliação (status + data + feedback). */
    public void registrarAvaliacao(Obra obra, short novoStatus,
                                   LocalDate dataAvaliacao, String feedback) throws SQLException {
        String sql = "UPDATE obra SET status = ?, data_avaliacao = ?, feedback = ? WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setShort(1, novoStatus);
            stmt.setDate(2, Date.valueOf(dataAvaliacao));
            stmt.setString(3, feedback);
            stmt.setString(4, obra.getId());
            stmt.executeUpdate();
        }
    }

    // -------------------------------------------------------------------------
    // VALIDAÇÃO
    // -------------------------------------------------------------------------

    private void validarCamposObrigatorios(Obra obra) {
        if (obra.getTitulo() == null || obra.getGenero() == null ||
                obra.getAno() == null || obra.getStatus() == null ||
                obra.getAutor() == null) {
            throw new IllegalArgumentException(
                    "Obra precisa ter título, gênero, ano, status e autor definidos.");
        }
    }

    // -------------------------------------------------------------------------
    // MAPEAMENTO
    // -------------------------------------------------------------------------

    Obra mapear(ResultSet rs) throws SQLException {
        Autor autor = autorDAO.buscarPorId(rs.getLong("autor_id"));

        Obra obra = new Obra(
                rs.getString("titulo"),
                rs.getString("genero"),
                rs.getShort("ano"),
                rs.getShort("status"),
                autor,
                rs.getString("id")
        );

        Date dataSub = rs.getDate("data_submissao");
        if (dataSub != null) obra.setDataSubmissao(dataSub.toLocalDate());

        Date dataAval = rs.getDate("data_avaliacao");
        if (dataAval != null) obra.setDataAvaliacao(dataAval.toLocalDate());

        obra.setArquivo(rs.getString("arquivo"));
        obra.setFeedback(rs.getString("feedback"));

        long avaliadorId = rs.getLong("avaliador_id");
        if (!rs.wasNull()) {
            Avaliador avaliador = avaliadorDAO.buscarPorId(avaliadorId);
            obra.setAvaliador(avaliador);
        }

        return obra;
    }
}