package service;

import br.com.biblioteca.client.LivroClient;
import br.com.biblioteca.exception.FalhaNoProcessamentoDeRespostaException;
import br.com.biblioteca.model.common.PageResponse;
import br.com.biblioteca.model.livro.LivroResponse;
import br.com.biblioteca.service.LivroService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LivroServiceTest {
	@Mock
	private LivroClient livroClient;
	private LivroService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		service = new LivroService(livroClient, new ObjectMapper());
	}

	@Test
	void deveListarLivrosQuandoRespostaForBemSucedida() {
		HttpResponse<String> response = response(200, "{\"content\":[{\"id\":7,\"titulo\":\"Clean Code\"}]}");

		when(livroClient.listar(0, 10))
				.thenReturn(CompletableFuture.completedFuture(response));

		PageResponse<LivroResponse> result = service.listar(0, 10).join();

		assertEquals(1, result.getContent().size());
		assertEquals(7L, result.getContent().get(0).getId());
	}

	@Test
	void deveFalharAoListarQuandoJsonForInvalido() {
		HttpResponse<String> response = response(200, "invalido");

		when(livroClient.listar(0, 10))
				.thenReturn(CompletableFuture.completedFuture(response));

		CompletionException exception = assertThrows(
				CompletionException.class,
				() -> service.listar(0, 10).join()
		);

		assertInstanceOf(
				FalhaNoProcessamentoDeRespostaException.class,
				exception.getCause()
		);
	}

	@Test
	void deveRejeitarBuscaPorTermoNuloSemConsultarCliente() {
		CompletionException exception = assertThrows(
				CompletionException.class,
				() -> service.buscarPorTermo(null, 0, 10).join()
		);

		assertEquals("TERMO VAZIO!", exception.getCause().getMessage());
		verifyNoInteractions(livroClient);
	}

	@Test
	void deveRetornarLivroAoBuscarPorIdComRespostaDeErroQueContemLivro() {
		HttpResponse<String> response = response(
				400,
				"{\"id\":7,\"titulo\":\"Clean Code\"}"
		);

		when(livroClient.buscarPorId(7L))
				.thenReturn(CompletableFuture.completedFuture(response));

		LivroResponse result = service.buscarPorId(7L).join();

		assertEquals(7L, result.getId());
	}

	private static HttpResponse<String> response(int status, String body) {
		HttpResponse<String> response = mock(HttpResponse.class);
		when(response.statusCode()).thenReturn(status);
		when(response.body()).thenReturn(body);
		return response;
	}
}