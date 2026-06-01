package br.edu.ufersa.LibreFox.editora.DAO;

import br.edu.ufersa.LibreFox.editora.entities.Endereco;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


public class EnderecoDAO {
    private static final String insert_sql = "INSERT INTO endereco (numero, bairro, logradouro, cidade, uf) VALUES (?, ?, ?, ?, ?)";
    private static final String select_by_id_sql = "SELECT * FROM endereco WHERE id = ?";

    private Connection conexao;

    public EnderecoDAO(Connection conexao) {
        this.conexao = conexao;
    }

    public Endereco inserir(Endereco endereco) {
        try {
            PreparedStatement pstmt = conexao.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, endereco.getNumero());
            pstmt.setString(2, endereco.getBairro());
            pstmt.setString(3, endereco.getLogradouro());
            pstmt.setString(4, endereco.getCidade());
            pstmt.setString(5, endereco.getUf());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                endereco.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return endereco;
    }

    public Endereco buscarPorId(long id) {
        try {
            PreparedStatement pstmt = conexao.prepareStatement(select_by_id_sql);
            pstmt.setLong(1, id);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Endereco endereco = new Endereco(
                    rs.getString("numero"),
                    rs.getString("bairro"),
                    rs.getString("logradouro"),
                    rs.getString("cidade"),
                    rs.getString("uf")
                );

                endereco.setId(rs.getLong(  "id"));

                return endereco;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }
}
