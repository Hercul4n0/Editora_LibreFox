package br.edu.ufersa.LibreFox.Model.entities;

import java.util.EnumSet;
import java.util.Set;

public class Usuario {

    private long id;
    private String nome;
    private String cpf;
    private Endereco endereco;
    private String login;
    private String senha;
    private Set<Perfil> perfis = EnumSet.noneOf(Perfil.class);

    public Usuario(String nome, String cpf, Endereco endereco,
                   String login, String senha, Set<Perfil> perfis) {
        setNome(nome);
        setCpf(cpf);
        setEndereco(endereco);
        setLogin(login);
        setSenha(senha);
        setPerfis(perfis);
    }

    public Set<Perfil> getPerfis() {
        return perfis;
    }

    public void setPerfis(Set<Perfil> perfis) {
        if (perfis != null && !perfis.isEmpty()) {
            this.perfis = EnumSet.copyOf(perfis);
        }
    }

    public void adicionarPerfil(Perfil perfil) {
        this.perfis.add(perfil);
    }

    public void removerPerfil(Perfil perfil) {
        this.perfis.remove(perfil);
    }

    public boolean temPerfil(Perfil perfil) {
        return this.perfis.contains(perfil);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        if (nome != null) this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        if (cpf != null) this.cpf = cpf;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        if (endereco != null) this.endereco = endereco;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        if (login != null) this.login = login;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        if (senha != null) this.senha = senha;
    }
}
