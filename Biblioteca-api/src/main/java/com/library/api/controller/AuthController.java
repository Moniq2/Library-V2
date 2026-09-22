package com.library.api.controller;

import com.library.api.dto.LoginRequestDTO;
import com.library.api.dto.LoginResponseDTO;
import com.library.api.security.JwtUtil;
import com.library.api.service.UsuarioDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("library/api/v1/auth")
@Tag(name = "Usuarios Auth", description = "Operações relacionadas à autenticação do usuário.")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthenticationManager authenticationManager, UsuarioDetailsService usuarioDetailsService, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.usuarioDetailsService = usuarioDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    @Operation(
            summary = "Realizar login",
            description = "Autentica o usuário utilizando e-mail e senha e gera um token JWT para acesso aos endpoints protegidos."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de login inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "E-mail ou senha inválidos"
            )
    })
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO requisicao) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            requisicao.getEmail(),
                            requisicao.getSenha()
                    )
            );
        } catch (BadCredentialsException excecao) {
            return ResponseEntity.status(401).body("E-mail ou senha inválidos.");
        }

        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(requisicao.getEmail());
        String token = jwtUtil.gerarToken(userDetails);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}