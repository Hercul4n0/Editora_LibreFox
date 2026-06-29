package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.Model.exceptions.AcessoNegadoException;
import br.edu.ufersa.LibreFox.Model.exceptions.OperacaoInvalidaException;

import java.sql.SQLException;
import java.util.ArrayList;

public interface IObraService {

    Obra submeter(Obra obra, Sessao sessao) throws SQLException, AcessoNegadoException;

    void alterar(Obra obra, Sessao sessao) throws SQLException, AcessoNegadoException;

    void excluir(Obra obra, Sessao sessao) throws SQLException, AcessoNegadoException;

    void designarAvaliador(Obra obra, Avaliador avaliador, Sessao sessao)
            throws SQLException, AcessoNegadoException, OperacaoInvalidaException;

    void avaliar(Obra obra, short novoStatus, Sessao sessao)
            throws SQLException, AcessoNegadoException, OperacaoInvalidaException;

    void avaliar(Obra obra, short novoStatus, String feedback, Sessao sessao)
            throws SQLException, AcessoNegadoException, OperacaoInvalidaException;

    ArrayList<Obra> listarObrasDoAutor(Sessao sessao) throws SQLException, AcessoNegadoException;

    ArrayList<Obra> listarObrasDoAvaliador(Sessao sessao) throws SQLException, AcessoNegadoException;

    ArrayList<Obra> listar(Sessao sessao) throws SQLException, AcessoNegadoException;

    ArrayList<Obra> buscarPorTitulo(String titulo, Sessao sessao) throws SQLException, AcessoNegadoException;

    ArrayList<Obra> buscarPorAutor(long autorId, Sessao sessao) throws SQLException, AcessoNegadoException;

    ArrayList<Obra> buscarPorStatus(short status, Sessao sessao) throws SQLException, AcessoNegadoException;

    ArrayList<Obra> buscarPorAno(short ano, Sessao sessao) throws SQLException, AcessoNegadoException;

    Obra buscarPorId(String id) throws SQLException;
}