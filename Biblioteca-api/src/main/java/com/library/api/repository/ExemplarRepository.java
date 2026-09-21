package com.library.api.repository;

import com.library.api.entity.Exemplar;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ExemplarRepository extends JpaRepository<Exemplar, Long> {
    @Query("""
        SELECT e
        FROM Exemplar e
        WHERE e.livro.id = :livroId
        AND e.disponivel = true
    """)
    Page<Exemplar> findExemplarDisponivel(@Param("livroId") Long id, Pageable pageable);
}
