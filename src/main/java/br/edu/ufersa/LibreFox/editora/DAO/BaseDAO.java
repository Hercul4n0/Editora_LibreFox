package br.edu.ufersa.LibreFox.editora.DAO;
import java.util.ArrayList;

public interface BaseDAO <T>{
    public T inserir(T objeto);
    public void deletar(T objeto);
    public void atualizar(T objeto);
    public T buscar(String parametro);
    public ArrayList<T> listar();
}
