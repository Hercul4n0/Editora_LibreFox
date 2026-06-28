package br.edu.ufersa.LibreFox;

import br.edu.ufersa.LibreFox.util.Conexao;

public class Teste {
    public static void main(String[] args) {
        try {
            var conn = Conexao.getConnection();
            System.out.println("Conexão OK");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
