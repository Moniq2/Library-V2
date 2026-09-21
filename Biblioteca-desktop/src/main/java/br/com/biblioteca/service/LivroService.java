package br.com.biblioteca.service;

import br.com.biblioteca.client.LivroClient;
import br.com.biblioteca.exception.FalhaNoProcessamentoDeRespostaException;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.biblioteca.model.common.PageResponse;
import br.com.biblioteca.model.livro.LivroResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.util.concurrent.CompletableFuture;

public class LivroService {
    private final LivroClient livroClient;
    private final ObjectMapper mapper;

    public LivroService() {
        this.livroClient = new LivroClient();
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
    }

    public CompletableFuture<PageResponse<LivroResponse>> listar(int pagina, int limite) {
        return livroClient.listar(pagina, limite)
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return mapper.readValue(response.body(), mapper.getTypeFactory()
                                    .constructParametricType(PageResponse.class, LivroResponse.class));
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                            throw new FalhaNoProcessamentoDeRespostaException("Nao foi possivel interpretar a resposta do servidor.");
                        }
                    }
                    throw new RuntimeException("Nao foi possivel listar livros. " + response.body());
                });
    }

    public CompletableFuture<PageResponse<LivroResponse>> buscarPorTermo(String termo, int pagina, int limite) {
        if (termo == null) {
            return CompletableFuture.failedFuture(new RuntimeException("TERMO VAZIO!"));
        }

        return livroClient.buscarPorTermo(termo, pagina, limite)
                .thenApply(response -> {
                    if (response.statusCode() > 300) {
                        throw new RuntimeException("Nao foi possivel buscar livros. Erro:" + response.statusCode());
                    }
                    try {
                        return mapper.readValue(response.body(), mapper.getTypeFactory()
                                .constructParametricType(PageResponse.class, LivroResponse.class));
                    } catch (JsonProcessingException e) {
                        throw new FalhaNoProcessamentoDeRespostaException("Nao foi possivel interpretar a resposta do servidor.");
                    }
                });
    }

    public CompletableFuture<LivroResponse> buscarPorId(Long id) {
        return livroClient.buscarPorId(id)
                .thenApply(response -> {
                    if (response.statusCode() != 200) {
                        try {
                            return mapper.readValue(response.body(), LivroResponse.class);
                        }
                        catch (JsonProcessingException e) {
                            throw new FalhaNoProcessamentoDeRespostaException("Erro ao processar resposta do servidor ao buscar livro.");
                        }
                    } else if (response.statusCode() == 404) {
                        throw new RecursoNaoEncontradoException("Não foi possível encontrar livro de Id " + id);
                    }
                    else {
                        throw new RuntimeException("Erro ao buscar livro.");
                    }
                });
    }
}
