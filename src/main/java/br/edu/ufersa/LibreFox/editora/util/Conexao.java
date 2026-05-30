package br.edu.ufersa.LibreFox.editora.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {
    private final static String URL = "jdbc:mysql://localhost/";
    private final static String USER = "root";
    private final static String PASS = "mysql";
    private static Connection connection = null;

    public static Connection getConnection(){
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASS);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return connection;
    }

    public static void closeConnection(){
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ex) {
                ex.printStackTrace();}
        }
    }
}
