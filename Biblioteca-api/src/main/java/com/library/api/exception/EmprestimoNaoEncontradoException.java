package com.library.api.exception;

public class EmprestimoNaoEncontradoException extends RuntimeException{
    public EmprestimoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
