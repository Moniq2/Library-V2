package br.com.biblioteca.exception;

public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem){ super(mensagem); }
}
