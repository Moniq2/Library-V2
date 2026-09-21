package br.com.biblioteca.exception;

public class FalhaNoProcessamentoDeRespostaException extends RuntimeException{
    public FalhaNoProcessamentoDeRespostaException(String mensagem){
        super(mensagem);
    }
}
