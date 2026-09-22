package com.library.api.controller;

import com.library.api.dto.EmprestimoRequestDTO;
import com.library.api.dto.EmprestimoResponseDTO;
import com.library.api.service.EmprestimoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Emprestimos", description = "Operações relacionadas aos emprestimos dos usuários")
@RestController
@RequestMapping("library/api/v1/usuarios/")
@SecurityRequirement(name = "bearerAuth")
public class EmprestimoController {

    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @GetMapping("{id}/emprestimos")
    @Operation(summary = "Listar emprestimos", description = "Lista todos os emprestimos do usuário de forma paginada.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprestimos listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Page<EmprestimoResponseDTO>> listar(@Parameter(description = "ID do usuário", example = "1") @PathVariable(name = "id") Long usuarioId, @Parameter(description = "Quantidade máxima de emprestimos por página", example = "10") @RequestParam int limite, @Parameter(description = "Número da página. A primeira página é 0.", example = "0") @RequestParam int pagina) {
        return ResponseEntity.ok(emprestimoService.listar(usuarioId, limite, pagina));
    }

    @GetMapping("{id}/emprestimos/buscar")
    @Operation(summary = "Buscar emprestimos", description = "Busca os emprestimos do usuário com base no termo de pesquisa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pesquisa realizada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Page<EmprestimoResponseDTO>> buscar(@Parameter(description = "ID do usuário", example = "1") @PathVariable("id") Long usuarioId, @Parameter(description = "Termo utilizado na pesquisa", example = "O Pequeno Príncipe") @RequestParam String q, @Parameter(description = "Quantidade máxima de emprestimos por página", example = "10") @RequestParam int limite, @Parameter(description = "Número da página. A primeira página é 0.", example = "0") @RequestParam int pagina) {
        return ResponseEntity.ok(emprestimoService.buscar(q, usuarioId, limite, pagina));
    }

    @GetMapping("{id}/emprestimos/ativos")
    @Operation(summary = "Listar emprestimos ativos", description = "Lista apenas os emprestimos ativos do usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprestimos ativos listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<EmprestimoResponseDTO>> listarAtivos(@Parameter(description = "ID do usuário", example = "1") @PathVariable("id") Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.listarAtivos(usuarioId));
    }

    @GetMapping("{id}/emprestimos/atrasados")
    @Operation(summary = "Listar emprestimos atrasados", description = "Lista apenas os emprestimos atrasados do usuário.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprestimos atrasados listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<EmprestimoResponseDTO>> listarAtrasados(@Parameter(description = "ID do usuário", example = "1") @PathVariable("id") Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.listarAtrasados(usuarioId));
    }

    @GetMapping("{id}/emprestimos/a-renovar")
    @Operation(summary = "Listar emprestimos para renovar", description = "Lista apenas os emprestimos do usuário que podem ser renovados.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprestimos disponíveis para renovação listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<EmprestimoResponseDTO>> listarARenovar(@Parameter(description = "ID do usuário", example = "1") @PathVariable("id") Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.listarARenovar(usuarioId));
    }

    @GetMapping("{id}/emprestimos/devolucao-hoje")
    @Operation(summary = "Listar emprestimos para devolver hoje", description = "Lista apenas os emprestimos do usuário que devem ser devolvidos no dia atual.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprestimos com devolução prevista para hoje listados com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<List<EmprestimoResponseDTO>> listarADevolverHoje(@Parameter(description = "ID do usuário", example = "1") @PathVariable("id") Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.listarADevolverHoje(usuarioId));
    }

    @PostMapping("emprestimos")
    @Operation(summary = "Criar emprestimo", description = "Cadastra um novo emprestimo no sistema.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Emprestimo criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados do emprestimo inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Livro, exemplar ou usuário não encontrado"),
            @ApiResponse(responseCode = "409", description = "Não foi possível realizar o emprestimo")
    })
    public ResponseEntity<EmprestimoResponseDTO> criar(@Valid @RequestBody EmprestimoRequestDTO emprestimoRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(emprestimoService.emprestar(emprestimoRequestDTO));
    }

    @PatchMapping("emprestimos/{id}/devolver")
    @Operation(summary = "Devolver emprestimo", description = "Realiza a devolução de um emprestimo utilizando seu ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Emprestimo devolvido com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Não foi possível realizar a devolução")
    })
    public ResponseEntity<Void> devolver(@Parameter(description = "ID do emprestimo", example = "1") @PathVariable("id") Long id) {
        emprestimoService.devolver(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("emprestimos/{id}/renovar")
    @Operation(summary = "Renovar emprestimo", description = "Realiza a renovação de um emprestimo utilizando seu ID e retorna a nova data de entrega.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Emprestimo renovado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "404", description = "Emprestimo não encontrado"),
            @ApiResponse(responseCode = "409", description = "Emprestimo não pode ser renovado")
    })
    public ResponseEntity<LocalDate> renovar(@Parameter(description = "ID do emprestimo", example = "1") @PathVariable("id") Long id) {
        return ResponseEntity.ok(emprestimoService.renovar(id));
    }
}
