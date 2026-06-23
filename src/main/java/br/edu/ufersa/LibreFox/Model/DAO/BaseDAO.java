package br.edu.ufersa.LibreFox.Model.DAO;

import java.sql.SQLException;
import java.util.ArrayList;

public interface BaseDAO<T> {
    T inserir(T objeto) throws SQLException;
    void deletar(T objeto) throws SQLException;
    void atualizar(T objeto) throws SQLException;
    ArrayList<T> listar() throws SQLException;
}
