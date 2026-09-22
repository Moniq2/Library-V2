package com.library.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmprestimoRequestDTO {
    @NotNull
    @Min(0)
    @Schema(description = "ID do usuário")
    Long usuarioId;

    @NotNull
    @Min(0)
    @Schema(description = "ID do livro")
    Long livroId;
}
