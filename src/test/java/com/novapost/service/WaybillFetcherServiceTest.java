package com.novapost.service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.novapost.client.NovaPostClient;
import com.novapost.controller.NovaPostApiException;
import com.novapost.model.InternetDocumentListItem;
import org.junit.jupiter.api.Test;

import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

import static org.junit.jupiter.api.Assertions.*;

public class WaybillFetcherServiceTest {

	public record WaybillMiniDto(
			@JsonProperty("Ref") String ref,
			@JsonProperty("IntDocNumber") String ttn,
			@JsonProperty("Cost") double cost
	) {}

	@Test
	void testMultiDayAndMultiPagePagination() {
		// Day 1 has 150 items (Page 1: 100, Page 2: 50)
		// Day 2 has 30 items (Page 1: 30)
		List<String> capturedRequests = new ArrayList<>();

		MockScriptedHttpClient httpClient = new MockScriptedHttpClient((req, reqBody) -> {
			capturedRequests.add(reqBody);
			if (reqBody.contains("01.08.2026") && reqBody.contains("\"Page\":1")) {
				return generateResponseJson(100, "day1-p1");
			} else if (reqBody.contains("01.08.2026") && reqBody.contains("\"Page\":2")) {
				return generateResponseJson(50, "day1-p2");
			} else if (reqBody.contains("02.08.2026") && reqBody.contains("\"Page\":1")) {
				return generateResponseJson(30, "day2-p1");
			}
			return generateResponseJson(0, "empty");
		});

		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, httpClient, NovaPostClient.createDefaultMapper());
		WaybillFetcherService service = new WaybillFetcherService(client, 0); // 0ms delay for fast test

		LocalDate from = LocalDate.of(2026, 8, 1);
		LocalDate to = LocalDate.of(2026, 8, 2);

		List<InternetDocumentListItem> documents = service.fetchAllDocuments(from, to);

		assertEquals(180, documents.size());
		assertEquals(3, capturedRequests.size());

		// Verify timestamp formatting
		assertTrue(capturedRequests.get(0).contains("\"DateTimeFrom\":\"01.08.2026 00:00:00\""));
		assertTrue(capturedRequests.get(0).contains("\"DateTimeTo\":\"01.08.2026 23:59:59\""));
		assertTrue(capturedRequests.get(0).contains("\"Page\":1"));
		assertTrue(capturedRequests.get(0).contains("\"Limit\":100"));

		assertTrue(capturedRequests.get(1).contains("\"DateTimeFrom\":\"01.08.2026 00:00:00\""));
		assertTrue(capturedRequests.get(1).contains("\"DateTimeTo\":\"01.08.2026 23:59:59\""));
		assertTrue(capturedRequests.get(1).contains("\"Page\":2"));

