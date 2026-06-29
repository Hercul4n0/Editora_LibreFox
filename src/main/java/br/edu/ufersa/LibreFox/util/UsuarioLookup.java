package br.edu.ufersa.LibreFox.util;

import br.edu.ufersa.LibreFox.Model.DAO.AutorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.AvaliadorDAO;
import br.edu.ufersa.LibreFox.Model.DAO.GerenteDAO;
import br.edu.ufersa.LibreFox.Model.entities.Perfil;
import br.edu.ufersa.LibreFox.Model.entities.Usuario;

import java.sql.Connection;
import java.sql.SQLException;

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
