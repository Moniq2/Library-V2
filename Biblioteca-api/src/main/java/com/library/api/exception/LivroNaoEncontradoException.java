package com.library.api.exception;

public class LivroNaoEncontradoException extends RuntimeException{
    public LivroNaoEncontradoException(String mensagem){
        super(mensagem);
    }
}
