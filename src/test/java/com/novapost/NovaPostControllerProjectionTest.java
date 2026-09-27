package com.novapost;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.novapost.client.NovaPostClient;
import com.novapost.controller.NovaPostController;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSession;

import static org.junit.jupiter.api.Assertions.*;

public class NovaPostControllerProjectionTest {

	public record SettlementSummaryDto(
			String ref,
			String description,
			@JsonProperty("AreaDescription") String area
	) {}

	public record WarehouseSummaryDto(
			String ref,
			String description,
			int number
	) {}

	public record WaybillSummaryDto(
			@JsonProperty("Ref") String ref,
			@JsonProperty("IntDocNumber") String documentNumber,
			@JsonProperty("Cost") double cost
	) {}

	@Test
	void testGetSettlementsMapAndClassProjection() {
		String jsonResponse = """
				{
				  "success": true,
				  "data": [
				    {
				      "Ref": "ref-settle-01",
				      "Description": "Kyiv",
				      "AreaDescription": "Kyivska",
				      "SettlementTypeDescription": "City"
				    }
				  ],
				  "errors": [],
				  "warnings": [],
				  "info": []
				}
				""";

		MockHttpClient mockClient = new MockHttpClient(200, jsonResponse);
		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, mockClient, NovaPostClient.createDefaultMapper());
		NovaPostController controller = new NovaPostController(client);

		Map<String, Object> targetFields = new HashMap<>();
		targetFields.put("ref", null);
		targetFields.put("description", null);

		List<Map<String, Object>> mapResults = controller.getSettlements("Kyiv", 1, 10, targetFields);
		assertEquals(1, mapResults.size());
		assertEquals("ref-settle-01", mapResults.getFirst().get("ref"));
		assertEquals("Kyiv", mapResults.getFirst().get("description"));
		assertFalse(mapResults.getFirst().containsKey("SettlementTypeDescription"));

