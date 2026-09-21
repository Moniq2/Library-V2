package br.com.biblioteca.model.usuario;

public class UsuarioLoginRequest {
    public UsuarioLoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
    private String email;
    private String senha;

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSenha() {
        return senha;
    }
}
