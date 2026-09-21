package com.library.api.service;

import com.library.api.dto.EmprestimoRequestDTO;
import com.library.api.dto.EmprestimoResponseDTO;
import com.library.api.entity.Emprestimo;
import com.library.api.entity.Exemplar;
import com.library.api.entity.Livro;
import com.library.api.entity.Usuario;
import com.library.api.exception.*;
import com.library.api.repository.EmprestimoRepository;
import com.library.api.repository.ExemplarRepository;
import com.library.api.repository.LivroRepository;
import com.library.api.repository.UsuarioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.print.Pageable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmprestimoService {
    private final EmprestimoRepository emprestimoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ModelMapper mapper;
    private final ExemplarService exemplarService;
    private final LivroRepository livroRepository;

    EmprestimoService(EmprestimoRepository emprestimoRepository, UsuarioRepository usuarioRepository, ModelMapper mapper, ExemplarService exemplarService, LivroRepository livroRepository) {
        this.emprestimoRepository = emprestimoRepository;
        this.exemplarService = exemplarService;
        this.usuarioRepository = usuarioRepository;
        this.mapper = mapper;
        this.livroRepository = livroRepository;
    }

    @Transactional
    public EmprestimoResponseDTO emprestar(EmprestimoRequestDTO emprestimoRequestDTO){
        Long usuarioId = emprestimoRequestDTO.getUsuarioId();
        Long livroId = emprestimoRequestDTO.getLivroId();

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Não foi possível encontrar usuário de id " + usuarioId));

        if (listarAtivos(usuarioId).size() >= usuario.getLimiteEmprestimos()){
            throw new LimiteDeEmprestimosAtingidoException("Não foi possível realizar emprestimo. Limite de emprestimos atingido.");
        }

        Livro livro = livroRepository.findById(livroId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Não foi possível encontrar livro de id " + livroId));
        Exemplar exemplar = exemplarService.buscarExemplarDisponivel(livroId);

        Emprestimo emprestimo = new Emprestimo(usuario, exemplar, livro);

        emprestimoRepository.save(emprestimo);
        exemplar.setDisponivel(false);

        return mapper.map(emprestimo, EmprestimoResponseDTO.class);
    }

    @Transactional
    public void devolver(Long emprestimoID){
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoID)
                .orElseThrow(() -> new RecursoNaoEncontradoException("emprestimo não encontrado."));
        emprestimo.getExemplar().setDisponivel(true);
        emprestimo.setAtivo(false);
    }

    @Transactional
    public LocalDate renovar(Long emprestimoID){
        Emprestimo emprestimo = emprestimoRepository.findById(emprestimoID)
                .orElseThrow(() -> new RecursoNaoEncontradoException("emprestimo não encontrado."));

        if (!emprestimo.isAtivo()) {
            throw new RenovacaoInvalidaException("Esse emprestimo já foi devolvido.");
        }

        LocalDate dataEmprestimo = emprestimo.getDataEmprestimo();
        LocalDate dataDevolucao = emprestimo.getDataDevolucao();

        if (dataDevolucao.isEqual(dataEmprestimo.plusDays(30))){
            throw new MaximoRenovacoesAtingidoException("Número máximo de renovações atingido.");
        }

        if (LocalDate.now().isAfter(dataDevolucao.minusDays(5)) && LocalDate.now().isBefore(dataDevolucao)) {
            LocalDate novaData = dataDevolucao.plusDays(15);
            emprestimo.setDataDevolucao(novaData);
            return novaData;

        } else {
            throw new RenovacaoInvalidaException("Só é possível renovar empréstimo a partir de 5 dias antes da data de entrega.");
        }
    }

    public Page<EmprestimoResponseDTO> listar(Long usuarioId, int limite, int pagina){
        usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RecursoNaoEncontradoException("usuario não encontrado."));
        Page<Emprestimo> emprestimos = emprestimoRepository.findEmprestimos(usuarioId, PageRequest.of(pagina, limite));

        return emprestimos.map(emprestimo -> (mapper.map(emprestimo, EmprestimoResponseDTO.class)));
    }

    public List<EmprestimoResponseDTO> listarAtivos(Long usuarioId){
        usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontradoException("usuario não encontrado."));
        List<Emprestimo> emprestimosAtivos = emprestimoRepository.findEmprestimosAtivos(usuarioId);
        return emprestimosAtivos.stream().map(emprestimo -> mapper.map(emprestimo, EmprestimoResponseDTO.class)).toList();
    }

    public List<EmprestimoResponseDTO> listarAtrasados(Long usuarioId){
        usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontradoException("usuario não encontrado."));
        LocalDate dataAtual =  LocalDate.now();
        List<Emprestimo> emprestimosAtivos = emprestimoRepository.findEmprestimosAtrasados(usuarioId, dataAtual);
        return emprestimosAtivos.stream().map(emprestimo -> mapper.map(emprestimo, EmprestimoResponseDTO.class)).toList();
    }

    public List<EmprestimoResponseDTO> listarARenovar(Long usuarioId){
        usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontradoException("usuario não encontrado."));
        LocalDate dataAtual =  LocalDate.now();
        List<Emprestimo> emprestimosAtivos = emprestimoRepository.findEmprestimosAtivos(usuarioId);
        List<Emprestimo> emprestimosARenovar = new ArrayList<>();

        for(Emprestimo emprestimo : emprestimosAtivos){
            LocalDate dataDevolucao = emprestimo.getDataDevolucao();
            if(dataAtual.isAfter(dataDevolucao.minusDays(6)) && dataAtual.isBefore(dataDevolucao)){
                emprestimosARenovar.add(emprestimo);
            }
        }
        return emprestimosARenovar.stream().map(emprestimo -> mapper.map(emprestimo, EmprestimoResponseDTO.class)).toList();
    }

    public List<EmprestimoResponseDTO> listarADevolverHoje(Long usuarioId){
        usuarioRepository.findById(usuarioId).orElseThrow(() -> new RecursoNaoEncontradoException("usuario não encontrado."));
        LocalDate dataAtual = LocalDate.now();
        List<Emprestimo> emprestimos = emprestimoRepository.findEmprestimosDevolucaoHoje(usuarioId, dataAtual);

        return emprestimos.stream().map(emprestimo -> mapper.map(emprestimo, EmprestimoResponseDTO.class)).toList();
    }
}
