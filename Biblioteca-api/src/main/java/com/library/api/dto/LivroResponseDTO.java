package com.library.api.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class LivroResponseDTO {

    @Size(min = 1, max = 100)
    @NotNull
    private String titulo;

    @Size(min = 1, max = 50)
    private String autor;

    @Min(0)
    private long id;

    private LocalDate dataPublicacao;

    LivroResponseDTO() {}
}
