package br.edu.ufersa.LibreFox.util;

import br.edu.ufersa.LibreFox.Model.DAO.AutorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.AvaliadorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.GerenteDAO;
import br.edu.ufersa.LibreFox.Model.entities.Perfil;
import br.edu.ufersa.LibreFox.Model.entities.Usuario;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Busca um usuário pelo login, no subtipo (Autor/Avaliador/Gerente) que
 * corresponde a um perfil específico.
 *
 * Cada DAO de perfil já carrega o conjunto completo de perfis do usuário
 * (não só o perfil usado no filtro da consulta), então o objeto retornado
 * aqui reflete corretamente todos os perfis da conta — só o tipo concreto
 * Java do objeto é que muda de acordo com o perfil pedido, e os dashboards
 * dependem desse tipo certo para fazer cast de {@code sessao.getUsuario()}.
 */
public final class UsuarioLookup {

    private UsuarioLookup() {}

    public static Usuario buscarPorLoginEPerfil(Connection conn, String login, Perfil perfil) throws SQLException {
        return switch (perfil) {
            case AUTOR -> new AutorDAO(conn).buscarPorLogin(login);
            case AVALIADOR -> new AvaliadorDAO(conn).buscarPorLogin(login);
            case GERENTE -> new GerenteDAO(conn).buscarPorLogin(login);
        };
    }
}
