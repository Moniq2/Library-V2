package service;

import br.com.biblioteca.client.UsuarioClient;
import br.com.biblioteca.exception.CredenciaisInvalidasException;
import br.com.biblioteca.exception.EmailInvalidoException;
import br.com.biblioteca.model.usuario.LoginResponse;
import br.com.biblioteca.model.usuario.UsuarioLoginRequest;
import br.com.biblioteca.model.usuario.UsuarioResponse;
import br.com.biblioteca.service.UsuarioService;
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

class UsuarioServiceTest {
	@Mock
	private UsuarioClient usuarioClient;

	private UsuarioService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		service = new UsuarioService(usuarioClient, new ObjectMapper());
	}

	@Test
	void deveLogarComCredenciaisValidas() {
		HttpResponse<String> response = response(200, "{\"token\":\"abc\"}");

		when(usuarioClient.logar(anyString()))
				.thenReturn(CompletableFuture.completedFuture(response));

		LoginResponse result = service.logar(
				new UsuarioLoginRequest("user@email.com", "senha")
		).join();

		assertEquals("abc", result.getToken());
		verify(usuarioClient).logar(contains("user@email.com"));
	}

	@Test
	void deveRejeitarLoginComEmailInvalido() {
		HttpResponse<String> response = response(200, "{\"token\":\"abc\"}");

		when(usuarioClient.logar(anyString()))
				.thenReturn(CompletableFuture.completedFuture(response));

		CompletionException exception = assertThrows(
				CompletionException.class,
				() -> service.logar(
						new UsuarioLoginRequest("email-invalido", "senha")
				).join()
		);

		assertInstanceOf(EmailInvalidoException.class, exception.getCause());
	}

	@Test
	void deveRejeitarLoginComCredenciaisInvalidas() {
		HttpResponse<String> response = response(401, "");

		when(usuarioClient.logar(anyString()))
				.thenReturn(CompletableFuture.completedFuture(response));

		CompletionException exception = assertThrows(
				CompletionException.class,
				() -> service.logar(
						new UsuarioLoginRequest("user@email.com", "senha")
				).join()
		);

		assertInstanceOf(CredenciaisInvalidasException.class, exception.getCause());
	}

	@Test
	void deveBuscarUsuarioAtualComRespostaValida() {
		HttpResponse<String> response = response(
				200,
				"{\"id\":3,\"email\":\"user@email.com\"}"
		);

		when(usuarioClient.buscarUsuarioAtual("token"))
				.thenReturn(CompletableFuture.completedFuture(response));

		UsuarioResponse result = service.buscarUsuarioAtual("token").join();

		assertEquals("user@email.com", result.getEmail());
	}

	private static HttpResponse<String> response(int status, String body) {
		HttpResponse<String> response = mock(HttpResponse.class);
		when(response.statusCode()).thenReturn(status);
		when(response.body()).thenReturn(body);
		return response;
	}
}
