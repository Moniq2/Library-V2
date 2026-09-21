package br.com.biblioteca.exception;

public class FalhaNaDevolucaoException extends RuntimeException {
    public FalhaNaDevolucaoException(String mensagem) {
        super(mensagem);
    }
}