package com.library.api.controller;

import com.library.api.dto.EmprestimoRequestDTO;
import com.library.api.dto.EmprestimoResponseDTO;
import com.library.api.service.EmprestimoService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
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

@RestController
@RequestMapping("library/api/v1/emprestimos")
public class EmprestimoController {
    private final EmprestimoService emprestimoService;

    public EmprestimoController(EmprestimoService emprestimoService) {
        this.emprestimoService = emprestimoService;
    }

    @GetMapping
    public ResponseEntity<Page<EmprestimoResponseDTO>> listarEmprestimos(@RequestParam(name="id") Long usuarioId, @RequestParam int limite, @RequestParam int pagina) {
        return ResponseEntity.ok(emprestimoService.listar(usuarioId, limite, pagina));
    }

    @GetMapping("/ativos")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarEmprestimosAtivos(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.listarAtivos(usuarioId));
    }

    @GetMapping("/atrasados")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarEmprestimosAtrasados(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.listarAtrasados(usuarioId));
    }

    @GetMapping("/renovar")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarEmprestimosARenovar(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.listarARenovar(usuarioId));
    }

    @GetMapping("/devolucao-hoje")
    public ResponseEntity<List<EmprestimoResponseDTO>> listarEmprestimoDevolucaoHoje(@RequestParam Long usuarioId) {
        return ResponseEntity.ok(emprestimoService.listarADevolverHoje(usuarioId));
    }

    @PostMapping
    public ResponseEntity<EmprestimoResponseDTO> criarEmprestimo(@Valid @RequestBody EmprestimoRequestDTO emprestimoRequestDTO){
        return ResponseEntity.ok(emprestimoService.emprestar(emprestimoRequestDTO));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        emprestimoService.devolver(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/renovar")
    public ResponseEntity<LocalDate> renovar (@PathVariable Long id) {
        return ResponseEntity.ok(emprestimoService.renovar(id));
    }
}
