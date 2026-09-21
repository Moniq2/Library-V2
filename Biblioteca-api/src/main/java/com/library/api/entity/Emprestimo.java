package com.library.api.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
public class Emprestimo {
    @ManyToOne
    @JoinColumn(nullable = false, name = "usuario_id")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(nullable = false, name = "exemplar_id")
    private Exemplar exemplar;

    @ManyToOne
    @JoinColumn(nullable = false, name = "livro_id")
    private Livro livro;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (name = "data_emprestimo", nullable = false)
    private LocalDate dataEmprestimo;

    @Column (name = "data_devolucao", nullable = false)
    private LocalDate dataDevolucao;

    @Column (nullable = false)
    private boolean ativo = true;

    public Emprestimo() {}

    public Emprestimo(Usuario usuario, Exemplar exemplar, Livro livro) {
        this.usuario = usuario;
        this.exemplar = exemplar;
        this.livro = livro;
        setDatas();
    }

    public void setDatas(){
        this.dataEmprestimo = LocalDate.now();
        this.dataDevolucao = dataEmprestimo.plusDays(15);
    }
}
