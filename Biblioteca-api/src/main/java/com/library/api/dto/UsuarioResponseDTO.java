package com.library.api.dto;

import com.library.api.util.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Dados retornados de um usuário da biblioteca")
public class UsuarioResponseDTO {

    @NotBlank
    @Schema(description = "Nome do usuário", example = "Monique Silva")
    private String nome;

    @NotBlank
    @Email
    @Schema(description = "E-mail do usuário", example = "monique@gmail.com")
    private String email;

    @NotNull
    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @NotNull
    @Schema(description = "Indica se o usuário está ativo", example = "true")
    private Boolean ativo;

    @NotNull
    @Schema(description = "Tipo do usuário", example = "ALUNO")
    private TipoUsuario tipo;
}
