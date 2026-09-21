package br.com.biblioteca.exception;

public class EmprestimoNaoEncontradoException extends RuntimeException{
    public EmprestimoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
