package com.library.api.service;

import com.library.api.entity.Exemplar;
import com.library.api.exception.SemExemplaresDisponiveisException;
import com.library.api.repository.ExemplarRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ExemplarService {
    private final ExemplarRepository repository;
    public ExemplarService(ExemplarRepository repository){
       this.repository =  repository;
    }

    public Exemplar buscarExemplarDisponivel(Long livroId) {
        Page<Exemplar> exemplares = repository.findExemplarDisponivel(livroId, PageRequest.of(0,1));
        if (exemplares.getTotalElements() == 0) {
            throw new SemExemplaresDisponiveisException("Não foram encontrados exemplares disponíveis.");
        }
        return exemplares.getContent().get(0);
    }
}

