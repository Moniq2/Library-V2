package br.com.biblioteca.exception;

public class LivroNaoEncontradoException extends RuntimeException{
    public LivroNaoEncontradoException(String mensagem){
        super(mensagem);
    }
}
