package com.novapost.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.novapost.client.NovaPostClient;
import com.novapost.config.AppConfig;
import com.novapost.model.*;
import com.novapost.projection.ModelProjector;
import com.novapost.service.WaybillFetcherService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class NovaPostController{

	private final NovaPostClient client;
	private final ModelProjector projector;

	public NovaPostController(NovaPostClient client){
		this(client, new ModelProjector(client != null ? client.getObjectMapper() : NovaPostClient.createDefaultMapper()));
	}

	public NovaPostController(NovaPostClient client, ModelProjector projector){
		if(client == null){
			throw new IllegalArgumentException("NovaPostClient must not be null");
		}
		this.client = client;
		this.projector = projector != null ? projector : new ModelProjector(client.getObjectMapper());
	}

	public static NovaPostController create(String apiKey){
		return new NovaPostController(new NovaPostClient(apiKey));
	}

	public static NovaPostController create(AppConfig config){
		return new NovaPostController(new NovaPostClient(
				config.apiKey(),
				config.apiUrl(),
				java.net.http.HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(15)).build(),
				NovaPostClient.createDefaultMapper()
		));
	}

	public List<SearchSettlementItem.SearchSettlementAddress> searchSettlements(String query){
		return searchSettlements(query, 1, 20);
	}

	public List<SearchSettlementItem.SearchSettlementAddress> searchSettlements(String query, int page, int limit){
		NpResponse<SearchSettlementItem> response = executeCall(() -> client.searchSettlements(query, page, limit), "searchSettlements");
		if(response.data() != null && !response.data().isEmpty() && response.data().getFirst().addresses() != null){
			return response.data().getFirst().addresses();
		}
		return List.of();
	}

	public List<Map<String, Object>> searchSettlements(String query, Map<String, Object> targetFields){
		return searchSettlements(query, 1, 20, targetFields);
	}

	public List<Map<String, Object>> searchSettlements(String query, int page, int limit, Map<String, Object> targetFields){
		JsonNode addressesNode = fetchSearchSettlementAddressesNode(query, page, limit);
		return projector.projectJsonArrayToMap(addressesNode, targetFields);
	}

	public <T> List<T> searchSettlements(String query, Class<T> targetType){
		return searchSettlements(query, 1, 20, targetType);
	}

	public <T> List<T> searchSettlements(String query, int page, int limit, Class<T> targetType){
		JsonNode addressesNode = fetchSearchSettlementAddressesNode(query, page, limit);
		return projector.projectJsonArrayToClass(addressesNode, targetType);
	}

	public List<Settlement> getSettlements(String query, int page, int limit){
		SettlementFilter filter = SettlementFilter.byString(query, page, limit);
		return unwrapData(() -> client.getSettlements(filter), "getSettlements");
	}

	public List<Map<String, Object>> getSettlements(String query, int page, int limit, Map<String, Object> targetFields){
		SettlementFilter filter = SettlementFilter.byString(query, page, limit);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS_GENERAL, "getSettlements", filter), "getSettlements");
		return projector.projectJsonArrayToMap(dataNode, targetFields);
	}

	public <T> List<T> getSettlements(String query, int page, int limit, Class<T> targetType){
		SettlementFilter filter = SettlementFilter.byString(query, page, limit);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS_GENERAL, "getSettlements", filter), "getSettlements");
		return projector.projectJsonArrayToClass(dataNode, targetType);
	}

	public List<Warehouse> getWarehouses(String cityRef){
		return getWarehouses(cityRef, 1, 100);
	}

	public List<Warehouse> getWarehouses(String cityRef, int page, int limit){
		WarehouseFilter filter = WarehouseFilter.byCityRef(cityRef, page, limit);
		return unwrapData(() -> client.getWarehouses(filter), "getWarehouses");
	}

	public List<Map<String, Object>> getWarehouses(String cityRef, Map<String, Object> targetFields){
		return getWarehouses(cityRef, 1, 100, targetFields);
	}

	public List<Map<String, Object>> getWarehouses(String cityRef, int page, int limit, Map<String, Object> targetFields){
		WarehouseFilter filter = WarehouseFilter.byCityRef(cityRef, page, limit);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS_GENERAL, "getWarehouses", filter), "getWarehouses");
		return projector.projectJsonArrayToMap(dataNode, targetFields);
	}

	public <T> List<T> getWarehouses(String cityRef, Class<T> targetType){
		return getWarehouses(cityRef, 1, 100, targetType);
	}

	public <T> List<T> getWarehouses(String cityRef, int page, int limit, Class<T> targetType){
		WarehouseFilter filter = WarehouseFilter.byCityRef(cityRef, page, limit);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS_GENERAL, "getWarehouses", filter), "getWarehouses");
		return projector.projectJsonArrayToClass(dataNode, targetType);
	}

	public List<Warehouse> searchWarehouses(String cityRef, String searchString){
		WarehouseFilter filter = new WarehouseFilter(cityRef, null, null, null, searchString, null, 1, 50, null);
		return unwrapData(() -> client.getWarehouses(filter), "getWarehouses");
	}

	public List<Map<String, Object>> searchWarehouses(String cityRef, String searchString, Map<String, Object> targetFields){
		WarehouseFilter filter = new WarehouseFilter(cityRef, null, null, null, searchString, null, 1, 50, null);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS_GENERAL, "getWarehouses", filter), "getWarehouses");
		return projector.projectJsonArrayToMap(dataNode, targetFields);
	}

	public <T> List<T> searchWarehouses(String cityRef, String searchString, Class<T> targetType){
		WarehouseFilter filter = new WarehouseFilter(cityRef, null, null, null, searchString, null, 1, 50, null);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS_GENERAL, "getWarehouses", filter), "getWarehouses");
		return projector.projectJsonArrayToClass(dataNode, targetType);
	}

	public List<SettlementStreet> searchStreets(String settlementRef, String streetName){
		SettlementStreetFilter filter = SettlementStreetFilter.of(settlementRef, streetName);
		return unwrapData(() -> client.searchSettlementStreets(filter), "searchSettlementStreets");
	}

	public List<Map<String, Object>> searchStreets(String settlementRef, String streetName, Map<String, Object> targetFields){
		SettlementStreetFilter filter = SettlementStreetFilter.of(settlementRef, streetName);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS, "searchSettlementStreets", filter), "searchSettlementStreets");
		return projector.projectJsonArrayToMap(dataNode, targetFields);
	}

	public <T> List<T> searchStreets(String settlementRef, String streetName, Class<T> targetType){
		SettlementStreetFilter filter = SettlementStreetFilter.of(settlementRef, streetName);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS, "searchSettlementStreets", filter), "searchSettlementStreets");
		return projector.projectJsonArrayToClass(dataNode, targetType);
	}

	public InternetDocumentResponse createWaybill(InternetDocumentSaveRequest request){
		List<InternetDocumentResponse> list = unwrapData(() -> client.saveInternetDocument(request), "saveInternetDocument");
		if(list.isEmpty()){
			throw new NovaPostApiException("Waybill creation succeeded with empty data array");
		}
		return list.getFirst();
	}

	public InternetDocumentResponse updateWaybill(InternetDocumentSaveRequest request){
		List<InternetDocumentResponse> list = unwrapData(() -> client.updateInternetDocument(request), "updateInternetDocument");
		if(list.isEmpty()){
			throw new NovaPostApiException("Waybill update succeeded with empty data array");
		}
		return list.getFirst();
	}

	public List<InternetDocumentListItem> getWaybills(LocalDate from, LocalDate to){
		return getWaybills(from, to, 1, 50);
	}

	public List<InternetDocumentListItem> getWaybills(LocalDate from, LocalDate to, int page, int limit){
		InternetDocumentListFilter filter = InternetDocumentListFilter.byDateRange(from, to, page, limit);
		return unwrapData(() -> client.getInternetDocumentList(filter), "getInternetDocumentList");
	}

	public List<Map<String, Object>> getWaybills(LocalDate from, LocalDate to, Map<String, Object> targetFields){
		return getWaybills(from, to, 1, 50, targetFields);
	}

	public List<Map<String, Object>> getWaybills(LocalDate from, LocalDate to, int page, int limit, Map<String, Object> targetFields){
		InternetDocumentListFilter filter = InternetDocumentListFilter.byDateRange(from, to, page, limit);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_INTERNET_DOCUMENT, "getDocumentList", filter), "getInternetDocumentList");
		return projector.projectJsonArrayToMap(dataNode, targetFields);
	}

	public <T> List<T> getWaybills(LocalDate from, LocalDate to, Class<T> targetType){
		return getWaybills(from, to, 1, 50, targetType);
	}

	public <T> List<T> getWaybills(LocalDate from, LocalDate to, int page, int limit, Class<T> targetType){
		InternetDocumentListFilter filter = InternetDocumentListFilter.byDateRange(from, to, page, limit);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_INTERNET_DOCUMENT, "getDocumentList", filter), "getInternetDocumentList");
		return projector.projectJsonArrayToClass(dataNode, targetType);
	}

	public List<InternetDocumentListItem> getRecentWaybills(int days){
		return getWaybills(LocalDate.now().minusDays(Math.max(1, days)), LocalDate.now());
	}

	public List<Map<String, Object>> getRecentWaybills(int days, Map<String, Object> targetFields){
		return getWaybills(LocalDate.now().minusDays(Math.max(1, days)), LocalDate.now(), targetFields);
	}

	public <T> List<T> getRecentWaybills(int days, Class<T> targetType){
		return getWaybills(LocalDate.now().minusDays(Math.max(1, days)), LocalDate.now(), targetType);
	}

	public List<InternetDocumentListItem> fetchAllDocuments(LocalDate from, LocalDate to){
		return new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to);
	}

	public <T> List<T> fetchAllDocuments(LocalDate from, LocalDate to, Class<T> targetType){
		return new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to, targetType);
	}

	public List<Map<String, Object>> fetchAllDocuments(LocalDate from, LocalDate to, Map<String, Object> targetFields){
		return new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to, targetFields);
	}

	public void fetchAllDocuments(LocalDate from, LocalDate to, Consumer<List<InternetDocumentListItem>> batchConsumer){
		new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to, batchConsumer);
	}

	public <T> void fetchAllDocuments(LocalDate from, LocalDate to, Class<T> targetType, Consumer<List<T>> batchConsumer){
		new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to, targetType, batchConsumer);
	}

	public void fetchAllDocuments(LocalDate from, LocalDate to, Map<String, Object> targetFields, Consumer<List<Map<String, Object>>> batchConsumer){
		new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to, targetFields, batchConsumer);
	}

	public double calculatePrice(String citySender, String cityRecipient, double weightKg, double declaredCost){
		return calculatePrice(citySender, cityRecipient, weightKg, declaredCost, "WarehouseWarehouse", "Parcel");
	}

	public double calculatePrice(String citySender, String cityRecipient, double weightKg, double declaredCost,
	                             String serviceType, String cargoType){
		DocumentPriceRequest req = new DocumentPriceRequest(
				citySender, cityRecipient, String.valueOf(weightKg),
				serviceType, String.valueOf(declaredCost), cargoType, "1"
		);
		List<DocumentPriceResponse> list = unwrapData(() -> client.getInternetDocumentPrice(req), "getInternetDocumentPrice");
		if(list.isEmpty() || list.getFirst().cost() == null){
			throw new NovaPostApiException("Failed to calculate shipping price: empty response");
		}
		return list.getFirst().cost();
	}

	public Optional<LocalDateTime> estimateDeliveryDate(String citySender, String cityRecipient){
		return estimateDeliveryDate(citySender, cityRecipient, "WarehouseWarehouse", LocalDate.now());
	}

	public Optional<LocalDateTime> estimateDeliveryDate(String citySender, String cityRecipient,
	                                                    String serviceType, LocalDate departureDate){
		DocumentDeliveryDateRequest req = new DocumentDeliveryDateRequest(departureDate, serviceType, citySender, cityRecipient);
		List<DocumentDeliveryDateResponse> list = unwrapData(() -> client.getInternetDocumentDeliveryDate(req), "getInternetDocumentDeliveryDate");
		if(list.isEmpty()){
			return Optional.empty();
		}
		return Optional.ofNullable(list.getFirst().getDeliveryDateTime());
	}

	public boolean deleteWaybill(String documentRef){
		List<InternetDocumentDeleteResponse> list = unwrapData(() -> client.deleteInternetDocument(List.of(documentRef)), "deleteInternetDocument");
		return !list.isEmpty();
	}

	public byte[] printMarking(String ttn, PrintFormat format){
		try{
			return client.printMarking(ttn, format);
		}
		catch(IOException | InterruptedException e){
			if(e instanceof InterruptedException){
				Thread.currentThread().interrupt();
			}
			throw new NovaPostApiException("Failed to generate PDF marking for TTN " + ttn + ": " + e.getMessage(), e);
		}
	}

	public byte[] printMarking(List<String> ttns, PrintFormat format){
		try{
			return client.printMarking(ttns, format);
		}
		catch(IOException | InterruptedException e){
			if(e instanceof InterruptedException){
				Thread.currentThread().interrupt();
			}
			throw new NovaPostApiException("Failed to generate PDF marking for TTNs: " + e.getMessage(), e);
		}
	}

	public byte[] printMarking(String ttn){
		return printMarkingZebra(ttn);
	}

	public byte[] printMarkingZebra(String ttn){
		return printMarking(ttn, PrintFormat.ZEBRA);
	}

	public byte[] printMarkingA4(String ttn){
		return printMarking(ttn, PrintFormat.A4);
	}

	public static byte[] printMarking(String apiKey, String ttn, PrintFormat format){
		return printMarking(apiKey, List.of(ttn), format);
	}

	public static byte[] printMarking(String apiKey, List<String> ttns, PrintFormat format){
		try{
			return NovaPostClient.downloadMarkingPdf(apiKey, ttns, format);
		}
		catch(IOException | InterruptedException e){
			if(e instanceof InterruptedException){
				Thread.currentThread().interrupt();
			}
			throw new NovaPostApiException("Failed to generate PDF marking for TTNs: " + e.getMessage(), e);
		}
	}

	public static byte[] printMarking(String apiKey, String ttn){
		return printMarkingZebra(apiKey, ttn);
	}

	public static byte[] printMarkingZebra(String apiKey, String ttn){
		return printMarking(apiKey, ttn, PrintFormat.ZEBRA);
	}

	public static byte[] printMarkingA4(String apiKey, String ttn){
		return printMarking(apiKey, ttn, PrintFormat.A4);
	}

	public static byte[] printWaybill(String apiKey, String ttn){
		return printMarking(apiKey, ttn, PrintFormat.WAYBILL_A4);
	}

	public static byte[] printScanSheet(String apiKey, String scanSheetRef){
		return printMarking(apiKey, scanSheetRef, PrintFormat.SCAN_SHEET);
	}

	public byte[] printWaybill(String ttn){
		return printMarking(ttn, PrintFormat.WAYBILL_A4);
	}

	public byte[] printScanSheet(String scanSheetRef){
		return printMarking(scanSheetRef, PrintFormat.SCAN_SHEET);
	}

	public PrintableDocument getPrintableDocument(String ttn, PrintFormat format){
		try{
			return client.fetchPrintableDocument(ttn, format);
		}
		catch(IOException | InterruptedException e){
			if(e instanceof InterruptedException){
				Thread.currentThread().interrupt();
			}
			throw new NovaPostApiException("Failed to fetch printable document for TTN " + ttn + ": " + e.getMessage(), e);
		}
	}

	public PrintableDocument getPrintableDocument(List<String> ttns, PrintFormat format){
		try{
			return client.fetchPrintableDocument(ttns, format);
		}
		catch(IOException | InterruptedException e){
			if(e instanceof InterruptedException){
				Thread.currentThread().interrupt();
			}
			throw new NovaPostApiException("Failed to fetch printable document for TTNs: " + e.getMessage(), e);
		}
	}

	public PrintableDocument getPrintableDocument(String ttn){
		return getPrintableZebra(ttn);
	}

	public PrintableDocument getPrintableZebra(String ttn){
		return getPrintableDocument(ttn, PrintFormat.ZEBRA);
	}

	public PrintableDocument getPrintableA4(String ttn){
		return getPrintableDocument(ttn, PrintFormat.A4);
	}

	public PrintableDocument getPrintableWaybill(String ttn){
		return getPrintableDocument(ttn, PrintFormat.WAYBILL_A4);
	}

	public PrintableDocument getPrintableScanSheet(String scanSheetRef){
		return getPrintableDocument(scanSheetRef, PrintFormat.SCAN_SHEET);
	}

	public static PrintableDocument getPrintableDocument(String apiKey, String ttn, PrintFormat format){
		return getPrintableDocument(apiKey, List.of(ttn), format);
	}

	public static PrintableDocument getPrintableDocument(String apiKey, List<String> ttns, PrintFormat format){
		byte[] pdfBytes = printMarking(apiKey, ttns, format);
		return PrintableDocument.of(pdfBytes, format, ttns);
	}

	public static PrintableDocument getPrintableZebra(String apiKey, String ttn){
		return getPrintableDocument(apiKey, ttn, PrintFormat.ZEBRA);
	}

	public static PrintableDocument getPrintableA4(String apiKey, String ttn){
		return getPrintableDocument(apiKey, ttn, PrintFormat.A4);
	}

	public static PrintableDocument getPrintableWaybill(String apiKey, String ttn){
		return getPrintableDocument(apiKey, ttn, PrintFormat.WAYBILL_A4);
	}

	public static PrintableDocument getPrintableScanSheet(String apiKey, String scanSheetRef){
		return getPrintableDocument(apiKey, scanSheetRef, PrintFormat.SCAN_SHEET);
	}

	public NovaPostClient getClient(){
		return client;
	}

	public ModelProjector getProjector(){
		return projector;
	}

	private <T> List<T> unwrapData(ApiSupplier<NpResponse<T>> call, String operationName){
		NpResponse<T> response = executeCall(call, operationName);
		return response.data() != null ? response.data() : List.of();
	}

	private <T> NpResponse<T> executeCall(ApiSupplier<NpResponse<T>> call, String operationName){
		try{
			NpResponse<T> response = call.get();
			if(response.hasErrors()){
				throw new NovaPostApiException("Nova Post API error in " + operationName, response.errors());
			}
			return response;
		}
		catch(IOException | InterruptedException e){
			if(e instanceof InterruptedException){
				Thread.currentThread().interrupt();
			}
			throw new NovaPostApiException("Communication failure during " + operationName + ": " + e.getMessage(), e);
		}
	}

	private JsonNode executeCallForRoot(ApiSupplier<JsonNode> call, String operationName){
		try{
			JsonNode root = call.get();
			if(root == null || root.isMissingNode()){
				return MissingNode.getInstance();
			}
			boolean success = root.path("success").asBoolean(true);
			JsonNode errorsNode = root.path("errors");
			if(!success || (errorsNode.isArray() && !errorsNode.isEmpty())){
				List<String> errors = new ArrayList<>();
				if(errorsNode.isArray()){
					for(JsonNode err : errorsNode){
						errors.add(err.asText());
					}
				}
				if(!errors.isEmpty()){
					throw new NovaPostApiException("Nova Post API error in " + operationName, errors);
				}
			}
			return root;
		}
		catch(IOException | InterruptedException e){
			if(e instanceof InterruptedException){
				Thread.currentThread().interrupt();
			}
			throw new NovaPostApiException("Communication failure during " + operationName + ": " + e.getMessage(), e);
		}
	}

	private JsonNode executeCallForData(ApiSupplier<JsonNode> call, String operationName){
		JsonNode root = executeCallForRoot(call, operationName);
		return root.path("data");
	}

	private JsonNode fetchSearchSettlementAddressesNode(String query, int page, int limit){
		SearchSettlementFilter filter = SearchSettlementFilter.of(query, page, limit);
		JsonNode root = executeCallForRoot(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS, "searchSettlements", filter), "searchSettlements");
		JsonNode data = root.path("data");
		if(data.isArray() && !data.isEmpty()){
			JsonNode addresses = data.get(0).path("Addresses");
			if(addresses.isArray()){
				return addresses;
			}
		}
		return MissingNode.getInstance();
	}

	@FunctionalInterface
	public interface ApiSupplier<T>{

		T get() throws IOException, InterruptedException;
	}
}
