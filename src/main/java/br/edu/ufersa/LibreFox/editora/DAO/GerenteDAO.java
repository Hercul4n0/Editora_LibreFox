package br.edu.ufersa.LibreFox.editora.DAO;
import br.edu.ufersa.LibreFox.editora.entities.Gerente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class GerenteDAO implements BaseDAO<Gerente> {
    private static final String insert_sql = "INSERT INTO usuario (nome, cpf, endereco) VALUES (?, ?, ?)";

    private Connection conexao;

    public GerenteDAO(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public Gerente inserir(Gerente objeto) {
        return null;
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
