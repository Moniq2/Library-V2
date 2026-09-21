package br.com.biblioteca.service;

import br.com.biblioteca.client.UsuarioClient;
import br.com.biblioteca.exception.*;
import br.com.biblioteca.model.usuario.LoginResponse;
import br.com.biblioteca.model.usuario.UsuarioLoginRequest;
import br.com.biblioteca.model.usuario.UsuarioRequest;
import br.com.biblioteca.model.usuario.UsuarioResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.validator.routines.EmailValidator;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class UsuarioService {
    private final ObjectMapper mapper;
    private final UsuarioClient usuarioClient;
    private final EmailValidator validator;

    public UsuarioService() {
        this.validator = EmailValidator.getInstance();
        this.mapper = new ObjectMapper();
        this.usuarioClient = new UsuarioClient();
    }

    public void cadastrar(UsuarioRequest usuarioRequest) {
        try {
            String usuarioRequestJson = mapper.writeValueAsString(usuarioRequest);
            usuarioClient.cadastrar(usuarioRequestJson)
                    .thenAccept(response -> {
                        if (!validator.isValid(usuarioRequest.getEmail())){
                           throw new EmailInvalidoException("Email invalido!");
                        }
                        else if (response.statusCode() == 409) {
                            throw new ConflitoDeCredenciaisException("Nao foi possivel cadastrar usuario. Conflito de e-mails.");
                        }
                        throw new RuntimeException("Nao foi possivel cadastrar. Erro HTTP: " + response.statusCode());
                    });
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Erro na conversão JSON");
        }
    }

    public CompletableFuture<LoginResponse> logar(UsuarioLoginRequest usuarioLoginRequest) {
        try {
            String usuarioRequestJson = mapper.writeValueAsString(usuarioLoginRequest);

            return usuarioClient.logar(usuarioRequestJson)
                    .thenApply(response -> {
                        if (!validator.isValid(usuarioLoginRequest.getEmail())) {
                            throw new EmailInvalidoException("Email deve ser um email válido!");
                        }
                        if (response.statusCode() == 200) {
                            try {
                                return mapper.readValue(response.body(), LoginResponse.class);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                                throw new FalhaNoProcessamentoDeRespostaException("Nao foi possivel interpretar a resposta do servidor.");
                            }
                        }
                        if (response.statusCode() == 401) {
                            throw new CredenciaisInvalidasException("Credenciais inválidas.");
                        }
                        throw new RuntimeException("Erro inesperado da API. Status: " + response.statusCode());
                    });

        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<UsuarioResponse> buscarUsuarioAtual(String token) {
        return usuarioClient.buscarUsuarioAtual(token)
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return mapper.readValue(response.body(), UsuarioResponse.class);
                        }
                        catch (JsonProcessingException e) {
                            System.out.println("deu erro aqui" + response.body());
                            throw new FalhaNoProcessamentoDeRespostaException("Erro ao processar resposta da API");
                        }
                    }
                    if (response.statusCode() == 404){
                        throw new RecursoNaoEncontradoException("Usuário não encontrado");
                    }
                    throw new RuntimeException("Erro inesperado da API. Status: " + response.statusCode());
                });
    }
}
