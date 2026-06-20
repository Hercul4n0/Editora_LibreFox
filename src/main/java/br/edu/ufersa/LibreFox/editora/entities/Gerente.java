package br.edu.ufersa.LibreFox.editora.entities;

import java.util.EnumSet;
import java.util.Set;

public class Gerente extends Usuario {

    public Gerente(String nome, String cpf, Endereco endereco,
                   String login, String senha) {
        super(nome, cpf, endereco, login, senha, EnumSet.of(Perfil.GERENTE));
    }

    // ERRO CORRIGIDO 8: faltava um construtor multi-perfil, igual ao de Autor e
    // Avaliador. Sem ele, era impossível um Gerente também acumular o perfil
    // AUTOR ou AVALIADOR, e o br.edu.ufersa.LibreFox.editora.DAO.GerenteDAO não tinha como reconstruir esse estado
    // vindo do banco.
    public Gerente(String nome, String cpf, Endereco endereco,
                   String login, String senha, Set<Perfil> perfis) {
        super(nome, cpf, endereco, login, senha, perfis);
    }
}
