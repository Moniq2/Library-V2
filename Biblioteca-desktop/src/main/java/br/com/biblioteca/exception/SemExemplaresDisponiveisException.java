package br.com.biblioteca.exception;

public class SemExemplaresDisponiveisException extends RuntimeException{
    public SemExemplaresDisponiveisException(String mensagem){
        super(mensagem);
    }
}
