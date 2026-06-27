package br.edu.ufersa.LibreFox.Model.exceptions;

/**
 * Exceção de domínio lançada quando um usuário tenta executar uma operação
 * para a qual seu perfil ativo (Autor, Avaliador ou Gerente) não tem
 * autorização — por exemplo, um Autor tentando designar um avaliador, ou
 * um Avaliador tentando avaliar uma obra que não lhe foi designada.
 *
 * É uma checked exception: o compilador obriga quem chama os métodos de
 * serviço sensíveis a tratar explicitamente o caso de acesso negado, em vez
 * de deixá-lo se propagar silenciosamente como uma RuntimeException.
 *
 * Substitui o uso de java.lang.SecurityException nas camadas de serviço,
 * tornando explícito que essa é uma regra de negócio do sistema LibreFox
 * (e não uma falha de segurança da JVM/SecurityManager).
 */
public class AcessoNegadoException extends Exception {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }

    public AcessoNegadoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
