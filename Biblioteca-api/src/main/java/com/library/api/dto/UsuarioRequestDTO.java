package com.library.api.dto;

import com.library.api.util.TipoUsuario;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados necessários para cadastrar um novo usuário")
public class UsuarioRequestDTO {

    @NotBlank
    @Schema(description = "Nome completo do usuário", example = "Monique Silva")
    private String nome;

    @NotBlank
    @Email
    @Schema(description = "E-mail do usuário", example = "monique@gmail.com")
    private String email;

    @NotBlank
    @Size(min = 7, max = 20)
    @Schema(description = "Senha do usuário", example = "1234567")
    private String senha;

    @NotNull
    @Schema(description = "Tipo do usuário que será cadastrado", example = "ALUNO")
    private TipoUsuario tipo;
}