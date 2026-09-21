package com.library.api.dto;

import com.library.api.util.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UsuarioResponseDTO {
    @NotBlank
    private String nome;

    @NotBlank
    @Email
    private String email;

    @NotNull
    private String id;

    @NotNull
    private Boolean ativo;

    @NotNull
    private TipoUsuario tipo;
}
