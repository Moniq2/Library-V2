package br.com.biblioteca.security;

public class Session {
    private String token;

    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
    public void logout(){
        token = null;
    }
}
