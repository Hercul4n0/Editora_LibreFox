package br.edu.ufersa.LibreFox.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL  = "jdbc:mysql://localhost:3306/librefox_editora";
    private static final String USER = "root";
    private static final String PASS = "root";

    private static Connection connection = null;

    // Impede instanciação acidental
    private Conexao() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            connection = DriverManager.getConnection(URL, USER, PASS);
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            } finally {
                connection = null;
            }
        }
    }
}
