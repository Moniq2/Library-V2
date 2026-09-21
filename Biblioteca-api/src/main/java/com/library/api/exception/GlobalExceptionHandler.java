package com.library.api.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({
            RecursoNaoEncontradoException.class,
            LivroNaoEncontradoException.class,
            EmprestimoNaoEncontradoException.class
    })
    public ResponseEntity<String> tratarRecursoNaoEncontrado(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CredenciaisInvalidasException.class)
    public ResponseEntity<String> tratarCredenciaisInvalidas(CredenciaisInvalidasException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler({
            EmailJaCadastradoException.class,
            DesativacaoNaoPermitidaException.class,
            SemExemplaresDisponiveisException.class,
            EmprestimoException.class,
            LimiteDeEmprestimosAtingidoException.class,
            MaximoRenovacoesAtingidoException.class
    })
    public ResponseEntity<String> tratarConflito(RuntimeException ex){
        if (ex instanceof SemExemplaresDisponiveisException) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("SEM_EXEMPLARES|" + ex.getMessage());
        }

        if (ex instanceof LimiteDeEmprestimosAtingidoException) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body("LIMITE_EMPRESTIMOS|" + ex.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler({
            DadosInvalidosException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<String> tratarRequisicaoInvalida(RuntimeException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler({
            RenovacaoInvalidaException.class
    })
    public ResponseEntity<String> tratarConteudoNaoProcessavel(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ex.getMessage());
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> tratarErroInesperado(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado.");
    }
}
