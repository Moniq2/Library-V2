package com.library.api.controller;

import com.library.api.dto.LoginRequestDTO;
import com.library.api.dto.LoginResponseDTO;
import com.library.api.security.JwtUtil;
import com.library.api.service.UsuarioDetailsService;
import com.library.api.service.UsuarioService;
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
public class AuthController {
    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final UsuarioDetailsService usuarioDetailsService;
    private final JwtUtil jwtUtil;

    public AuthController(UsuarioService usuarioService, AuthenticationManager authenticationManager, UsuarioDetailsService usuarioDetailsService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.authenticationManager = authenticationManager;
        this.usuarioDetailsService = usuarioDetailsService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO requisicao) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(requisicao.getEmail(), requisicao.getSenha()));
        } catch (BadCredentialsException excecao) {
            return ResponseEntity.status(401).body("E-mail ou senha inválidos.");
        }

        UserDetails userDetails = usuarioDetailsService.loadUserByUsername(requisicao.getEmail());
        String token = jwtUtil.gerarToken(userDetails);
        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

}
