package br.com.biblioteca.client;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class LivroClient {
    final HttpClient client;
    private static final String BASE_URL = "http://localhost:8081/library/api/v1/livros";

    public LivroClient() {
        this.client = HttpClient.newHttpClient();
    }

    public CompletableFuture<HttpResponse<String>> listar(int pagina, int limite){
            String url = String.format(BASE_URL + "?pagina=%d&limite=%d", pagina, limite);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> buscarPorId(Long id){
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/" + id))
                .header("Accept", "application/json")
                .GET()
                .build();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }

    public CompletableFuture<HttpResponse<String>> buscarPorTermo(String termo, int pagina, int limite){
        String termoEncoded = URLEncoder.encode(termo, StandardCharsets.UTF_8);
        String url = String.format(BASE_URL + "/buscar?q=%s&pagina=%d&limite=%d", termoEncoded, pagina, limite);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
    }
}
