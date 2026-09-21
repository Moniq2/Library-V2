package br.com.biblioteca.exception;

public class CredenciaisInvalidasException extends RuntimeException{
    public CredenciaisInvalidasException(String mensagem){
        super(mensagem);
    }
}
