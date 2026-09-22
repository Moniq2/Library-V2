package com.library.api.repository;

import com.library.api.entity.Emprestimo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long> {
    @Query("""
        SELECT e
        FROM Emprestimo e
        WHERE e.usuario.id = :usuarioId
        ORDER BY e.dataEmprestimo DESC
    """)
    Page<Emprestimo> findEmprestimos(@Param("usuarioId") Long id, Pageable pageable);

    @Query("""
        SELECT e
        FROM Emprestimo e
        WHERE UPPER(e.livro.titulo) LIKE UPPER(CONCAT('%', :termo, '%'))
        OR CAST(e.dataEmprestimo AS string) LIKE TRIM(:termo)
        OR CAST(e.id AS string) LIKE TRIM(:termo)
    """)
    Page<Emprestimo> findEmprestimosByTerm(@Param("termo") String termo, Pageable pageable);

    @Query("""
        SELECT e
        FROM Emprestimo e
        WHERE e.usuario.id = :usuarioId
        AND e.ativo = true
        ORDER BY e.dataEmprestimo DESC
    """)
    List<Emprestimo> findEmprestimosAtivos(@Param("usuarioId") Long id);

    @Query("""
        SELECT e
        FROM Emprestimo e
        WHERE e.usuario.id = :usuarioId
        AND e.dataDevolucao < :dataAtual
        AND e.ativo = true
        ORDER BY e.dataEmprestimo DESC
    """)
    List<Emprestimo> findEmprestimosAtrasados(@Param("usuarioId") Long id, @Param("dataAtual") LocalDate dataAtual);

    @Query("""
        SELECT e
        FROM Emprestimo e
        WHERE e.usuario.id = :usuarioId
        AND e.ativo = true
        AND e.dataDevolucao = :dataAtual
    """)
    List<Emprestimo> findEmprestimosDevolucaoHoje(@Param("usuarioId") Long id, @Param("dataAtual") LocalDate dataAtual);
}
