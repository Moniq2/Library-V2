package com.library.api.controller;

import com.library.api.dto.LoginResponseDTO;
import com.library.api.dto.UsuarioRequestDTO;
import com.library.api.dto.UsuarioResponseDTO;
import com.library.api.dto.UsuarioUpdateDTO;
import com.library.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("library/api/v1/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(usuarioService.buscarPorId(usuarioId));
    }

    @PatchMapping("{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody UsuarioUpdateDTO usuarioUpdateDTO){
        return ResponseEntity.ok(usuarioService.atualizarDados(id, usuarioUpdateDTO));
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> cadastrar(@Valid @RequestBody UsuarioRequestDTO usuarioRequestDTO){
        usuarioService.cadastrarUsuario(usuarioRequestDTO);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> usuarioAtual(Authentication authentication) {
        String email = authentication.getName();
        UsuarioResponseDTO usuario = usuarioService.buscarPorEmail(email);
        return ResponseEntity.ok(usuario);
    }
}
