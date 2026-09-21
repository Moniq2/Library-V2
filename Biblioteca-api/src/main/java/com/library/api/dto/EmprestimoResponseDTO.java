package com.library.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class EmprestimoResponseDTO {
    @NotNull
    @Min(0)
    Long usuarioId;

    @NotNull
    @Min(0)
    Long exemplarId;

    @NotNull
    LivroResponseDTO livro;

    @NotNull
    @Min(0)
    Long id;

    @NotNull
    LocalDate dataEmprestimo;

    @NotNull
    LocalDate dataDevolucao;

    @NotNull
    Boolean ativo;
}
