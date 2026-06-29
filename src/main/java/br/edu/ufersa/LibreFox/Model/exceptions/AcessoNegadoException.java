package br.edu.ufersa.LibreFox.Model.exceptions;

public class AcessoNegadoException extends Exception {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }

    public AcessoNegadoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

// Nega acesso caso o usuario tente fazer algo q só outro tipo faz