package com.novapost.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.novapost.client.NovaPostClient;
import com.novapost.model.InternetDocumentListItem;
import org.junit.jupiter.api.Test;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

class NovaPostControllerTtnLookupTest {

	private static final LocalDateTime FROM = LocalDateTime.of(2026, 1, 1, 0, 0);
	private static final LocalDateTime TO = LocalDateTime.of(2026, 1, 31, 0, 0);
	private static final String TARGET = "20400000012345";

	@Test
	void findsTtnOnFirstPage() {
		String page = page(List.of("20400000000001", TARGET));
		NovaPostController controller = controllerReturning(page);

		InternetDocumentListItem found = controller.fetchWaybillByTtn(TARGET, FROM, TO).orElseThrow();

		assertEquals(TARGET, found.intDocNumber());
	}

	@Test
	void findsTtnOnLaterPageWhenFirstPageIsFull() {
		List<String> fullPage = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			fullPage.add("2040000000" + String.format("%04d", i));
		}
		CountingClient client = new CountingClient(page(fullPage), page(List.of(TARGET)));
		NovaPostController controller = new NovaPostController(client.novaPostClient());

		assertEquals(TARGET, controller.fetchWaybillByTtn(TARGET, FROM, TO).orElseThrow().intDocNumber());
		assertEquals(2, client.calls());
	}

	@Test
	void returnsEmptyWhenTtnNotInRange() {
		NovaPostController controller = controllerReturning(page(List.of("20400000000001")));

		assertTrue(controller.fetchWaybillByTtn(TARGET, FROM, TO).isEmpty());
	}

	@Test
	void nodeLookupReturnsRawJson() {
		NovaPostController controller = controllerReturning(page(List.of(TARGET)));

		JsonNode node = controller.fetchWaybillNodeByTtn(TARGET, FROM, TO).orElseThrow();

		assertEquals(TARGET, node.path("IntDocNumber").asText());
	}

	@Test
	void rejectsBlankTtnAndReversedRange() {
		NovaPostController controller = controllerReturning(page(List.of()));

		assertThrows(IllegalArgumentException.class, () -> controller.fetchWaybillByTtn(" ", FROM, TO));
		assertThrows(IllegalArgumentException.class, () -> controller.fetchWaybillByTtn(TARGET, TO, FROM));
	}

	private static String page(List<String> ttns) {
		StringBuilder data = new StringBuilder();
		for (int i = 0; i < ttns.size(); i++) {
			if (i > 0) data.append(',');
			data.append("{\"IntDocNumber\":\"").append(ttns.get(i)).append("\"}");
		}
		return "{\"success\":true,\"data\":[" + data + "],\"errors\":[],\"warnings\":[]}";
	}

	private static NovaPostController controllerReturning(String body) {
		return new NovaPostController(new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL,
				new CountingClient(body).httpClient(), NovaPostClient.createDefaultMapper()));
	}

	/** Returns the given bodies in order; the last one repeats once they run out. */
	private static final class CountingClient {
		private final List<String> bodies;
		private int calls;

		CountingClient(String... bodies) {
			this.bodies = List.of(bodies);
		}

		int calls() {
			return calls;
		}

		NovaPostClient novaPostClient() {
			return new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, httpClient(), NovaPostClient.createDefaultMapper());
		}

		HttpClient httpClient() {
			return new HttpClient() {
				@Override
				@SuppressWarnings("unchecked")
				public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> handler) {
					String body = bodies.get(Math.min(calls, bodies.size() - 1));
					calls++;
					return (HttpResponse<T>) new StubResponse(body, request);
				}

				@Override
				public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler) {
					return CompletableFuture.completedFuture(send(request, handler));
				}

				@Override
				public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> handler, HttpResponse.PushPromiseHandler<T> push) {
					return CompletableFuture.completedFuture(send(request, handler));
				}

				@Override public Optional<CookieHandler> cookieHandler() { return Optional.empty(); }
				@Override public Optional<Duration> connectTimeout() { return Optional.empty(); }
				@Override public Redirect followRedirects() { return Redirect.NORMAL; }
				@Override public Optional<ProxySelector> proxy() { return Optional.empty(); }
				@Override public SSLContext sslContext() { return null; }
				@Override public SSLParameters sslParameters() { return null; }
				@Override public Optional<Authenticator> authenticator() { return Optional.empty(); }
				@Override public Version version() { return Version.HTTP_2; }
				@Override public Optional<Executor> executor() { return Optional.empty(); }
			};
		}
	}

	private record StubResponse(String body, HttpRequest request) implements HttpResponse<String> {
		@Override public int statusCode() { return 200; }
		@Override public HttpRequest request() { return request; }
		@Override public Optional<HttpResponse<String>> previousResponse() { return Optional.empty(); }
		@Override public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (k, v) -> true); }
		@Override public String body() { return body; }
		@Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
		@Override public URI uri() { return request.uri(); }
		@Override public HttpClient.Version version() { return HttpClient.Version.HTTP_2; }
	}
}
