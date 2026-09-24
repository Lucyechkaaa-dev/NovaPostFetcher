package com.novapost;

import com.novapost.client.NovaPostClient;
import com.novapost.controller.NovaPostApiException;
import com.novapost.controller.NovaPostController;
import com.novapost.model.PrintFormat;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.Authenticator;
import java.net.CookieHandler;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

import static org.junit.jupiter.api.Assertions.*;

class NovaPostPrintMarkingTest {

	@Test
	void testDownloadMarkingPdfSuccess() throws Exception {
		byte[] fakePdf = "%PDF-1.4 mock pdf content".getBytes(StandardCharsets.UTF_8);

		MockHttpClient mockClient = new MockHttpClient(200, fakePdf);
		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, mockClient, NovaPostClient.createDefaultMapper());

		byte[] resultZebra = client.printMarkingZebra("20400048799000");
		assertArrayEquals(fakePdf, resultZebra);
		assertTrue(mockClient.lastRequestUri.toString().contains("printMarking100x100"));
		assertTrue(mockClient.lastRequestUri.toString().endsWith("/zebra"));

		byte[] resultA4 = client.printMarkingA4("20400048799000");
		assertArrayEquals(fakePdf, resultA4);
		assertTrue(mockClient.lastRequestUri.toString().contains("printMarkings"));
		assertFalse(mockClient.lastRequestUri.toString().endsWith("/zebra"));
	}

	@Test
	void testControllerPrintMarkingDelegatesCorrectly() {
		byte[] fakePdf = "%PDF-1.4 controller test".getBytes(StandardCharsets.UTF_8);
		MockHttpClient mockClient = new MockHttpClient(200, fakePdf);
		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, mockClient, NovaPostClient.createDefaultMapper());
		NovaPostController controller = new NovaPostController(client);

		byte[] zebraBytes = controller.printMarkingZebra("20400048799000");
		assertArrayEquals(fakePdf, zebraBytes);

		byte[] a4Bytes = controller.printMarkingA4("20400048799000");
		assertArrayEquals(fakePdf, a4Bytes);

		byte[] defaultBytes = controller.printMarking("20400048799000");
		assertArrayEquals(fakePdf, defaultBytes);
	}

	@Test
	void testDownloadMarkingPdfHttpError() {
		MockHttpClient mockClient = new MockHttpClient(404, "Not Found".getBytes(StandardCharsets.UTF_8));
		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, mockClient, NovaPostClient.createDefaultMapper());
		NovaPostController controller = new NovaPostController(client);

		assertThrows(NovaPostApiException.class, () -> controller.printMarkingZebra("20400048799000"));
	}

	@Test
	void testDownloadMarkingPdfEmptyBody() {
		MockHttpClient mockClient = new MockHttpClient(200, new byte[0]);
		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, mockClient, NovaPostClient.createDefaultMapper());
		NovaPostController controller = new NovaPostController(client);

		assertThrows(NovaPostApiException.class, () -> controller.printMarkingA4("20400048799000"));
	}

	private static class MockHttpClient extends HttpClient {
		private final int statusCode;
		private final byte[] responseBytes;
		private URI lastRequestUri;

		public MockHttpClient(int statusCode, byte[] responseBytes) {
			this.statusCode = statusCode;
			this.responseBytes = responseBytes;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseHandler) {
			this.lastRequestUri = request.uri();
			return (HttpResponse<T>) new MockHttpResponse<>(statusCode, responseBytes, request);
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
