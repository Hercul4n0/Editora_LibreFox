package br.edu.ufersa.LibreFox.editora.DAO;
import br.edu.ufersa.LibreFox.editora.entities.Gerente;

import java.sql.*;
import java.util.ArrayList;

public class GerenteDAO implements BaseDAO<Gerente> {
    private static final String insert_sql = "INSERT INTO usuario (nome, cpf, endereco, login, senha) VALUES (?, ?, ?, ?, ?)";
    private static final String delete_sql = "DELETE FROM usuario  WHERE id = ?";

    private Connection conexao;

    public GerenteDAO(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public Gerente inserir(Gerente objeto) {
        try{
            PreparedStatement pstmt = conexao.prepareStatement(insert_sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, objeto.getNome());
            pstmt.setString(2, objeto.getCpf());
            pstmt.setString(3, objeto.getEndereco());
            pstmt.setString(4, objeto.getLogin());
            pstmt.setString(5, objeto.getSenha());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                objeto.setId(rs.getLong(1));
            }
        }catch(SQLException e){e.printStackTrace();}
        return objeto;
    }

    @Override
    public void deletar(Gerente objeto) {

    }

    @Override
    public void atualizar(Gerente objeto) {

    }

    @Override
    public Gerente buscar(String parametro) {
        return null;
    }

    @Override
    public ArrayList<Gerente> listar() {
        return null;
    }
}
