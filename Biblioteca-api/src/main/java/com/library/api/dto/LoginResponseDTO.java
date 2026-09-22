package com.library.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Resposta retornada após uma autenticação bem-sucedida")
public class LoginResponseDTO {

    @Schema(description = "Token JWT utilizado para autenticar as requisições protegidas", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtb25pcXVlQGdtYWlsLmNvbSJ9...")
    private final String token;

    public LoginResponseDTO(String token) {
        this.token = token;
    }
}
