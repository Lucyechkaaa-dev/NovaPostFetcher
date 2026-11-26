package com.novapost.client;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novapost.client.api.*;
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
	public static final String MODEL_ADDRESS_GENERAL = "AddressGeneral";
	public static final String MODEL_ADDRESS = "Address";
	public static final String MODEL_INTERNET_DOCUMENT = "InternetDocument";
	public static final String MODEL_TRACKING_DOCUMENT = "TrackingDocument";

	/** The documented maximum number of shipments per tracking request. */
	public static final int TRACKING_BATCH_SIZE = 100;

	private final String apiKey;
	private final String apiUrl;
	private final HttpClient httpClient;
	private final ObjectMapper objectMapper;
	private final NpModelStyle modelStyle;

	public NovaPostClient(String apiKey){
		this(apiKey, DEFAULT_API_URL, HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(15))
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build(), createDefaultMapper());
	}

	public NovaPostClient(String apiKey, String apiUrl, HttpClient httpClient, ObjectMapper objectMapper){
		this(apiKey, apiUrl, httpClient, objectMapper, NpModelStyle.LEGACY);
	}

	public NovaPostClient(String apiKey, String apiUrl, HttpClient httpClient, ObjectMapper objectMapper, NpModelStyle modelStyle){
		this.apiKey = apiKey;
		this.apiUrl = apiUrl;
		this.httpClient = httpClient;
		this.objectMapper = objectMapper;
		this.modelStyle = modelStyle != null ? modelStyle : NpModelStyle.LEGACY;
	}

	/** The same client (shared HTTP client and mapper) sending the other style of model names. */
	public NovaPostClient withModelStyle(NpModelStyle style){
		return new NovaPostClient(apiKey, apiUrl, httpClient, objectMapper, style);
	}

	public NpModelStyle getModelStyle(){
		return modelStyle;
	}

	/** Applies the {@link NpModelStyle}: in the documented style a legacy name such as {@code InternetDocument} becomes {@code InternetDocumentGeneral}. */
	public String resolveModelName(String modelName){
		return modelStyle == NpModelStyle.DOCUMENTED ? NpModel.documentedName(modelName) : modelName;
	}

	/**
	 * The mapper of the client. A value that cannot be read as the type of its field (a date in an unknown format, a number that is not one)
	 * is read as {@code null} and reported once in the log, so one odd value never fails a whole answer ({@link NpLenientValues}).
	 * Call {@code clearProblemHandlers()} on the result for a strict mapper.
	 */
	public static ObjectMapper createDefaultMapper(){
		return new ObjectMapper()
				.registerModule(new Jdk8Module())
				.registerModule(new JavaTimeModule())
				.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
				.addHandler(new com.novapost.model.support.NpLenientValues());
	}

	/** Prepares a call; nothing is sent until a terminal method of {@link NpCall} runs. */
	public <R> NpCall<R> call(NpModel model, String method, Object properties, Class<R> type){
		return new NpCall<>(this, model, method, properties, type);
	}

	public AddressApi address(){
		return new AddressApi(this);
	}

	public CommonApi common(){
		return new CommonApi(this);
	}

	public CounterpartyApi counterparty(){
		return new CounterpartyApi(this);
	}

	public ContactPersonApi contactPerson(){
		return new ContactPersonApi(this);
	}

	public InternetDocumentApi internetDocument(){
		return new InternetDocumentApi(this);
	}

	public ScanSheetApi scanSheet(){
		return new ScanSheetApi(this);
	}

	public AdditionalServiceApi additionalService(){
		return new AdditionalServiceApi(this);
	}

	public CarCallApi courier(){
		return new CarCallApi(this);
	}

	public TrackingApi tracking(){
		return new TrackingApi(this);
	}

	public PrintApi print(){
		return new PrintApi(this);
	}

	public NpResponse<Settlement> getSettlements(SettlementFilter filter) throws IOException, InterruptedException{
		return execute(MODEL_ADDRESS_GENERAL, "getSettlements", filter, Settlement.class);
	}

	public NpResponse<Warehouse> getWarehouses(WarehouseFilter filter) throws IOException, InterruptedException{
		return execute(MODEL_ADDRESS_GENERAL, "getWarehouses", filter, Warehouse.class);
	}

  public NpResponse<WarehouseTypes> getWarehouseTypes() throws IOException, InterruptedException{
    return execute(MODEL_ADDRESS_GENERAL, "getWarehouseTypes", null, WarehouseTypes.class);
  }

	public NpResponse<SearchSettlementItem> searchSettlements(SearchSettlementFilter filter) throws IOException, InterruptedException{
		return execute(MODEL_ADDRESS, "searchSettlements", filter, SearchSettlementItem.class);
	}

	public NpResponse<SearchSettlementItem> searchSettlements(String cityName, int page, int limit) throws IOException, InterruptedException{
		return searchSettlements(SearchSettlementFilter.of(cityName, page, limit));
	}

	/** The streets are in {@code data[0].Addresses}; see {@link SettlementStreetSearchResult#streets()}. */
	public NpResponse<SettlementStreetSearchResult> searchSettlementStreets(SettlementStreetFilter filter) throws IOException, InterruptedException{
		return execute(MODEL_ADDRESS, "searchSettlementStreets", filter, SettlementStreetSearchResult.class);
	}

	public NpResponse<Street> getStreet(StreetFilter filter) throws IOException, InterruptedException{
		return execute(MODEL_ADDRESS, "getStreet", filter, Street.class);
	}

	public NpResponse<Street> getStreet(String cityRef, int page, int limit) throws IOException, InterruptedException{
		return getStreet(StreetFilter.byCity(cityRef, page, limit));
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

	/** Tracking statuses for one TTN. */
	public List<TrackingStatus> getTracking(String ttn) throws IOException, InterruptedException{
		return getTracking(ttn, "");
	}

	/** Tracking statuses for one TTN, with the recipientRef phone when Nova Post requires it. */
	public List<TrackingStatus> getTracking(String ttn, String phone) throws IOException, InterruptedException{
		return toTrackingStatuses(getTrackingNode(ttn, phone));
	}

	/** Tracking statuses for several TTNs in one request. */
	public List<TrackingStatus> getTracking(List<String> ttns) throws IOException, InterruptedException{
		return toTrackingStatuses(getTrackingNode(ttns));
	}

	/** Raw "data" array of the tracking response, for fields that TrackingStatus does not declare. */
	public JsonNode getTrackingNode(String ttn) throws IOException, InterruptedException{
		return getTrackingNode(ttn, "");
	}

	public JsonNode getTrackingNode(String ttn, String phone) throws IOException, InterruptedException{
		return getTrackingRoot(ttn, phone).path("data");
	}

	public JsonNode getTrackingNode(List<String> ttns) throws IOException, InterruptedException{
		return getTrackingRoot(ttns).path("data");
	}

	/**
	 * The full response envelope for one TTN, so callers can look at {@code success} and {@code errors}.
	 * <p>With an API key the answer holds the complete data of the shipment, including the correct sender and recipient. Without a key
	 * (the method also works unauthorized) the data is limited: the sender and recipient data is replaced with the data that belongs to
	 * the phone number in the request. A phone number ({@code phone}) of the recipient or the sender returns extended information.
	 */
	public JsonNode getTrackingRoot(String ttn, String phone) throws IOException, InterruptedException{
		java.util.Map<String, String> doc = new java.util.HashMap<>();
		doc.put("DocumentNumber", ttn);
		doc.put("Phone", phone != null ? phone : "");
		return executeForRootNode(MODEL_TRACKING_DOCUMENT, "getStatusDocuments", java.util.Map.of("Documents", java.util.List.of(doc)));
	}

	/**
	 * The response envelope for many TTNs. The API accepts up to {@link #TRACKING_BATCH_SIZE} shipments per request, so longer
	 * lists are split and the {@code data} arrays are merged; the first failing response is returned as is.
	 */
	public JsonNode getTrackingRoot(List<String> ttns) throws IOException, InterruptedException{
		ArrayNode merged = objectMapper.createArrayNode();
		JsonNode last = null;
		for(int from = 0; from < ttns.size(); from += TRACKING_BATCH_SIZE){
			java.util.List<java.util.Map<String, String>> docs = ttns.subList(from, Math.min(ttns.size(), from + TRACKING_BATCH_SIZE)).stream()
					.map(t -> java.util.Map.of("DocumentNumber", t, "Phone", ""))
					.toList();
			last = executeForRootNode(MODEL_TRACKING_DOCUMENT, "getStatusDocuments", java.util.Map.of("Documents", docs));
			boolean failed = !last.path("success").asBoolean(true) || (last.path("errors").isArray() && !last.path("errors").isEmpty());
			if(failed){
				return last;
			}
			if(last.path("data").isArray()){
				merged.addAll((ArrayNode) last.path("data"));
			}
		}
		if(last == null){
			return objectMapper.createObjectNode().put("success", true).set("data", merged);
		}
		return ttns.size() <= TRACKING_BATCH_SIZE ? last : ((com.fasterxml.jackson.databind.node.ObjectNode) last.deepCopy()).set("data", merged);
	}

	private List<TrackingStatus> toTrackingStatuses(JsonNode dataNode){
		if(dataNode == null || !dataNode.isArray()){
			return List.of();
		}
		return objectMapper.convertValue(dataNode, new com.fasterxml.jackson.core.type.TypeReference<List<TrackingStatus>>(){});
	}

	public byte[] printMarking(List<String> ttns, PrintFormat format) throws IOException, InterruptedException{
		return downloadMarkingPdf(this.apiKey, ttns, format, this.httpClient);
	}

	/** Downloads whatever the print request describes (PDF or HTML). */
	public byte[] print(PrintRequest request) throws IOException, InterruptedException{
		return downloadPrint(request.toUrl(apiKey), request.type().isPdf(), request.ids().toString(), this.httpClient);
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
		return downloadPrint(format.buildUrl(apiKey, ttns), format.getType().isPdf(), String.valueOf(ttns), client);
	}

	/**
	 * Downloads a print URL with the same 429 backoff as the JSON calls. When a PDF is expected, a 2xx answer that does not start
	 * with {@code %PDF} (an HTML error page, for instance) is rejected instead of being returned as a "document".
	 */
	static byte[] downloadPrint(String targetUrl, boolean expectPdf, String label, HttpClient client) throws IOException, InterruptedException{
		int maxRetries = 5;
		long backoffMs = 1500;

		for(int attempt = 1; attempt <= maxRetries; attempt++){
			HttpRequest httpRequest = HttpRequest.newBuilder()
					.uri(URI.create(targetUrl))
					.timeout(Duration.ofSeconds(30))
					.header("Accept", "application/pdf, application/octet-stream, text/html, */*")
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
				throw new IOException("Received empty response from Nova Post print service for: " + label);
			}
			if(expectPdf && !(body.length >= 4 && body[0] == '%' && body[1] == 'P' && body[2] == 'D' && body[3] == 'F')){
				String head = new String(body, 0, Math.min(body.length, 160), java.nio.charset.StandardCharsets.UTF_8).replaceAll("\\s+", " ");
				throw new IOException("Nova Post print service returned something that is not a PDF for " + label + ": " + head);
			}

			return body;
		}

		throw new IOException("Failed to download PDF marking after " + maxRetries + " attempts");
	}

	public <T> JsonNode executeForRootNode(String modelName, String calledMethod, T properties)
			throws IOException, InterruptedException{
		NpRequest<T> request = new NpRequest<>(apiKey, resolveModelName(modelName), calledMethod, properties);
		String requestJson = objectMapper.writeValueAsString(request);

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

			JsonNode root = objectMapper.readTree(response.body());
			JsonNode errorsNode = root.path("errors");
			boolean isRateLimitError = false;
			if(errorsNode.isArray()){
				for(JsonNode err : errorsNode){
					String text = err.isValueNode() ? err.asText() : err.toString(); // an error may be an object keyed by the waybill
					if(text != null && (text.toLowerCase().contains("to many requests") || text.toLowerCase().contains("too many requests"))){
						isRateLimitError = true;
						break;
					}
				}
			}

			if(isRateLimitError){
				if(attempt == maxRetries){
					return root;
				}
				System.out.println("Nova Post rate limit [Too many requests] hit for " + calledMethod + ". Backing off for " + backoffMs + "ms (attempt " + attempt + "/" + maxRetries + ")...");
				Thread.sleep(backoffMs);
				backoffMs *= 2;
				continue;
			}

			return root;
		}

		throw new IOException("Failed to execute request after " + maxRetries + " attempts");
	}

	public <T> JsonNode executeForDataNode(String modelName, String calledMethod, T properties)
			throws IOException, InterruptedException{
		return executeForRootNode(modelName, calledMethod, properties).path("data");
	}

	public <T, R> NpResponse<R> execute(String modelName, String calledMethod, T properties, Class<R> itemClass)
			throws IOException, InterruptedException{
		JsonNode root = executeForRootNode(modelName, calledMethod, properties);
		JavaType responseType = objectMapper.getTypeFactory()
				.constructParametricType(NpResponse.class, itemClass);
		return objectMapper.treeToValue(root, responseType);
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
