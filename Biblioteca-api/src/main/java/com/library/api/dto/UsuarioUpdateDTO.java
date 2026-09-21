package com.library.api.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioUpdateDTO {
    @Size(min = 7, max = 100)
    private String nome;

    @Email
    private String email;

    @Size(min = 7, max = 20)
    private String senha;

}
