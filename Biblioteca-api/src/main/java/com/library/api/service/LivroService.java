package com.library.api.service;
import com.library.api.dto.LivroResponseDTO;
import com.library.api.exception.RecursoNaoEncontradoException;
import com.library.api.repository.LivroRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LivroService {
    LivroRepository livroRepository;
    public LivroService(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }
    ModelMapper mapper = new ModelMapper();

    public Page<LivroResponseDTO> listar(int pagina, int limite){
        Pageable pageable = PageRequest.of(pagina, limite);
        return livroRepository.findAll(pageable).map(livro -> mapper.map(livro, LivroResponseDTO.class));
    }

    public LivroResponseDTO buscarPorId(long id){
        return livroRepository.findById(id)
                .map(livro -> mapper.map(livro, LivroResponseDTO.class))
                .orElseThrow(() -> new RecursoNaoEncontradoException("Não foi possível encontrar livro de id: " + id));
    }

    public Page<LivroResponseDTO> buscarPorTermo(String termo, int pagina, int limite){
        Pageable pageable = PageRequest.of(pagina, limite);
        return livroRepository.findByTerm(termo, pageable)
                .map(livro -> mapper.map(livro, LivroResponseDTO.class));
    }
}
