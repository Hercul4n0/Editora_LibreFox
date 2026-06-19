package br.edu.ufersa.LibreFox.editora.entities;

import java.util.EnumSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class Autor extends Usuario {

    private List<Obra> obrasEnviadas = new ArrayList<>();

    public Autor(String nome, String cpf, Endereco endereco,
                 String login, String senha) {
        super(nome, cpf, endereco, login, senha, EnumSet.of(Perfil.AUTOR));
    }

    // Construtor para usuário que também é avaliador
    public Autor(String nome, String cpf, Endereco endereco,
                 String login, String senha, Set<Perfil> perfis) {
        super(nome, cpf, endereco, login, senha, perfis);
    }

    public List<Obra> getObrasEnviadas() {
        return obrasEnviadas;
    }
}
