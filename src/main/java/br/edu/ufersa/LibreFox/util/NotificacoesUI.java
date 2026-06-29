package br.edu.ufersa.LibreFox.util;

import br.edu.ufersa.LibreFox.Model.DAO.NotificacaoDAO;
import br.edu.ufersa.LibreFox.Model.entities.Notificacao;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Exibição das notificações geradas pelos observadores de ObraService
 * (ver Model.service.ObraEventListener / NotificacaoObserver) — usada pelos
 * três dashboards (Autor, Avaliador, Gerente) para evitar repetir a mesma
 * lógica de carregar/mostrar/marcar como lida em cada controller.
 */
public final class NotificacoesUI {

    private NotificacoesUI() {}

    /**
     * Busca as notificações não lidas do usuário e atualiza o botão: some se
     * não houver nenhuma, ou mostra a contagem e guarda a lista para exibir
     * ao clicar.
     */
    public static void atualizar(Button botao, long usuarioId) {
        try (Connection conn = Conexao.getConnection()) {
            List<Notificacao> naoLidas = new NotificacaoDAO(conn).listarNaoLidas(usuarioId);
            if (naoLidas.isEmpty()) {
                botao.setVisible(false);
                botao.setManaged(false);
                return;
            }
            botao.setText("🔔 " + naoLidas.size() + " notificação(ões)");
            botao.setVisible(true);
            botao.setManaged(true);
            botao.setUserData(naoLidas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mostra as notificações guardadas no botão (por {@link #atualizar}) e,
     * ao fechar o diálogo, marca todas como lidas e esconde o botão.
     */
    @SuppressWarnings("unchecked")
    public static void mostrarEMarcarLidas(Button botao, long usuarioId) {
        Object dados = botao.getUserData();
        if (!(dados instanceof List<?> lista) || lista.isEmpty()) return;
        List<Notificacao> notificacoes = (List<Notificacao>) lista;

        StringBuilder texto = new StringBuilder();
        for (Notificacao n : notificacoes) {
            texto.append("• ").append(n.getMensagem()).append("\n");
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notificações");
        alert.setHeaderText("Você tem " + notificacoes.size() + " notificação(ões)");
        alert.setContentText(texto.toString());
        alert.showAndWait();

        try (Connection conn = Conexao.getConnection()) {
            new NotificacaoDAO(conn).marcarTodasComoLidas(usuarioId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        botao.setVisible(false);
        botao.setManaged(false);
    }
}
