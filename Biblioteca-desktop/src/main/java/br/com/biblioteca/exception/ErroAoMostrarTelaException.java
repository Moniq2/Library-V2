package br.com.biblioteca.exception;

public class ErroAoMostrarTelaException extends  RuntimeException {
    public ErroAoMostrarTelaException(String mensagem) {
        super(mensagem);
    }
}
