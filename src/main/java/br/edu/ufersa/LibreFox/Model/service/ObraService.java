package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.DAO.ObraDAO;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Obra;
import br.edu.ufersa.LibreFox.Model.entities.Sessao;
import br.edu.ufersa.LibreFox.Model.exceptions.AcessoNegadoException;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

/**
 * Casos de uso ligados a Obras — o coração do fluxo da editora.
 *
 * Concentra a orquestração e a autorização do ciclo de vida da obra:
 * <ul>
 *   <li>submissão pelo autor;</li>
 *   <li>designação de avaliador pelo gerente;</li>
 *   <li>avaliação pelo avaliador designado;</li>
 *   <li>visibilidade restrita (autor só vê as suas, avaliador só as designadas
 *       a ele, gerente vê o catálogo completo).</li>
 * </ul>
 *
 * O {@link ObraDAO} cuida apenas da persistência; toda regra de negócio mora aqui.
 * Violações de autorização lançam {@link AcessoNegadoException}, uma exceção
 * de domínio própria do LibreFox.
 */
public class ObraService {

    // Status da obra (convenção do mini mundo — ainda não é enum).
    public static final short EM_AVALIACAO = 0;
    public static final short ACEITA       = 1;
    public static final short REJEITADA    = 2;

    private final ObraDAO obraDAO;

    public ObraService(Connection connection) {
        this.obraDAO = new ObraDAO(connection);
    }

    // -------------------------------------------------------------------------
    // SUBMISSÃO — autor envia a própria obra
    // -------------------------------------------------------------------------

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

        // A tabela "obra" usa chave primária String (não AUTO_INCREMENT); o
        // identificador é gerado aqui caso o autor ainda não tenha definido um.
        // Segue a convenção "OBRA001" do mini mundo, mantendo o id curto o
        // suficiente para caber na coluna (um UUID de 36 chars estourava o limite).
        if (obra.getId() == null || obra.getId().isBlank()) {
            obra.setId(gerarProximoId());
        }

        // Toda obra entra no fluxo "em avaliação", recém-submetida.
        obra.setStatus(EM_AVALIACAO);
        obra.setDataSubmissao(LocalDate.now());

        obraDAO.inserir(obra);
        obra.getAutor().getObrasEnviadas().add(obra);
        return obra;
    }

    /**
     * Gera o próximo id de obra no padrão "OBRA000", baseado no maior sufixo
     * numérico já existente. Mantém o id curto (cabe na coluna) e legível.
     */
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

    // -------------------------------------------------------------------------
    // ALTERAÇÃO / EXCLUSÃO — autor dono (enquanto em avaliação) ou gerente
    // -------------------------------------------------------------------------

    public void alterar(Obra obra, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirDonoOuGerente(obra, sessao, "alterar");
        obraDAO.atualizar(obra);
    }

    public void excluir(Obra obra, Sessao sessao) throws SQLException, AcessoNegadoException {
        exigirDonoOuGerente(obra, sessao, "excluir");
        obraDAO.deletar(obra);
    }

    // -------------------------------------------------------------------------
    // DESIGNAÇÃO DE AVALIADOR — somente o gerente
    // -------------------------------------------------------------------------

    public void designarAvaliador(Obra obra, Avaliador avaliador, Sessao sessao)
            throws SQLException, AcessoNegadoException {
        if (obra == null || avaliador == null) {
            throw new IllegalArgumentException("Obra e avaliador são obrigatórios.");
        }
        if (sessao == null || !sessao.podeGerenciar()) {
            throw new AcessoNegadoException("Apenas o gerente pode designar avaliadores.");
        }
        if (obra.getStatus() != EM_AVALIACAO) {
            throw new IllegalStateException(
                    "Só é possível designar avaliador para obras em avaliação.");
        }

        obraDAO.definirAvaliador(obra, avaliador);
        obra.setAvaliador(avaliador);
    }

    // -------------------------------------------------------------------------
    // AVALIAÇÃO — somente o avaliador designado
    // -------------------------------------------------------------------------

    public void avaliar(Obra obra, short novoStatus, Sessao sessao)
            throws SQLException, AcessoNegadoException {
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
            throw new IllegalArgumentException(
                    "Avaliação deve resultar em ACEITA (1) ou REJEITADA (2).");
        }
        if (obra.getStatus() != EM_AVALIACAO) {
            throw new IllegalStateException("Esta obra já foi avaliada.");
        }

        LocalDate hoje = LocalDate.now();
        obraDAO.registrarAvaliacao(obra, novoStatus, hoje);
        obra.setStatus(novoStatus);
        obra.setDataAvaliacao(hoje);
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