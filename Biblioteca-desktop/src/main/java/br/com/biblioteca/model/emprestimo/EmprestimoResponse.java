package br.com.biblioteca.model.emprestimo;

import br.com.biblioteca.model.livro.LivroResponse;

import java.time.LocalDate;

public class EmprestimoResponse {
    private Long usuarioId;

    private LivroResponse livro;

    private Long exemplarId;

    private Long id;

    private LocalDate dataEmprestimo;

    private LocalDate dataDevolucao;

    private boolean ativo;

    public EmprestimoResponse(){};

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getExemplarId() {
        return exemplarId;
    }

    public void setExemplarId(Long exemplarId) {
        this.exemplarId = exemplarId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long emprestimoId) {
        this.id = emprestimoId;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public void setDataEmprestimo(LocalDate dataEmprestimo) {
        this.dataEmprestimo = dataEmprestimo;
    }

    public LocalDate getDataDevolucao() {
        return dataDevolucao;
    }

    public void setDataDevolucao(LocalDate dataDevolucao) {
        this.dataDevolucao = dataDevolucao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public LivroResponse getLivro() {
        return livro;
    }

    public void setLivro(LivroResponse livro) {
        this.livro = livro;
    }
}
