package br.com.biblioteca.exception;

public class SemPermissaoParaAcessarRecursoException extends RuntimeException {
    public SemPermissaoParaAcessarRecursoException (String mensagem){
        super(mensagem);
    }
}
