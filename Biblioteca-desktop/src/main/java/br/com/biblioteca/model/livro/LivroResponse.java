package br.com.biblioteca.model.livro;

import java.time.LocalDate;

public class LivroResponse {
    private String titulo;
    private String autor;
    private long id;
    private LocalDate dataPublicacao;

    public LivroResponse() {}

    public String getTitulo() {
        return titulo;
    }
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }
    public String getAutor() {
        return autor;
    }
    public void setAutor(String autor) {
        this.autor = autor;
    }
    public Long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public LocalDate getDataPublicacao() {
        return dataPublicacao;
    }
    public void setDataPublicacao(LocalDate dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }
}
