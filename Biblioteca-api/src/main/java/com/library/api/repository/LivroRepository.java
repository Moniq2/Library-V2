package com.library.api.repository;
import com.library.api.entity.Livro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {
    @Query ("""
        SELECT l FROM Livro l
        WHERE UPPER(l.titulo) LIKE  UPPER(CONCAT('%', :termo, '%'))
        OR UPPER(l.autor) LIKE UPPER(CONCAT('%', :termo, '%'))
        ORDER BY l.titulo ASC
    """)
    public Page<Livro> findByTerm(@Param("termo") String termo, Pageable pageable);
}
