package com.library.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EmprestimoRequestDTO {
    @NotNull
    @Min(0)
    Long usuarioId;

    @NotNull
    @Min(0)
    Long livroId;
}
