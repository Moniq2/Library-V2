package com.library.api.controller;

import com.library.api.dto.LivroResponseDTO;
import com.library.api.service.LivroService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("library/api/v1/livros")
@Tag(name = "Livros", description = "Operações relacionadas aos livros da biblioteca")
public class LivroController {

    private final LivroService livroService;

    public LivroController(LivroService livroService) {
        this.livroService = livroService;
    }

    @GetMapping
    @Operation(summary = "Listar livros", description = "Lista os livros da biblioteca de forma paginada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livros listados com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros de paginação inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<Page<LivroResponseDTO>> listar(@Parameter(description = "Número da página. A primeira página é 0.", example = "0") @RequestParam int pagina, @Parameter(description = "Quantidade máxima de livros retornados por página.", example = "10") @RequestParam int limite) {
        return ResponseEntity.ok(livroService.listar(pagina, limite));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar livro por ID", description = "Busca e retorna um livro específico pelo seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Livro encontrado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Livro não encontrado")
    })
    public ResponseEntity<LivroResponseDTO> buscarPorId(@Parameter(description = "ID do livro que será buscado.", example = "1") @PathVariable Long id) {
        return ResponseEntity.ok(livroService.buscarPorId(id));
    }

    @GetMapping("/buscar")
    @Operation(summary = "Buscar livros por termo", description = "Pesquisa livros utilizando um termo e retorna os resultados de forma paginada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Parâmetros da pesquisa inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado")
    })
    public ResponseEntity<Page<LivroResponseDTO>> buscarPorTermo(@Parameter(description = "Termo utilizado na pesquisa.", example = "O Pequeno Príncipe", required = true) @RequestParam String q, @Parameter(description = "Número da página. A primeira página é 0.", example = "0") @RequestParam int pagina, @Parameter(description = "Quantidade máxima de livros retornados por página.", example = "10") @RequestParam int limite) {
        return ResponseEntity.ok(livroService.buscarPorTermo(q, pagina, limite));
    }
}