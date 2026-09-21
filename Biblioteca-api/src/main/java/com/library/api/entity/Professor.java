package com.library.api.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.DiscriminatorValue;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.List;

@Entity
@DiscriminatorValue("Professor")
public class Professor extends Usuario {
    public Professor(String nome, String email, String senha){
        super(nome, email, senha);
    }

    public Professor() {}

    public int getLimiteEmprestimos(){
        return 6;
    }
}
