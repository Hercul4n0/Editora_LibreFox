package br.edu.ufersa.LibreFox.editora.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL  = "jdbc:mysql://localhost/librefox_editora?useSSL=false&serverTimezone=America/Recife&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASS = "mysql";

    private static Connection connection = null;

    // Impede instanciação acidental
    private Conexao() {}

    /**
     * Retorna a conexão ativa, criando uma nova se necessário.
     * Verifica se a conexão existente ainda está aberta e válida
     * antes de reutilizá-la, evitando erros silenciosos após
     * timeouts do MySQL.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed() || !connection.isValid(2)) {
            connection = DriverManager.getConnection(URL, USER, PASS);
        }
        return connection;
    }

    /**
     * Fecha a conexão com o banco e a descarta do Singleton,
     * permitindo que uma nova seja criada na próxima chamada.
     */
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
