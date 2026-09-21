package br.com.biblioteca.service;

import br.com.biblioteca.client.EmprestimoClient;
import br.com.biblioteca.client.UsuarioClient;
import br.com.biblioteca.exception.*;
import br.com.biblioteca.model.common.PageResponse;
import br.com.biblioteca.model.emprestimo.EmprestimoRequest;
import br.com.biblioteca.model.emprestimo.EmprestimoResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class EmprestimoService {
    private final EmprestimoClient emprestimoClient;
    private final ObjectMapper mapper;
    private final UsuarioClient usuarioClient;

    public EmprestimoService() {
        this.emprestimoClient = new EmprestimoClient();
        this.mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        usuarioClient = new UsuarioClient();
    }

    public CompletableFuture<PageResponse<EmprestimoResponse>> listar(String token ,long usuarioId, int pagina, int limite) {
        return emprestimoClient.listar(token, usuarioId, pagina, limite)
                .thenApply(response -> {
                    if (response.statusCode() == 404) {
                        throw new RecursoNaoEncontradoException("Recurso nao encontrado.");
                    }
                    else if (response.statusCode() == 403) {
                        throw new SemPermissaoParaAcessarRecursoException("Erro ao listar emprestimos. Acesso negado");
                    }
                    else if (response.statusCode() != 200) {
                        throw new RuntimeException("Erro ao listar emprestimos. " + response.body());
                    }
                    try {
                        System.out.println(response.body());

                        return mapper.readValue(
                                response.body(),
                                mapper.getTypeFactory().constructParametricType(PageResponse.class, EmprestimoResponse.class)
                        );
                    } catch (JsonProcessingException e) {
                        throw new FalhaNoProcessamentoDeRespostaException("Erro na interpretacao da resposta http. " + response.body());
                    }
                });
    }

    public CompletableFuture<Void> devolver(String token, long emprestimoId) {
        return emprestimoClient.devolver(token, emprestimoId)
                .thenAccept(response -> {
                    if (response.statusCode() == 404) {
                        throw new RecursoNaoEncontradoException("Emprestimo nao encontrado.");
                    }
                    if (response.statusCode() != 200) {
                        throw new RuntimeException("Erro ao desativar emprestimo. " + response.body());
                    }
                });
    }

    public CompletableFuture<EmprestimoResponse> emprestar(String token, long livroId, long usuarioId) {
        EmprestimoRequest emprestimo = new EmprestimoRequest(usuarioId, livroId);
        try {
            String emprestimoJson = mapper.writeValueAsString(emprestimo);
            return emprestimoClient.emprestar(token, emprestimoJson)
                    .thenApply(response -> {
                        switch (response.statusCode()) {
                            case 404:
                                throw new RecursoNaoEncontradoException("Usuário não encontrado.");
                            case 409:
                                if (response.body().contains("SEM_EXEMPLARES")) {
                                    throw new SemExemplaresDisponiveisException("Não há exemplares disponíveis.");
                                }
                                else if (response.body().contains("LIMITE_EMPRESTIMOS")) {
                                    throw new LimiteDeEmprestimosAtingidoException("Limite de emprestimos atingidos");
                                }
                            case 403:
                                throw new SemPermissaoParaAcessarRecursoException("Você não tem permissão para acessar esse recurso.");
                            case 200:
                                try {
                                    return mapper.readValue(response.body(), EmprestimoResponse.class);
                                } catch (JsonProcessingException e) {
                                    throw new FalhaNoProcessamentoDeRespostaException("Nao foi possivel interpretar a resposta da API." + response.body());
                                }
                            default:
                                throw new RuntimeException("Código de erro: " + response.statusCode());
                        }
                    });
        } catch (JsonProcessingException e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    public CompletableFuture<LocalDate> renovar(String token, long emprestimoId) {
        return emprestimoClient.renovar(token, emprestimoId)
                .thenApply(response -> {
                    switch (response.statusCode()) {
                        case 404:
                            throw new RecursoNaoEncontradoException("Emprestimo não encontrado.");
                        case 409:
                            throw new MaximoRenovacoesAtingidoException("Número máximo de renovações atingido.");
                        case 403:
                            throw new SemPermissaoParaAcessarRecursoException("Você não tem permissão para acessar esse recurso.");
                        case 422:
                            throw new RenovacaoInvalidaException("Você não pode renovar este emprestimo agora. Aguarde mais um pouco!");
                        case 200:
                            try {
                                return mapper.readValue(response.body(), LocalDate.class);
                            }
                            catch (JsonProcessingException e) {
                                throw new FalhaNoProcessamentoDeRespostaException("Não foi possivel interpretar a resposta http.");
                            }
                        default:
                            throw new RuntimeException("Código de erro: " + response.statusCode());
                    }
                });
    }


    public CompletableFuture<EmprestimoResponse> buscarPorId(String token, long emprestimoId) {
        return emprestimoClient.buscarEmprestimo(token, emprestimoId)
                .thenApply(response -> {
                    if (response.statusCode() == 404) {
                        throw new RecursoNaoEncontradoException("Emprestimo nao encontrado.");
                    }
                    else if (response.statusCode() == 403){
                        throw new SemPermissaoParaAcessarRecursoException("Você não tem permissão para acessar esse recurso.");
                    }
                    else if (response.statusCode() != 200) {
                        throw new RuntimeException("Erro ao buscar emprestimo.\nCódigo de erro: " + response.statusCode());
                    }
                    try {
                        return mapper.readValue(response.body(), EmprestimoResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new FalhaNoProcessamentoDeRespostaException("Nao foi possivel interpretar a resposta http." + response.body());
                    }
                });
    }

    public CompletableFuture<String> calcularNumAtivos(String token, long usuarioId) {
        return emprestimoClient.listarAtivos(token, usuarioId)
                .thenApply(response -> {
                    if (response.statusCode() == 404) {
                        throw new RecursoNaoEncontradoException("Usuario nao encontrado.");
                    }
                    else if (response.statusCode() != 200) {
                        throw new RuntimeException("Erro ao calcular emprestimos ativos. \nCódigo de erro: " + response.statusCode());
                    }
                    else if (response.statusCode() == 403){
                        throw new SemPermissaoParaAcessarRecursoException("Você não tem permissão para acessar esse recurso.");
                    }
                    try {
                        List<EmprestimoResponse> emprestimosAtivos = mapper.readValue(
                                response.body(),
                                new TypeReference<List<EmprestimoResponse>>() {}
                        );
                        return String.valueOf(emprestimosAtivos.size());
                    } catch (JsonProcessingException e) {
                        throw new FalhaNoProcessamentoDeRespostaException("Nao foi possivel interpretar a resposta http. " + response.body());
                    }
                });
    }

    public CompletableFuture<String> calcularNumDevolucoes(String token, long usuarioId) {
        return emprestimoClient.listarADevolverHoje(token, usuarioId)
                .thenApply(response -> {
                    if (response.statusCode() == 404) {
                        throw new RecursoNaoEncontradoException("Usuario nao encontrado.");
                    }
                    else if (response.statusCode() != 200) {
                        throw new RuntimeException("Erro ao calcular numero de devolucoes. \nCódigo de erro: " + response.statusCode());
                    }
                    try {
                        List<EmprestimoResponse> emprestimos = mapper.readValue(
                                response.body(),
                                new TypeReference<List<EmprestimoResponse>>() {}
                        );
                        return String.valueOf(emprestimos.size());
                    } catch (JsonProcessingException e) {
                        throw new FalhaNoProcessamentoDeRespostaException("Nao foi possivel interpretar a resposta http." + response.body());
                    }
                });
    }
}
