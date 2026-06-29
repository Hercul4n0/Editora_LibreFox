package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.DAO.GerenteDAO;
import br.edu.ufersa.LibreFox.Model.DAO.NotificacaoDAO;
import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Gerente;
import br.edu.ufersa.LibreFox.Model.entities.Notificacao;
import br.edu.ufersa.LibreFox.Model.entities.Obra;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class NotificacaoObserver implements ObraEventListener {

    @Override
    public void aoSubmeter(Connection conn, Obra obra) throws SQLException {
        String mensagem = "Nova obra \"" + obra.getTitulo() + "\" enviada por "
                + obra.getAutor().getNome() + " precisa de avaliador.";

        NotificacaoDAO notificacaoDAO = new NotificacaoDAO(conn);
        List<Gerente> gerentes = new GerenteDAO(conn).listar();
        for (Gerente gerente : gerentes) {
            notificacaoDAO.inserir(new Notificacao(gerente.getId(), mensagem));
        }
    }

    @Override
    public void aoDesignarAvaliador(Connection conn, Obra obra, Avaliador avaliador) throws SQLException {
        String mensagem = "Você foi designado para avaliar a obra \"" + obra.getTitulo() + "\".";
        new NotificacaoDAO(conn).inserir(new Notificacao(avaliador.getId(), mensagem));
    }

    @Override
    public void aoAvaliar(Connection conn, Obra obra, short novoStatus) throws SQLException {
        String resultado = novoStatus == ObraService.ACEITA ? "aceita" : "rejeitada";
        String mensagem = "Sua obra \"" + obra.getTitulo() + "\" foi " + resultado + ".";
        new NotificacaoDAO(conn).inserir(new Notificacao(obra.getAutor().getId(), mensagem));
    }
}
