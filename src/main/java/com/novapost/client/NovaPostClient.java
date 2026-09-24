package com.novapost.client;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novapost.model.*;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

public class NovaPostClient{

	public static final String DEFAULT_API_URL = "https://api.novaposhta.ua/v2.0/json/";
	private static final String MODEL_ADDRESS_GENERAL = "AddressGeneral";
	private static final String MODEL_ADDRESS = "Address";
	private static final String MODEL_INTERNET_DOCUMENT = "InternetDocument";

	private final String apiKey;
	private final String apiUrl;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;

	public NovaPostClient(String apiKey){
		this(apiKey, DEFAULT_API_URL, HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(15))
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build(), createDefaultMapper());
	}

	public NovaPostClient(String apiKey, String apiUrl, HttpClient httpClient, ObjectMapper objectMapper){
		this.apiKey = apiKey;
		this.apiUrl = apiUrl;
		this.httpClient = httpClient;
		this.objectMapper = objectMapper;
	}

	public static ObjectMapper createDefaultMapper(){
		return new ObjectMapper()
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule());
	}

	public NpResponse<Settlement> getSettlements(SettlementFilter filter) throws IOException, InterruptedException{
		return execute(MODEL_ADDRESS_GENERAL, "getSettlements", filter, Settlement.class);
	}

	public NpResponse<Warehouse> getWarehouses(WarehouseFilter filter) throws IOException, InterruptedException{
		return execute(MODEL_ADDRESS_GENERAL, "getWarehouses", filter, Warehouse.class);
	}

	public NpResponse<SearchSettlementItem> searchSettlements(SearchSettlementFilter filter) throws IOException, InterruptedException{
		return execute(MODEL_ADDRESS, "searchSettlements", filter, SearchSettlementItem.class);
	}

	public NpResponse<SearchSettlementItem> searchSettlements(String cityName, int page, int limit) throws IOException, InterruptedException{
		return searchSettlements(SearchSettlementFilter.of(cityName, page, limit));
	}

	public NpResponse<SettlementStreet> searchSettlementStreets(SettlementStreetFilter filter) throws IOException, InterruptedException{
		return execute(MODEL_ADDRESS, "searchSettlementStreets", filter, SettlementStreet.class);
	}

	public NpResponse<InternetDocumentResponse> saveInternetDocument(InternetDocumentSaveRequest request) throws IOException, InterruptedException{
		return execute(MODEL_INTERNET_DOCUMENT, "save", request, InternetDocumentResponse.class);
	}

	public NpResponse<InternetDocumentResponse> updateInternetDocument(InternetDocumentSaveRequest request) throws IOException, InterruptedException{
		return execute(MODEL_INTERNET_DOCUMENT, "update", request, InternetDocumentResponse.class);
	}

	public NpResponse<InternetDocumentListItem> getInternetDocumentList(InternetDocumentListFilter filter) throws IOException, InterruptedException{
		return execute(MODEL_INTERNET_DOCUMENT, "getDocumentList", filter, InternetDocumentListItem.class);
	}

	public NpResponse<DocumentPriceResponse> getInternetDocumentPrice(DocumentPriceRequest request) throws IOException, InterruptedException{
		return execute(MODEL_INTERNET_DOCUMENT, "getDocumentPrice", request, DocumentPriceResponse.class);
	}

	public NpResponse<DocumentDeliveryDateResponse> getInternetDocumentDeliveryDate(DocumentDeliveryDateRequest request) throws IOException, InterruptedException{
		return execute(MODEL_INTERNET_DOCUMENT, "getDocumentDeliveryDate", request, DocumentDeliveryDateResponse.class);
	}

	public NpResponse<InternetDocumentDeleteResponse> deleteInternetDocument(InternetDocumentDeleteRequest request) throws IOException, InterruptedException{
		return execute(MODEL_INTERNET_DOCUMENT, "delete", request, InternetDocumentDeleteResponse.class);
	}

	public NpResponse<InternetDocumentDeleteResponse> deleteInternetDocument(List<String> documentRefs) throws IOException, InterruptedException{
		return deleteInternetDocument(InternetDocumentDeleteRequest.of(documentRefs));
	}

	public byte[] printMarking(List<String> ttns, PrintFormat format) throws IOException, InterruptedException{
		return downloadMarkingPdf(this.apiKey, ttns, format, this.httpClient);
	}

	public byte[] printMarking(String ttn, PrintFormat format) throws IOException, InterruptedException{
		return printMarking(List.of(ttn), format);
	}

	public byte[] printMarkingZebra(String ttn) throws IOException, InterruptedException{
		return printMarking(ttn, PrintFormat.ZEBRA);
	}

	public byte[] printMarkingA4(String ttn) throws IOException, InterruptedException{
		return printMarking(ttn, PrintFormat.A4);
	}

	public byte[] printWaybill(String ttn) throws IOException, InterruptedException{
		return printMarking(ttn, PrintFormat.WAYBILL_A4);
	}

	public byte[] printScanSheet(String scanSheetRef) throws IOException, InterruptedException{
		return printMarking(scanSheetRef, PrintFormat.SCAN_SHEET);
	}

	public PrintableDocument fetchPrintableDocument(String ttn, PrintFormat format) throws IOException, InterruptedException{
		return fetchPrintableDocument(List.of(ttn), format);
	}

	public PrintableDocument fetchPrintableDocument(List<String> ttns, PrintFormat format) throws IOException, InterruptedException{
		byte[] pdfBytes = printMarking(ttns, format);
		return PrintableDocument.of(pdfBytes, format, ttns);
	}

	public static byte[] downloadMarkingPdf(String apiKey, List<String> ttns, PrintFormat format) throws IOException, InterruptedException{
		HttpClient defaultClient = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(15))
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build();
		return downloadMarkingPdf(apiKey, ttns, format, defaultClient);
	}

	public static byte[] downloadMarkingPdf(String apiKey, List<String> ttns, PrintFormat format, HttpClient client) throws IOException, InterruptedException{
		if(format == null){
			format = PrintFormat.ZEBRA;
		}
		String targetUrl = format.buildUrl(apiKey, ttns);

		int maxRetries = 5;
		long backoffMs = 1500;

		for(int attempt = 1; attempt <= maxRetries; attempt++){
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(targetUrl))
					.timeout(Duration.ofSeconds(30))
					.header("Accept", "application/pdf, application/octet-stream, */*")
					.GET()
					.build();

			HttpResponse<byte[]> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofByteArray());

			if(response.statusCode() == 429){
				if(attempt == maxRetries){
					throw new IOException("HTTP 429 rate limit exceeded downloading marking PDF after " + maxRetries + " attempts");
				}
				Thread.sleep(backoffMs);
				backoffMs *= 2;
				continue;
			}

			if(response.statusCode() < 200 || response.statusCode() >= 300){
				String errorBody = response.body() != null ? new String(response.body()) : "";
				throw new IOException("HTTP error from Nova Post print service: status " + response.statusCode() + ", body: " + errorBody);
			}

			byte[] body = response.body();
			if(body == null || body.length == 0){
				throw new IOException("Received empty PDF response from Nova Post print service for TTN: " + ttns);
			}

			return body;
		}

		throw new IOException("Failed to download PDF marking after " + maxRetries + " attempts");
	}

	public <T, R> NpResponse<R> execute(String modelName, String calledMethod, T properties, Class<R> itemClass)
			throws IOException, InterruptedException{
		NpRequest<T> request = new NpRequest<>(apiKey, modelName, calledMethod, properties);
		String requestJson = objectMapper.writeValueAsString(request);
		JavaType responseType = objectMapper.getTypeFactory()
				.constructParametricType(NpResponse.class, itemClass);

		int maxRetries = 5;
		long backoffMs = 1500;

		for(int attempt = 1; attempt <= maxRetries; attempt++){
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(apiUrl))
					.timeout(Duration.ofSeconds(30))
					.header("Content-Type", "application/json; charset=UTF-8")
					.header("Accept", "application/json")
					.POST(HttpRequest.BodyPublishers.ofString(requestJson))
					.build();

			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			if(response.statusCode() == 429){
				if(attempt == maxRetries){
					throw new IOException("HTTP 429 rate limit exceeded after " + maxRetries + " attempts");
				}
				System.out.println("Rate limit (HTTP 429) hit for " + calledMethod + ". Backing off for " + backoffMs + "ms (attempt " + attempt + "/" + maxRetries + ")...");
				Thread.sleep(backoffMs);
				backoffMs *= 2;
				continue;
			}

			if(response.statusCode() < 200 || response.statusCode() >= 300){
				throw new IOException("HTTP error from Nova Post API: status " + response.statusCode() + ", body: " + response.body());
			}

			NpResponse<R> npResponse = objectMapper.readValue(response.body(), responseType);

			boolean isRateLimitError = npResponse.errors() != null && npResponse.errors().stream()
					.anyMatch(err -> err != null && (err.toLowerCase().contains("to many requests") || err.toLowerCase().contains("too many requests")));

			if(isRateLimitError){
				if(attempt == maxRetries){
					return npResponse;
				}
				System.out.println("Nova Post rate limit [Too many requests] hit for " + calledMethod + ". Backing off for " + backoffMs + "ms (attempt " + attempt + "/" + maxRetries + ")...");
				Thread.sleep(backoffMs);
				backoffMs *= 2;
				continue;
			}

			return npResponse;
		}

		throw new IOException("Failed to execute request after " + maxRetries + " attempts");
	}

	public String getApiKey(){
		return apiKey;
	}

	public String getApiUrl(){
		return apiUrl;
	}

	public ObjectMapper getObjectMapper(){
		return objectMapper;
	}
}
