package com.library.api.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Livro {
    @OneToMany(
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        mappedBy = "livro",
        fetch = FetchType.LAZY
    )
    private List<Exemplar> exemplares;

    @Column (nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String autor;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "data_publicacao")
    private LocalDate dataPublicacao;

    Livro() {}
}
