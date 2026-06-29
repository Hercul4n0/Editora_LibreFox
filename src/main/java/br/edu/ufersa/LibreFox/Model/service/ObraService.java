package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.DAO.ObraDAO;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.Model.exceptions.AcessoNegadoException;
import br.edu.ufersa.LibreFox.Model.exceptions.OperacaoInvalidaException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ObraService implements IObraService {

    public static final short EM_AVALIACAO = 0;
    public static final short ACEITA       = 1;
    public static final short REJEITADA    = 2;

    private static final List<ObraEventListener> LISTENERS = new ArrayList<>();

    public static void registrarListener(ObraEventListener listener) {
        LISTENERS.add(listener);
    }

    private final Connection connection;
    private final ObraDAO obraDAO;

    public ObraService(Connection connection) {
        this.connection = connection;
        this.obraDAO = new ObraDAO(connection);
    }


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

        if (obra.getId() == null || obra.getId().isBlank()) {
            obra.setId(gerarProximoId());
        }

        obra.setStatus(EM_AVALIACAO);
        obra.setDataSubmissao(LocalDate.now());

        obraDAO.inserir(obra);
        obra.getAutor().getObrasEnviadas().add(obra);
        notificarSubmissao(obra);
        return obra;
    }

    private String gerarProximoId() throws SQLException {
        int maior = 0;
        for (Obra existente : obraDAO.listar()) {
            String id = existente.getId();
            if (id != null && id.startsWith("OBRA")) {
                try {
                    maior = Math.max(maior, Integer.parseInt(id.substring(4)));
                } catch (NumberFormatException ignored) {
                    // ids fora do padrão são apenas ignorados na numeração
                }
            }
        }
        return String.format("OBRA%03d", maior + 1);
    }

    public void alterar(Obra obra, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirDonoOuGerente(obra, sessao, "alterar");
        obraDAO.atualizar(obra);
    }

    public void excluir(Obra obra, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirDonoOuGerente(obra, sessao, "excluir");
        obraDAO.deletar(obra);
    }


    // Designação de avaliador

    public void designarAvaliador(Obra obra, Avaliador avaliador, Sessao sessao)
            throws SQLException, AcessoNegadoException, OperacaoInvalidaException {
        if (obra == null || avaliador == null) {
            throw new IllegalArgumentException("Obra e avaliador são obrigatórios.");
        }
        if (sessao == null || !sessao.podeGerenciar()) {
            throw new AcessoNegadoException("Apenas o gerente pode designar avaliadores.");
        }
        if (obra.getStatus() != EM_AVALIACAO) {
            throw new OperacaoInvalidaException(
                    "Só é possível designar avaliador para obras em avaliação.");
        }
        // Regra de negócio: ninguém pode avaliar a própria obra, mesmo que a
        // mesma pessoa acumule os perfis Autor e Avaliador.
        if (obra.getAutor() != null && obra.getAutor().getId() == avaliador.getId()) {
            throw new OperacaoInvalidaException(
                    "O autor de uma obra não pode ser designado avaliador da própria obra.");
        }

        obraDAO.definirAvaliador(obra, avaliador);
        obra.setAvaliador(avaliador);
        notificarDesignacao(obra, avaliador);
    }

    // -------------------------------------------------------------------------
    // AVALIAÇÃO — somente o avaliador designado
    // -------------------------------------------------------------------------

    /** Sobrecarga sem feedback, mantida por compatibilidade. */
    public void avaliar(Obra obra, short novoStatus, Sessao sessao)
            throws SQLException, AcessoNegadoException, OperacaoInvalidaException {
        avaliar(obra, novoStatus, null, sessao);
    }

    public void avaliar(Obra obra, short novoStatus, String feedback, Sessao sessao)
            throws SQLException, AcessoNegadoException, OperacaoInvalidaException {
        if (obra == null) {
            throw new IllegalArgumentException("Obra não pode ser nula.");
        }
        if (sessao == null || !sessao.podeAvaliar()) {
            throw new AcessoNegadoException("Apenas avaliadores podem avaliar obras.");
        }
        if (obra.getAvaliador() == null ||
                obra.getAvaliador().getId() != sessao.getUsuarioId()) {
            throw new AcessoNegadoException("Você não é o avaliador designado para esta obra.");
        }
        if (novoStatus != ACEITA && novoStatus != REJEITADA) {
            throw new OperacaoInvalidaException(
                    "Avaliação deve resultar em ACEITA (1) ou REJEITADA (2).");
        }
        if (obra.getStatus() != EM_AVALIACAO) {
            throw new OperacaoInvalidaException("Esta obra já foi avaliada.");
        }

        LocalDate hoje = LocalDate.now();
        obraDAO.registrarAvaliacao(obra, novoStatus, hoje, feedback);
        obra.setStatus(novoStatus);
        obra.setDataAvaliacao(hoje);
        obra.setFeedback(feedback);
        notificarAvaliacao(obra, novoStatus);
    }

    // -------------------------------------------------------------------------
    // VISIBILIDADE RESTRITA (regra de negócio 7)
    // -------------------------------------------------------------------------

    /** Autor só visualiza as próprias obras. */
    public ArrayList<Obra> listarObrasDoAutor(Sessao sessao) throws SQLException, AcessoNegadoException {
        if (sessao == null || !sessao.podeEnviarObra()) {
            throw new AcessoNegadoException("Acesso negado: perfil ativo não é autor.");
        }
        return obraDAO.buscarPorAutor(sessao.getUsuarioId());
    }

    /** Avaliador só visualiza as obras designadas a ele. */
    public ArrayList<Obra> listarObrasDoAvaliador(Sessao sessao) throws SQLException, AcessoNegadoException {
        if (sessao == null || !sessao.podeAvaliar()) {
            throw new AcessoNegadoException("Acesso negado: perfil ativo não é avaliador.");
        }
        return obraDAO.buscarPorAvaliador(sessao.getUsuarioId());
    }

    // -------------------------------------------------------------------------
    // CATÁLOGO E BUSCAS GERAIS — somente o gerente (visão completa)
    // -------------------------------------------------------------------------

    public ArrayList<Obra> listar(Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirGerente(sessao);
        return obraDAO.listar();
    }

    public ArrayList<Obra> buscarPorTitulo(String titulo, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirGerente(sessao);
        return obraDAO.buscarPorTitulo(titulo);
    }

    public ArrayList<Obra> buscarPorAutor(long autorId, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirGerente(sessao);
        return obraDAO.buscarPorAutor(autorId);
    }

    public ArrayList<Obra> buscarPorStatus(short status, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirGerente(sessao);
        return obraDAO.buscarPorStatus(status);
    }

    public ArrayList<Obra> buscarPorAno(short ano, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirGerente(sessao);
        return obraDAO.buscarPorAno(ano);
    }

    /**
     * Carrega uma obra pelo id. Lookup utilitário (sem restrição de perfil) —
     * a visibilidade efetiva é garantida pelas operações que consomem a obra.
     */
    public Obra buscarPorId(String id) throws SQLException {
        return obraDAO.buscarPorId(id);
    }

    // -------------------------------------------------------------------------
    // OBSERVER — dispara os listeners registrados após cada transição já
    // persistida com sucesso. Uma notificação é um efeito best-effort: se um
    // listener falhar, o erro é só registrado (e os demais listeners ainda
    // são chamados) — nunca desfaz a operação de negócio, que já foi salva.
    // -------------------------------------------------------------------------

    private void notificarSubmissao(Obra obra) {
        for (ObraEventListener listener : LISTENERS) {
            try {
                listener.aoSubmeter(connection, obra);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void notificarDesignacao(Obra obra, Avaliador avaliador) {
        for (ObraEventListener listener : LISTENERS) {
            try {
                listener.aoDesignarAvaliador(connection, obra, avaliador);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void notificarAvaliacao(Obra obra, short novoStatus) {
        for (ObraEventListener listener : LISTENERS) {
            try {
                listener.aoAvaliar(connection, obra, novoStatus);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // -------------------------------------------------------------------------
    // AUTORIZAÇÃO
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