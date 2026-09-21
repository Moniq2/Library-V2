package br.com.biblioteca.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpRequest.BodyPublishers;
import java.util.concurrent.CompletableFuture;

public class UsuarioClient {
    private static final String BASE_URL = "http://localhost:8081/library/api/v1";
    private final HttpClient client =  HttpClient.newHttpClient();

    public CompletableFuture<HttpResponse<String>> cadastrar(String usuarioRequestJson ) {
        String url = String.format(BASE_URL + "/usuarios/signup");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(usuarioRequestJson))
                .build();

        return client.sendAsync(request, BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> logar(String usuarioRequestJson) {
        String url = String.format(BASE_URL + "/auth");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(BodyPublishers.ofString(usuarioRequestJson))
                .build();

        return client.sendAsync(request, BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> buscarUsuarioAtual(String token) {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(BASE_URL + "/usuarios/me"))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .build();

        return client.sendAsync(request, BodyHandlers.ofString());
    }
}
