package br.com.biblioteca.exception;

public class ConflitoDeCredenciaisException extends RuntimeException {
 public ConflitoDeCredenciaisException(String mensagem) {
     super(mensagem);
 }
}