		List<SettlementSummaryDto> dtoList = controller.getSettlements("Kyiv", 1, 10, SettlementSummaryDto.class);
		assertEquals(1, dtoList.size());
		assertEquals("ref-settle-01", dtoList.getFirst().ref());
		assertEquals("Kyiv", dtoList.getFirst().description());
		assertEquals("Kyivska", dtoList.getFirst().area());
	}

	@Test
	void testGetWarehousesMapAndClassProjection() {
		String jsonResponse = """
				{
				  "success": true,
				  "data": [
				    {
				      "Ref": "wh-ref-55",
				      "Description": "Warehouse 5",
				      "Number": "5",
				      "CityRef": "city-ref-01"
				    }
				  ],
				  "errors": [],
				  "warnings": [],
				  "info": []
				}
				""";

		MockHttpClient mockClient = new MockHttpClient(200, jsonResponse);
		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, mockClient, NovaPostClient.createDefaultMapper());
		NovaPostController controller = new NovaPostController(client);

		Map<String, Object> fields = new HashMap<>();
		fields.put("ref", null);
		fields.put("number", null);

		List<Map<String, Object>> mapResults = controller.getWarehouses("city-ref-01", fields);
		assertEquals(1, mapResults.size());
		assertEquals("wh-ref-55", mapResults.getFirst().get("ref"));
		assertEquals("5", mapResults.getFirst().get("number"));

		List<WarehouseSummaryDto> dtoList = controller.getWarehouses("city-ref-01", WarehouseSummaryDto.class);
		assertEquals(1, dtoList.size());
		assertEquals("wh-ref-55", dtoList.getFirst().ref());
		assertEquals("Warehouse 5", dtoList.getFirst().description());
		assertEquals(5, dtoList.getFirst().number());
	}

	@Test
	void testGetWaybillsMapAndClassProjection() {
		String jsonResponse = """
				{
				  "success": true,
				  "data": [
				    {
				      "Ref": "doc-ref-100",
				      "IntDocNumber": "20450000000001",
				      "Cost": "150.50"
				    }
				  ],
				  "errors": [],
				  "warnings": [],
				  "info": []
				}
				""";

		MockHttpClient mockClient = new MockHttpClient(200, jsonResponse);
		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, mockClient, NovaPostClient.createDefaultMapper());
		NovaPostController controller = new NovaPostController(client);

		Map<String, Object> fields = new HashMap<>();
		fields.put("IntDocNumber", null);
		fields.put("Cost", null);

		List<Map<String, Object>> mapResults = controller.getWaybills(LocalDate.now().minusDays(3), LocalDate.now(), fields);
		assertEquals(1, mapResults.size());
		assertEquals("20450000000001", mapResults.getFirst().get("IntDocNumber"));
		assertEquals("150.50", mapResults.getFirst().get("Cost"));

		List<WaybillSummaryDto> dtoList = controller.getWaybills(LocalDate.now().minusDays(3), LocalDate.now(), WaybillSummaryDto.class);
		assertEquals(1, dtoList.size());
		assertEquals("doc-ref-100", dtoList.getFirst().ref());
		assertEquals("20450000000001", dtoList.getFirst().documentNumber());
		assertEquals(150.50, dtoList.getFirst().cost(), 0.001);
	}

	@Test
	void testGetRecentWaybillsProjection() {
		String jsonResponse = """
				{
				  "success": true,
				  "data": [
				    {
				      "Ref": "doc-ref-200",
				      "IntDocNumber": "20450000000002",
				      "Cost": "75.00"
				    }
				  ],
				  "errors": [],
				  "warnings": [],
				  "info": []
				}
				""";

		MockHttpClient mockClient = new MockHttpClient(200, jsonResponse);
		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, mockClient, NovaPostClient.createDefaultMapper());
		NovaPostController controller = new NovaPostController(client);

		List<WaybillSummaryDto> list = controller.getRecentWaybills(7, WaybillSummaryDto.class);
		assertEquals(1, list.size());
		assertEquals("doc-ref-200", list.getFirst().ref());
		assertEquals(75.00, list.getFirst().cost(), 0.001);
	}

	public record ExtendedWaybillDto(
			String intDocNumber,
			String recipientAddressDescription,
			String customNovaPostTrackingCode
	) {}

	@Test
	void testDirectJsonProjectionWithFieldsMissingFromDomainModels() {
		String jsonResponse = """
				{
				  "success": true,
				  "data": [
				    {
				      "Ref": "doc-ref-unmapped",
				      "IntDocNumber": "20450000009999",
				      "Cost": "99.99",
				      "RecipientAddressDescription": "Khreshchatyk 10",
				      "CustomNovaPostTrackingCode": "TRACK-XYZ-999",
				      "IgnoredExtraField1": "skip",
				      "IgnoredExtraField2": 12345
				    }
				  ],
				  "errors": [],
				  "warnings": [],
				  "info": []
				}
				""";

		MockHttpClient mockClient = new MockHttpClient(200, jsonResponse);
		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, mockClient, NovaPostClient.createDefaultMapper());
		NovaPostController controller = new NovaPostController(client);

		Map<String, Object> fields = new HashMap<>();
		fields.put("RecipientAddressDescription", null);
		fields.put("CustomNovaPostTrackingCode", null);

		List<Map<String, Object>> mapResults = controller.getWaybills(LocalDate.now().minusDays(1), LocalDate.now(), fields);
		assertEquals(1, mapResults.size());
		assertEquals("Khreshchatyk 10", mapResults.getFirst().get("RecipientAddressDescription"));
		assertEquals("TRACK-XYZ-999", mapResults.getFirst().get("CustomNovaPostTrackingCode"));
		assertFalse(mapResults.getFirst().containsKey("IgnoredExtraField1"));

		List<ExtendedWaybillDto> dtoList = controller.getWaybills(LocalDate.now().minusDays(1), LocalDate.now(), ExtendedWaybillDto.class);
		assertEquals(1, dtoList.size());
		assertEquals("20450000009999", dtoList.getFirst().intDocNumber());
		assertEquals("Khreshchatyk 10", dtoList.getFirst().recipientAddressDescription());
		assertEquals("TRACK-XYZ-999", dtoList.getFirst().customNovaPostTrackingCode());
	}

	private static class MockHttpClient extends HttpClient {
		private final int statusCode;
		private final String responseBody;

		public MockHttpClient(int statusCode, String responseBody) {
			this.statusCode = statusCode;
			this.responseBody = responseBody;
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> HttpResponse<T> send(HttpRequest request, HttpResponse.BodyHandler<T> responseHandler) {
			return (HttpResponse<T>) new MockHttpResponse<>(statusCode, responseBody, request);
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
