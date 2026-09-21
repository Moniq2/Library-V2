package com.library.api.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Exemplar {
    @ManyToOne
    @JoinColumn(nullable = false, name = "livro_id")
    private Livro livro;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private boolean disponivel = true;

    Exemplar() {}

    public Livro getLivro(){
        return livro;
    }

    public Long getId(){
        return id;
    }

    public void setLivro(Livro livro){
        Objects.requireNonNull(livro, "livro inválido.");
        this.livro = livro;
    }

    public void setId(Long id){
        this.id = id;
    }

    public void setDisponivel(Boolean bool){
        Objects.requireNonNull(bool, "Booleano inválido.");
        disponivel = bool;
    }
}
