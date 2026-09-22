package com.library.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Dados utilizados para atualizar as informações de um usuário")
public class UsuarioUpdateDTO {

    @Size(min = 7, max = 100)
    @Schema(description = "Novo nome do usuário", example = "Monique Silva")
    private String nome;

    @Email
    @Schema(description = "Novo e-mail do usuário", example = "monique.novo@gmail.com")
    private String email;

    @Size(min = 7, max = 20)
    @Schema(description = "Nova senha do usuário", example = "1234567")
    private String senha;
}