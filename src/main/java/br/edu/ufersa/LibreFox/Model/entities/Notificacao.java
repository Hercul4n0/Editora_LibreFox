package br.edu.ufersa.LibreFox.Model.entities;

import java.time.LocalDateTime;

/**
 * Notificação dirigida a um usuário, gerada a partir de um evento do ciclo
 * de vida de uma obra (submissão, designação de avaliador, avaliação).
 *
 * É o efeito visível, na próxima vez que o destinatário entrar no sistema,
 * dos observadores registrados em {@code ObraService} (ver
 * {@code Model.service.ObraEventListener} e {@code NotificacaoObserver}).
 */
public class Notificacao {

    private long id;
    private final long usuarioId;
    private final String mensagem;
    private boolean lida;
    private LocalDateTime dataCriacao;

    public Notificacao(long usuarioId, String mensagem) {
        this.usuarioId = usuarioId;
        this.mensagem = mensagem;
        this.lida = false;
        this.dataCriacao = LocalDateTime.now();
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUsuarioId() { return usuarioId; }

    public String getMensagem() { return mensagem; }

    public boolean isLida() { return lida; }
    public void setLida(boolean lida) { this.lida = lida; }

    public LocalDateTime getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(LocalDateTime dataCriacao) { this.dataCriacao = dataCriacao; }
}
