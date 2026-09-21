package com.library.api.controller;

import com.library.api.dto.LivroResponseDTO;
import com.library.api.service.LivroService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("library/api/v1/livros")
public class LivroController {
    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @GetMapping
    public ResponseEntity<Page<LivroResponseDTO>> listar(@RequestParam int pagina, @RequestParam int limite){
        return ResponseEntity.ok(livroService.listar(pagina, limite));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LivroResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    public ResponseEntity<Page<LivroResponseDTO>> buscarPorTermo(@RequestParam String q, @RequestParam int pagina, @RequestParam int limite){
        return ResponseEntity.ok(livroService.buscarPorTermo(q, pagina, limite));
    }
}
