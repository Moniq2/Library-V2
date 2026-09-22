package com.library.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Dados necessários para realizar o login")
public class LoginRequestDTO {

    @NotBlank
    @Email
    @Schema(description = "E-mail cadastrado do usuário", example = "monique@gmail.com")
    private String email;

    @NotBlank
    @Schema(description = "Senha cadastrada do usuário", example = "123456")
    private String senha;
}