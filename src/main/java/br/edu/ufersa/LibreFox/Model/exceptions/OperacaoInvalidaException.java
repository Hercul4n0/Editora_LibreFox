package br.edu.ufersa.LibreFox.Model.exceptions;

public class OperacaoInvalidaException extends Exception {

    public OperacaoInvalidaException(String mensagem) {
        super(mensagem);
    }

    public OperacaoInvalidaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}

// Impede de realizar operações com dados inválidos.