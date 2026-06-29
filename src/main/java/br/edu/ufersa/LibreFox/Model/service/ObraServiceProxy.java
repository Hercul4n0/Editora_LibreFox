package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.Model.exceptions.AcessoNegadoException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Protection Proxy para {@link IObraService}.
 *
 * Intercepta toda chamada antes de delegar ao {@link ObraService} real,
 * aplicando um gate grosso por perfil de sessão (gerente / autor / avaliador).
 * Não substitui as checagens internas de {@code ObraService} (posse da obra,
 * estado da obra, avaliador designado) — essas continuam ali como defesa em
 * profundidade, pois dependem de regras de negócio específicas da obra que
 * um proxy genérico de perfil não tem como replicar com segurança.
 */
public class ObraServiceProxy implements IObraService {

    private final ObraService obraServiceReal;

    public ObraServiceProxy(Connection connection) {
        this.obraServiceReal = new ObraService(connection);
    }

    @Override
    public Obra submeter(Obra obra, Sessao sessao) throws SQLException, AcessoNegadoException {
        if (obra == null) {
            throw new IllegalArgumentException("Obra não pode ser nula.");
        }
        if (sessao == null || !sessao.podeEnviarObra()) {
            throw new AcessoNegadoException("Apenas autores podem submeter obras.");
        }
        if (obra.getAutor() == null || obra.getAutor().getId() != sessao.getUsuarioId()) {
            throw new AcessoNegadoException("Um autor só pode submeter obras em seu próprio nome.");
        }
        return obraServiceReal.submeter(obra, sessao);
    }

    @Override
    public void alterar(Obra obra, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirDonoOuGerente(obra, sessao, "alterar");
        obraServiceReal.alterar(obra, sessao);
    }

    @Override
    public void excluir(Obra obra, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirDonoOuGerente(obra, sessao, "excluir");
        obraServiceReal.excluir(obra, sessao);
    }

    @Override
    public void designarAvaliador(Obra obra, Avaliador avaliador, Sessao sessao)
            throws SQLException, AcessoNegadoException {
        if (obra == null || avaliador == null) {
            throw new IllegalArgumentException("Obra e avaliador são obrigatórios.");
        }
        if (sessao == null || !sessao.podeGerenciar()) {
            throw new AcessoNegadoException("Apenas o gerente pode designar avaliadores.");
        }
        if (obra.getAutor() != null && obra.getAutor().getId() == avaliador.getId()) {
            throw new IllegalArgumentException(
                    "O autor de uma obra não pode ser designado avaliador da própria obra.");
        }
        obraServiceReal.designarAvaliador(obra, avaliador, sessao);
    }

    @Override
    public void avaliar(Obra obra, short novoStatus, Sessao sessao)
            throws SQLException, AcessoNegadoException {
        avaliar(obra, novoStatus, null, sessao);
    }

    @Override
    public void avaliar(Obra obra, short novoStatus, String feedback, Sessao sessao)
            throws SQLException, AcessoNegadoException {
        if (obra == null) {
            throw new IllegalArgumentException("Obra não pode ser nula.");
        }
        if (sessao == null || !sessao.podeAvaliar()) {
            throw new AcessoNegadoException("Apenas avaliadores podem avaliar obras.");
        }
        if (obra.getAvaliador() == null || obra.getAvaliador().getId() != sessao.getUsuarioId()) {
            throw new AcessoNegadoException("Você não é o avaliador designado para esta obra.");
        }
        obraServiceReal.avaliar(obra, novoStatus, feedback, sessao);
    }

    @Override
    public ArrayList<Obra> listarObrasDoAutor(Sessao sessao) throws SQLException, AcessoNegadoException {
        if (sessao == null || !sessao.podeEnviarObra()) {
            throw new AcessoNegadoException("Acesso negado: perfil ativo não é autor.");
        }
        return obraServiceReal.listarObrasDoAutor(sessao);
    }

    @Override
    public ArrayList<Obra> listarObrasDoAvaliador(Sessao sessao) throws SQLException, AcessoNegadoException {
        if (sessao == null || !sessao.podeAvaliar()) {
            throw new AcessoNegadoException("Acesso negado: perfil ativo não é avaliador.");
        }
        return obraServiceReal.listarObrasDoAvaliador(sessao);
    }

    @Override
    public ArrayList<Obra> listar(Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirGerente(sessao);
        return obraServiceReal.listar(sessao);
    }

    @Override
    public ArrayList<Obra> buscarPorTitulo(String titulo, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirGerente(sessao);
        return obraServiceReal.buscarPorTitulo(titulo, sessao);
    }

    @Override
    public ArrayList<Obra> buscarPorAutor(long autorId, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirGerente(sessao);
        return obraServiceReal.buscarPorAutor(autorId, sessao);
    }

    @Override
    public ArrayList<Obra> buscarPorStatus(short status, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirGerente(sessao);
        return obraServiceReal.buscarPorStatus(status, sessao);
    }

    @Override
    public ArrayList<Obra> buscarPorAno(short ano, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirGerente(sessao);
        return obraServiceReal.buscarPorAno(ano, sessao);
    }

    @Override
    public Obra buscarPorId(String id) throws SQLException {
        // Lookup utilitário sem restrição de perfil — repassa direto, sem checagem.
        return obraServiceReal.buscarPorId(id);
    }

    // -------------------------------------------------------------------------
    // AUTORIZAÇÃO — gate grosso por perfil (espelha os helpers de ObraService)
    // -------------------------------------------------------------------------

    private void exigirGerente(Sessao sessao) throws AcessoNegadoException {
        if (sessao == null || !sessao.podeGerenciar()) {
            throw new AcessoNegadoException("Apenas o gerente pode consultar o catálogo completo de obras.");
        }
    }

    private void exigirDonoOuGerente(Obra obra, Sessao sessao, String acao) throws AcessoNegadoException {
        if (obra == null) {
            throw new IllegalArgumentException("Obra não pode ser nula.");
        }
        if (sessao != null && sessao.podeGerenciar()) {
            return;
        }
        boolean autorDono = sessao != null && sessao.podeEnviarObra()
                && obra.getAutor() != null
                && obra.getAutor().getId() == sessao.getUsuarioId();
        if (!autorDono) {
            throw new AcessoNegadoException(
                    "Apenas o gerente ou o autor dono da obra pode " + acao + "-la.");
        }
    }
}