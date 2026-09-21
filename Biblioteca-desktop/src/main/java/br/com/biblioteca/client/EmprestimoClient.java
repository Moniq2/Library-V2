package br.com.biblioteca.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class EmprestimoClient {
    private static final String BASE_URL = "http://localhost:8081/library/api/v1/emprestimos";

    private final HttpClient client;
    private final ObjectMapper mapper;

    public EmprestimoClient() {
        this(HttpClient.newHttpClient(), new ObjectMapper());
    }

    EmprestimoClient(HttpClient client, ObjectMapper objectMapper) {
        this.client = client;
        this.mapper = objectMapper;
        mapper.registerModule(new JavaTimeModule());
    }

    public CompletableFuture<HttpResponse<String>> buscarEmprestimo (String token, Long id) {
        String url = String.format(BASE_URL + "/%d", id);
        HttpRequest request = HttpRequest
                .newBuilder(URI.create(url))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> listar(String token, Long usuarioId, int pagina, int limite) {
        String url = String.format(BASE_URL + "?id=%d&limite=%d&pagina=%d", usuarioId, limite, pagina);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> renovar (String token, Long emprestimoId) {
        String url = String.format(BASE_URL + "/" + emprestimoId + "/renovar");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
    public CompletableFuture<HttpResponse<String>> listarAtivos(String token, Long usuarioId) {
        return listarPorRecurso(token, "ativos", usuarioId);
    }

    public CompletableFuture<HttpResponse<String>> listarAtrasados(String token, Long usuarioId) {
        return listarPorRecurso(token, "atrasados", usuarioId);
    }

    public CompletableFuture<HttpResponse<String>> listarARenovar(String token, Long usuarioId) {
        return listarPorRecurso(token, "renovar", usuarioId);
    }

    public CompletableFuture<HttpResponse<String>> listarADevolverHoje(String token, Long usuarioId) {
        return listarPorRecurso(token, "devolucao-hoje", usuarioId);
    }

    public CompletableFuture<HttpResponse<String>> emprestar(String token, String emprestimoJson) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token)
                .POST(HttpRequest.BodyPublishers.ofString(emprestimoJson))
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> devolver(String token, Long emprestimoId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + emprestimoId))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .method("PATCH", HttpRequest.BodyPublishers.noBody())
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    private CompletableFuture<HttpResponse<String>> listarPorRecurso(String token, String recurso, Long usuarioId) {
        String url = String.format(BASE_URL + "/%s?usuarioId=%d", recurso, usuarioId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}

