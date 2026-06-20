package br.edu.ufersa.LibreFox.editora.entities;

import java.util.EnumSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class Avaliador extends Usuario {

    private List<Obra> obrasParaAvaliar = new ArrayList<>();

    public Avaliador(String nome, String cpf, Endereco endereco,
                     String login, String senha) {
        super(nome, cpf, endereco, login, senha, EnumSet.of(Perfil.AVALIADOR));
    }

    // Construtor para usuário que também é autor
    public Avaliador(String nome, String cpf, Endereco endereco,
                     String login, String senha, Set<Perfil> perfis) {
        super(nome, cpf, endereco, login, senha, perfis);
    }

    public List<Obra> getObrasParaAvaliar() {
        return obrasParaAvaliar;
    }
}
