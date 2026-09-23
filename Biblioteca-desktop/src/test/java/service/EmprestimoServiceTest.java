package service;

import br.com.biblioteca.client.EmprestimoClient;
import br.com.biblioteca.client.UsuarioClient;
import br.com.biblioteca.exception.LimiteDeEmprestimosAtingidoException;
import br.com.biblioteca.exception.MaximoRenovacoesAtingidoException;
import br.com.biblioteca.exception.RecursoNaoEncontradoException;
import br.com.biblioteca.exception.SemExemplaresDisponiveisException;
import br.com.biblioteca.model.common.PageResponse;
import br.com.biblioteca.model.emprestimo.EmprestimoResponse;
import br.com.biblioteca.service.EmprestimoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmprestimoServiceTest {
	@Mock
	private EmprestimoClient emprestimoClient;

	@Mock
	private UsuarioClient usuarioClient;

	private EmprestimoService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		service = new EmprestimoService(emprestimoClient, new ObjectMapper());
	}

	@Test
	void deveListarEmprestimosQuandoRespostaForBemSucedida() {
		HttpResponse<String> response = response(200, "{\"content\":[{\"id\":8,\"usuarioId\":3}]}");

		when(emprestimoClient.listar("token", 3L, 0, 10))
				.thenReturn(CompletableFuture.completedFuture(response));

		PageResponse<EmprestimoResponse> result = service.listar("token", 3L, 0, 10).join();

		assertEquals(8L, result.getContent().get(0).getId());
	}

	@Test
	void deveLancarExcecaoAoListarUsuarioInexistente() {
		HttpResponse<String> response = response(404, "");

		when(emprestimoClient.listar("token", 3L, 0, 10))
				.thenReturn(CompletableFuture.completedFuture(response));

		CompletionException exception = assertThrows(
				CompletionException.class,
				() -> service.listar("token", 3L, 0, 10).join()
		);

		assertInstanceOf(RecursoNaoEncontradoException.class, exception.getCause());
	}

	@Test
	void deveDevolverEmprestimoComSucesso() {
		HttpResponse<String> response = response(200, "");

		when(emprestimoClient.devolver("token", 8L))
				.thenReturn(CompletableFuture.completedFuture(response));

		assertDoesNotThrow(() -> service.devolver("token", 8L).join());
	}

	@Test
	void deveLancarExcecaoQuandoNaoHouverExemplares() {
		HttpResponse<String> response = response(409, "SEM_EXEMPLARES");

		when(emprestimoClient.emprestar(eq("token"), anyString()))
				.thenReturn(CompletableFuture.completedFuture(response));

		CompletionException exception = assertThrows(
				CompletionException.class,
				() -> service.emprestar("token", 4L, 3L).join()
		);

		assertInstanceOf(SemExemplaresDisponiveisException.class, exception.getCause());
	}

	@Test
	void deveLancarExcecaoAoAtingirLimiteDeEmprestimos() {
		HttpResponse<String> response = response(409, "LIMITE_EMPRESTIMOS");

		when(emprestimoClient.emprestar(eq("token"), anyString()))
				.thenReturn(CompletableFuture.completedFuture(response));

		CompletionException exception = assertThrows(
				CompletionException.class,
				() -> service.emprestar("token", 4L, 3L).join()
		);

		assertInstanceOf(LimiteDeEmprestimosAtingidoException.class, exception.getCause());
	}

	@Test
	void deveRenovarEmprestimoERetornarNovaData() {
		HttpResponse<String> response = response(200, "\"2026-10-01\"");

		when(emprestimoClient.renovar("token", 8L))
				.thenReturn(CompletableFuture.completedFuture(response));

		LocalDate result = service.renovar("token", 8L).join();

		assertEquals(LocalDate.of(2026, 10, 1), result);
	}

	@Test
	void deveLancarExcecaoAoAtingirMaximoDeRenovacoes() {
		HttpResponse<String> response = response(409, "");

		when(emprestimoClient.renovar("token", 8L))
				.thenReturn(CompletableFuture.completedFuture(response));

		CompletionException exception = assertThrows(
				CompletionException.class,
				() -> service.renovar("token", 8L).join()
		);

		assertInstanceOf(MaximoRenovacoesAtingidoException.class, exception.getCause());
	}

	@Test
	void deveCalcularQuantidadeDeEmprestimosAtivos() {
		HttpResponse<String> response = response(200, "[{\"id\":1},{\"id\":2}]");

		when(emprestimoClient.listarAtivos("token", 3L))
				.thenReturn(CompletableFuture.completedFuture(response));

		assertEquals("2", service.calcularNumAtivos("token", 3L).join());
	}

	@Test
	void deveCalcularQuantidadeDevolvidaHoje() {
		HttpResponse<String> response = response(200, "[{\"id\":1}]");

		when(emprestimoClient.listarADevolverHoje("token", 3L))
				.thenReturn(CompletableFuture.completedFuture(response));

		assertEquals("1", service.calcularNumDevolucoes("token", 3L).join());
	}

	private static HttpResponse<String> response(int status, String body) {
		HttpResponse<String> response = mock(HttpResponse.class);
		when(response.statusCode()).thenReturn(status);
		when(response.body()).thenReturn(body);
		return response;
	}
}
