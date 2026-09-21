package com.library.api.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@DiscriminatorValue("Aluno")
public class Aluno extends Usuario {
    public Aluno() {}
    public Aluno(String nome, String email, String senha){
        super(nome, email, senha);
    }

    public int getLimiteEmprestimos(){
        return 3;
    }
}
