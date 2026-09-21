package com.library.api.entity;
import com.library.api.util.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo")
@Data
abstract public class Usuario implements UserDetails{

    @Column (nullable = false)
    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String senha;

    @Column
    private Boolean ativo = true;

    @Column
    private UserRole role;

    //Construtor Padrão
    public Usuario() {}

    public Usuario(String nome, String email, String senha){
        setNome(nome);
        setEmail(email);
        setSenha(senha);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == UserRole.PROFESSOR) return List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
        else return List.of(new SimpleGrantedAuthority("ROLE_ALUNO"));
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public int getLimiteEmprestimos() {
        return 1;
    }
}
