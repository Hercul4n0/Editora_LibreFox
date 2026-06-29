package br.edu.ufersa.LibreFox.Model.entities;

import java.util.EnumSet;
import java.util.Set;

public class Gerente extends Usuario {

    public Gerente(String nome, String cpf, Endereco endereco,
                   String login, String senha) {
        super(nome, cpf, endereco, login, senha, EnumSet.of(Perfil.GERENTE));
    }

    public Gerente(String nome, String cpf, Endereco endereco,
                   String login, String senha, Set<Perfil> perfis) {
        super(nome, cpf, endereco, login, senha, perfis);
    }
}
