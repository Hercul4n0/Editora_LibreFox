package br.edu.ufersa.LibreFox.Model.entities;

public class Sessao {

    private final Usuario usuario;
    private final Perfil perfilAtivo;

    public Sessao(Usuario usuario, Perfil perfilAtivo) {
        if (usuario == null) {
            throw new IllegalArgumentException("Usuário não pode ser nulo.");
        }
        if (!usuario.temPerfil(perfilAtivo)) {
            throw new IllegalArgumentException(
                    "Usuário não possui o perfil: " + perfilAtivo);
        }
        this.usuario = usuario;
        this.perfilAtivo = perfilAtivo;
    }

    // -------------------------------------------------------------------------
    // VERIFICAÇÕES DE PERMISSÃO
    // -------------------------------------------------------------------------

    public boolean podeGerenciar() {
        return perfilAtivo == Perfil.GERENTE;
    }

    public boolean podeAvaliar() {
        return perfilAtivo == Perfil.AVALIADOR;
    }

    public boolean podeEnviarObra() {
        return perfilAtivo == Perfil.AUTOR;
    }

    // -------------------------------------------------------------------------
    // GETTERS
    // -------------------------------------------------------------------------

    public Usuario getUsuario() {
        return usuario;
    }

    public Perfil getPerfilAtivo() {
        return perfilAtivo;
    }

    public long getUsuarioId() {
        return usuario.getId();
    }

    public String getNomeUsuario() {
        return usuario.getNome();
    }
}
