package com.library.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
@Schema(description = "Dados de um livro da biblioteca")
public class LivroResponseDTO {

    @NotNull
    @Size(min = 1, max = 100)
    @Schema(description = "Título do livro", example = "O Pequeno Príncipe")
    private String titulo;

    @Size(min = 1, max = 50)
    @Schema(description = "Autor do livro", example = "Antoine de Saint-Exupéry")
    private String autor;

    @Min(0)
    @Schema(description = "ID do livro", example = "1")
    private long id;

    @Schema(description = "Data de publicação do livro", example = "1943-04-06")
    private LocalDate dataPublicacao;

    LivroResponseDTO() {}
}