		assertTrue(capturedRequests.get(2).contains("\"DateTimeFrom\":\"02.08.2026 00:00:00\""));
		assertTrue(capturedRequests.get(2).contains("\"DateTimeTo\":\"02.08.2026 23:59:59\""));
		assertTrue(capturedRequests.get(2).contains("\"Page\":1"));
	}

	@Test
	void testGenericClassProjectionDirectlyFromService() {
		MockScriptedHttpClient httpClient = new MockScriptedHttpClient((req, reqBody) ->
				generateResponseJson(5, "doc")
		);

		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, httpClient, NovaPostClient.createDefaultMapper());
		WaybillFetcherService service = new WaybillFetcherService(client, 0);

		LocalDate day = LocalDate.of(2026, 8, 10);
		List<WaybillMiniDto> dtos = service.fetchAllDocuments(day, day, WaybillMiniDto.class);

		assertEquals(5, dtos.size());
		assertTrue(dtos.getFirst().ttn().startsWith("TTN-doc-"));
		assertTrue(dtos.getFirst().cost() > 0);
	}

	@Test
	void testMapProjectionDirectlyFromService() {
		MockScriptedHttpClient httpClient = new MockScriptedHttpClient((req, reqBody) ->
				generateResponseJson(3, "doc")
		);

		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, httpClient, NovaPostClient.createDefaultMapper());
		WaybillFetcherService service = new WaybillFetcherService(client, 0);

		Map<String, Object> fields = new HashMap<>();
		fields.put("IntDocNumber", null);
		fields.put("Cost", null);

		LocalDate day = LocalDate.of(2026, 8, 15);
		List<Map<String, Object>> results = service.fetchAllDocuments(day, day, fields);

		assertEquals(3, results.size());
		assertEquals(2, results.getFirst().size());
		assertTrue(results.getFirst().containsKey("IntDocNumber"));
		assertTrue(results.getFirst().containsKey("Cost"));
	}

	@Test
	void testStreamingBatchConsumer() {
		MockScriptedHttpClient httpClient = new MockScriptedHttpClient((req, reqBody) -> {
			if (reqBody.contains("\"Page\":1")) {
				return generateResponseJson(100, "p1");
			} else {
				return generateResponseJson(40, "p2");
			}
		});

		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, httpClient, NovaPostClient.createDefaultMapper());
		WaybillFetcherService service = new WaybillFetcherService(client, 0);

		List<Integer> batchSizes = new ArrayList<>();
		LocalDate day = LocalDate.of(2026, 8, 20);

		service.fetchAllDocuments(day, day, (List<InternetDocumentListItem> batch) -> {
			batchSizes.add(batch.size());
		});

		assertEquals(2, batchSizes.size());
		assertEquals(100, batchSizes.get(0));
		assertEquals(40, batchSizes.get(1));
	}

	@Test
	void testApiErrorResponseThrowsNovaPostApiExceptionWithErrors() {
		String errorResponse = """
				{
				  "success": false,
				  "data": [],
				  "errors": ["API Key is invalid or expired"],
				  "warnings": [],
				  "info": []
				}
				""";

		MockScriptedHttpClient httpClient = new MockScriptedHttpClient((req, reqBody) -> errorResponse);
		NovaPostClient client = new NovaPostClient("bad-key", NovaPostClient.DEFAULT_API_URL, httpClient, NovaPostClient.createDefaultMapper());
		WaybillFetcherService service = new WaybillFetcherService(client, 0);

		LocalDate day = LocalDate.of(2026, 8, 25);
		NovaPostApiException ex = assertThrows(NovaPostApiException.class, () ->
				service.fetchAllDocuments(day, day)
		);

		assertTrue(ex.getErrors().contains("API Key is invalid or expired"));
	}

	@Test
	void testTransientFailureRetryWithBackoff() {
		AtomicInteger attempts = new AtomicInteger(0);

		MockScriptedHttpClient httpClient = new MockScriptedHttpClient((req, reqBody) -> {
			int count = attempts.incrementAndGet();
			if (count == 1) {
				// Simulate HTTP 500 error
				return new MockHttpResponse<>(500, "Internal Server Error", req);
			}
			return new MockHttpResponse<>(200, generateResponseJson(10, "recovered"), req);
		});

		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, httpClient, NovaPostClient.createDefaultMapper());
		WaybillFetcherService service = new WaybillFetcherService(client, null, 0, 3, 50);

		LocalDate day = LocalDate.of(2026, 8, 26);
		List<InternetDocumentListItem> docs = service.fetchAllDocuments(day, day);

		assertEquals(10, docs.size());
		assertEquals(2, attempts.get());
	}

	@Test
	void testInvalidDateRangeValidation() {
		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, new MockScriptedHttpClient((r, b) -> ""), NovaPostClient.createDefaultMapper());
		WaybillFetcherService service = new WaybillFetcherService(client, 0);

		assertThrows(IllegalArgumentException.class, () -> service.fetchAllDocuments(null, LocalDate.now()));
		assertThrows(IllegalArgumentException.class, () -> service.fetchAllDocuments(LocalDate.now(), null));
		assertThrows(IllegalArgumentException.class, () -> service.fetchAllDocuments(LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 1)));
	}

	static String generateResponseJson(int count, String prefix) {
		StringBuilder sb = new StringBuilder();
		sb.append("{\"success\":true,\"data\":[");
		for (int i = 0; i < count; i++) {
			if (i > 0) sb.append(",");
			sb.append("{")
					.append("\"Ref\":\"ref-").append(prefix).append("-").append(i).append("\",")
					.append("\"IntDocNumber\":\"TTN-").append(prefix).append("-").append(i).append("\",")
					.append("\"Cost\":\"").append(100.0 + i).append("\"")
					.append("}");
		}
		sb.append("],\"errors\":[],\"warnings\":[],\"info\":[]}");
		return sb.toString();
	}

	@FunctionalInterface
	interface ResponseSupplier {
		Object get(HttpRequest request, String body);
	}

	static class MockScriptedHttpClient extends HttpClient {
		private final ResponseSupplier supplier;

		public MockScriptedHttpClient(ResponseSupplier supplier) {
			this.supplier = supplier;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseHandler) {
			String body = "";
			if (request.bodyPublisher().isPresent()) {
				MockBodySubscriber sub = new MockBodySubscriber();
				request.bodyPublisher().get().subscribe(sub);
				body = sub.getBody();
			}
			Object res = supplier.get(request, body);
			if (res instanceof HttpResponse<?> r) {
				return (HttpResponse<T>) r;
			}
			return (HttpResponse<T>) new MockHttpResponse<>(200, res.toString(), request);
		}

		@Override
		public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseHandler) {
			return CompletableFuture.completedFuture(send(request, responseHandler));
		}

		@Override
		public <T> CompletableFuture<HttpResponse<T>> sendAsync(HttpRequest request, HttpResponse.BodyHandler<T> responseHandler, HttpResponse.PushPromiseHandler<T> pushPromiseHandler) {
			return CompletableFuture.completedFuture(send(request, responseHandler));
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
	}

	private static class MockBodySubscriber implements java.util.concurrent.Flow.Subscriber<java.nio.ByteBuffer> {
		private final StringBuilder sb = new StringBuilder();

		@Override public void onSubscribe(java.util.concurrent.Flow.Subscription subscription) { subscription.request(Long.MAX_VALUE); }
		@Override public void onNext(java.nio.ByteBuffer item) { sb.append(java.nio.charset.StandardCharsets.UTF_8.decode(item)); }
		@Override public void onError(Throwable throwable) {}
		@Override public void onComplete() {}
		public String getBody() { return sb.toString(); }
	}

	private static class MockHttpResponse<T> implements HttpResponse<T> {
		private final int statusCode;
		private final T body;
		private final HttpRequest request;

		public MockHttpResponse(int statusCode, T body, HttpRequest request) {
			this.statusCode = statusCode;
			this.body = body;
			this.request = request;
		}

		@Override public int statusCode() { return statusCode; }
		@Override public HttpRequest request() { return request; }
		@Override public Optional<HttpResponse<T>> previousResponse() { return Optional.empty(); }
		@Override public HttpHeaders headers() { return HttpHeaders.of(Map.of(), (k, v) -> true); }
		@Override public T body() { return body; }
		@Override public Optional<SSLSession> sslSession() { return Optional.empty(); }
		@Override public URI uri() { return request.uri(); }
		@Override public HttpClient.Version version() { return HttpClient.Version.HTTP_2; }
	}
}
