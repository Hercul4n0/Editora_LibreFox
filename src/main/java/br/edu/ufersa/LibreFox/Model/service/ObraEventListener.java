package br.edu.ufersa.LibreFox.Model.service;

import br.edu.ufersa.LibreFox.Model.entities.Avaliador;
import br.edu.ufersa.LibreFox.Model.entities.Obra;

import java.sql.Connection;
import java.sql.SQLException;

public interface ObraEventListener {

    default void aoSubmeter(Connection conn, Obra obra) throws SQLException {}

    default void aoDesignarAvaliador(Connection conn, Obra obra, Avaliador avaliador) throws SQLException {}

    default void aoAvaliar(Connection conn, Obra obra, short novoStatus) throws SQLException {}
}
