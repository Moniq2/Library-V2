package com.library.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "Dados de um empréstimo realizado por um usuário")
public class EmprestimoResponseDTO {

    @NotNull
    @Min(0)
    @Schema(description = "ID do usuário que realizou o empréstimo", example = "1")
    Long usuarioId;

    @NotNull
    @Min(0)
    @Schema(description = "ID do exemplar emprestado", example = "5")
    Long exemplarId;

    @NotNull
    @Schema(description = "Dados do livro relacionado ao exemplar")
    LivroResponseDTO livro;

    @NotNull
    @Min(0)
    @Schema(description = "ID do empréstimo", example = "10")
    Long id;

    @NotNull
    @Schema(description = "Data em que o empréstimo foi realizado", example = "2026-09-22")
    LocalDate dataEmprestimo;

    @NotNull
    @Schema(description = "Data prevista para devolução do empréstimo", example = "2026-10-06")
    LocalDate dataDevolucao;

    @NotNull
    @Schema(description = "Indica se o empréstimo está ativo", example = "true")
    Boolean ativo;
}