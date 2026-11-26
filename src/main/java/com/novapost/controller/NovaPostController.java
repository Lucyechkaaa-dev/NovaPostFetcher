package com.novapost.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.novapost.client.NovaPostClient;
import com.novapost.config.AppConfig;
import com.novapost.model.*;
import com.novapost.projection.ModelProjector;
import com.novapost.service.WaybillFetcherService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;

@SuppressWarnings("unused")
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
				NovaPostClient.createDefaultMapper(),
				config.modelStyle()
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

	public List<WarehouseTypes> getWarehouseTypes(){
		return unwrapData(client::getWarehouseTypes, "getWarehouseTypes");
	}

	public <T> List<T> getWarehouseTypes(Class<T> targetType){
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS_GENERAL, "getWarehouseTypes", null), "getWarehouseTypes");
		return projector.projectJsonArrayToClass(dataNode, targetType);
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

	/** Streets of a settlement; the API wraps them as {@code data[0].Addresses}, which is unwrapped here. */
	public List<SettlementStreet> searchStreets(String settlementRef, String streetName){
		SettlementStreetFilter filter = SettlementStreetFilter.of(settlementRef, streetName);
		List<SettlementStreetSearchResult> data = unwrapData(() -> client.searchSettlementStreets(filter), "searchSettlementStreets");
		return data.isEmpty() ? List.of() : data.getFirst().streets();
	}

	public List<Map<String, Object>> searchStreets(String settlementRef, String streetName, Map<String, Object> targetFields){
		SettlementStreetFilter filter = SettlementStreetFilter.of(settlementRef, streetName);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS, "searchSettlementStreets", filter), "searchSettlementStreets");
		return projector.projectJsonArrayToMap(streetAddresses(dataNode), targetFields);
	}

	public <T> List<T> searchStreets(String settlementRef, String streetName, Class<T> targetType){
		SettlementStreetFilter filter = SettlementStreetFilter.of(settlementRef, streetName);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS, "searchSettlementStreets", filter), "searchSettlementStreets");
		return projector.projectJsonArrayToClass(streetAddresses(dataNode), targetType);
	}

	private static JsonNode streetAddresses(JsonNode dataNode){
		if(dataNode.isArray() && !dataNode.isEmpty() && dataNode.get(0).path("Addresses").isArray()){
			return dataNode.get(0).path("Addresses");
		}
		return MissingNode.getInstance();
	}

	public List<Street> getStreets(String cityRef){
		return getStreets(cityRef, 1, 500);
	}

	public List<Street> getStreets(String cityRef, int page, int limit){
		StreetFilter filter = StreetFilter.byCity(cityRef, page, limit);
		List<Street> streets = unwrapData(() -> client.getStreet(filter), "getStreet");
		return streets.stream().map(s -> s.withCityRef(cityRef)).toList();
	}

	public List<Street> searchStreetsByCity(String cityRef, String streetName){
		return searchStreetsByCity(cityRef, streetName, 1, 100);
	}

	public List<Street> searchStreetsByCity(String cityRef, String streetName, int page, int limit){
		StreetFilter filter = StreetFilter.byCityAndName(cityRef, streetName, page, limit);
		List<Street> streets = unwrapData(() -> client.getStreet(filter), "getStreet");
		return streets.stream().map(s -> s.withCityRef(cityRef)).toList();
	}

	public List<Map<String, Object>> getStreets(String cityRef, int page, int limit, Map<String, Object> targetFields){
		StreetFilter filter = StreetFilter.byCity(cityRef, page, limit);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS, "getStreet", filter), "getStreet");
		return projector.projectJsonArrayToMap(dataNode, targetFields);
	}

	public <T> List<T> getStreets(String cityRef, int page, int limit, Class<T> targetType){
		StreetFilter filter = StreetFilter.byCity(cityRef, page, limit);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_ADDRESS, "getStreet", filter), "getStreet");
		return projector.projectJsonArrayToClass(dataNode, targetType);
	}

	public InternetDocumentResponse createWaybill(InternetDocumentSaveRequest request){
		List<InternetDocumentResponse> list = unwrapData(() -> client.saveInternetDocument(request), "saveInternetDocument");
		if(list.isEmpty()){
			throw new NovaPostApiException("Waybill creation succeeded with empty data array");
		}
		return list.getFirst();
	}

	public InternetDocumentResponse createDraft(ParticipantRouting routing){
		return new com.novapost.service.WaybillLifecycleService(this).createDraft(routing);
	}

	public InternetDocumentResponse createWaybill(ParticipantRouting routing, CargoDetails cargo){
		return new com.novapost.service.WaybillLifecycleService(this).createWaybill(routing, cargo);
	}

	/** Creates a waybill and lets the caller apply a recipe (postomat, recipient as text, backward delivery...) to the request first. */
	public InternetDocumentResponse createWaybill(ParticipantRouting routing, CargoDetails cargo, Consumer<InternetDocumentSaveRequest.Builder> customizer){
		return new com.novapost.service.WaybillLifecycleService(this).createWaybill(routing, cargo, customizer);
	}

	public InternetDocumentResponse updateWaybill(InternetDocumentSaveRequest request){
		List<InternetDocumentResponse> list = unwrapData(() -> client.updateInternetDocument(request), "updateInternetDocument");
		if(list.isEmpty()){
			throw new NovaPostApiException("Waybill update succeeded with empty data array");
		}
		return list.getFirst();
	}

	public InternetDocumentResponse updateWaybillCargo(String ttn, CargoDetails cargo){
		return new com.novapost.service.WaybillLifecycleService(this).updateCargo(ttn, cargo);
	}

	public boolean deleteWaybillByTtn(String ttn){
		return new com.novapost.service.WaybillLifecycleService(this).deleteByTtn(ttn);
	}

	public List<InternetDocumentListItem> getWaybills(LocalDateTime from, LocalDateTime to){
		return getWaybills(from, to, 1, 50);
	}

	public List<InternetDocumentListItem> getWaybills(LocalDateTime from, LocalDateTime to, int page, int limit){
		InternetDocumentListFilter filter = InternetDocumentListFilter.byDateRange(from, to, page, limit);
		return unwrapData(() -> client.getInternetDocumentList(filter), "getInternetDocumentList");
	}

	public List<Map<String, Object>> getWaybills(LocalDateTime from, LocalDateTime to, Map<String, Object> targetFields){
		return getWaybills(from, to, 1, 50, targetFields);
	}

	public List<Map<String, Object>> getWaybills(LocalDateTime from, LocalDateTime to, int page, int limit, Map<String, Object> targetFields){
		InternetDocumentListFilter filter = InternetDocumentListFilter.byDateRange(from, to, page, limit);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_INTERNET_DOCUMENT, "getDocumentList", filter), "getInternetDocumentList");
		return projector.projectJsonArrayToMap(dataNode, targetFields);
	}

	public <T> List<T> getWaybills(LocalDateTime from, LocalDateTime to, Class<T> targetType){
		return getWaybills(from, to, 1, 50, targetType);
	}

	public <T> List<T> getWaybills(LocalDateTime from, LocalDateTime to, int page, int limit, Class<T> targetType){
		InternetDocumentListFilter filter = InternetDocumentListFilter.byDateRange(from, to, page, limit);
		JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_INTERNET_DOCUMENT, "getDocumentList", filter), "getInternetDocumentList");
		return projector.projectJsonArrayToClass(dataNode, targetType);
	}

	public List<InternetDocumentListItem> getRecentWaybills(int days){
		return getWaybills(LocalDateTime.now().minusDays(Math.max(1, days)), LocalDateTime.now());
	}

	public List<Map<String, Object>> getRecentWaybills(int days, Map<String, Object> targetFields){
		return getWaybills(LocalDateTime.now().minusDays(Math.max(1, days)), LocalDateTime.now(), targetFields);
	}

	public <T> List<T> getRecentWaybills(int days, Class<T> targetType){
		return getWaybills(LocalDateTime.now().minusDays(Math.max(1, days)), LocalDateTime.now(), targetType);
	}

	public List<InternetDocumentListItem> fetchAllDocuments(LocalDateTime from, LocalDateTime to){
		return new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to);
	}

	public <T> List<T> fetchAllDocuments(LocalDateTime from, LocalDateTime to, Class<T> targetType){
		return new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to, targetType);
	}

	public List<Map<String, Object>> fetchAllDocuments(LocalDateTime from, LocalDateTime to, Map<String, Object> targetFields){
		return new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to, targetFields);
	}

	public void fetchAllDocuments(LocalDateTime from, LocalDateTime to, Consumer<List<InternetDocumentListItem>> batchConsumer){
		new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to, batchConsumer);
	}

	public <T> void fetchAllDocuments(LocalDateTime from, LocalDateTime to, Class<T> targetType, Consumer<List<T>> batchConsumer){
		new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to, targetType, batchConsumer);
	}

	public void fetchAllDocuments(LocalDateTime from, LocalDateTime to, Map<String, Object> targetFields, Consumer<List<Map<String, Object>>> batchConsumer){
		new WaybillFetcherService(client, projector, 300, 5, 1000).fetchAllDocuments(from, to, targetFields, batchConsumer);
	}

	public BigDecimal calculatePrice(String citySender, String cityRecipient, double weightKg, double declaredCost){
		return calculatePrice(citySender, cityRecipient, weightKg, declaredCost, "WarehouseWarehouse", "Parcel");
	}

	public BigDecimal calculatePrice(String citySender, String cityRecipient, double weightKg, double declaredCost,
	                             String serviceType, String cargoType){
		DocumentPriceRequest req = new DocumentPriceRequest(
				citySender, cityRecipient, BigDecimal.valueOf(weightKg),
				serviceType, BigDecimal.valueOf(declaredCost), cargoType, 1
		);
		List<DocumentPriceResponse> list = unwrapData(() -> client.getInternetDocumentPrice(req), "getInternetDocumentPrice");
		if(list.isEmpty() || list.getFirst().cost() == null){
			throw new NovaPostApiException("Failed to calculate shipping price: empty response");
		}
		return list.getFirst().cost();
	}

	public Optional<LocalDateTime> estimateDeliveryDate(String citySender, String cityRecipient){
		return estimateDeliveryDate(citySender, cityRecipient, "WarehouseWarehouse", LocalDateTime.now());
	}

	/**
	 * Delivery date estimate sending date and time ({@code yyyy-MM-dd HH:mm:ss}) as in the documentation's example, instead of the
	 * calendar date only. {@code LiveContractSmokeTest} reports which of the two forms the live API accepts.
	 */
	public Optional<LocalDateTime> estimateDeliveryDateAt(String citySender, String cityRecipient,
	                                                      String serviceType, LocalDateTime departure){
		DocumentDeliveryDateRequest req = DocumentDeliveryDateRequest.ofDateTime(departure, serviceType, citySender, cityRecipient);
		List<DocumentDeliveryDateResponse> list = unwrapData(() -> client.getInternetDocumentDeliveryDate(req), "getInternetDocumentDeliveryDate");
		return list.isEmpty() ? Optional.empty() : Optional.ofNullable(list.getFirst().getDeliveryDateTime());
	}

	/**
	 * Delivery date estimate for a calendar date. The documentation says the waybill's services (backward delivery, daily delivery and
	 * others) must be part of the request, yet it names no parameter for them; only the service type and the two cities are modelled.
	 * Other parameters can be sent through {@code client.call(NpModel.INTERNET_DOCUMENT, "getDocumentDeliveryDate", map, JsonNode.class)}.
	 */
	public Optional<LocalDateTime> estimateDeliveryDate(String citySender, String cityRecipient,
	                                                    String serviceType, LocalDateTime departureDate){
		// The delivery-date API takes a calendar date only.
		DocumentDeliveryDateRequest req = new DocumentDeliveryDateRequest(departureDate.toLocalDate(), serviceType, citySender, cityRecipient);
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

	/** Tracking statuses for one TTN. Returns an empty list if Nova Post knows no such document. */
	public List<TrackingStatus> getTracking(String ttn){
		return getTracking(ttn, "");
	}

	// The client's getTrackingNode(...) already returns the "data" array, so the controller has to go through the full
	// envelope (getTrackingRoot) to check for errors and then read "data" once; reading it from the array again yielded nothing.
	/**
	 * Tracking of one TTN. Authorized access (the client's API key) returns the complete data, including the correct sender and
	 * recipient; unauthorized access would return limited data where the sender and recipient are replaced with the data that belongs
	 * to the phone number in the request. The recipient's or sender's {@code phone} returns extended information.
	 */
	public List<TrackingStatus> getTracking(String ttn, String phone){
		return toTrackingStatuses(executeCallForData(() -> client.getTrackingRoot(ttn, phone), "getTracking"));
	}

	/** Tracking for any number of TTNs; the client splits them into requests of at most 100. */
	public List<TrackingStatus> getTracking(List<String> ttns){
		return toTrackingStatuses(executeCallForData(() -> client.getTrackingRoot(ttns), "getTracking"));
	}

	/** Raw tracking JSON, for fields that TrackingStatus does not declare. */
	public JsonNode getTrackingNode(String ttn){
		return executeCallForData(() -> client.getTrackingRoot(ttn, ""), "getTracking");
	}

	private List<TrackingStatus> toTrackingStatuses(JsonNode dataNode){
		if(dataNode == null || !dataNode.isArray()){
			return List.of();
		}
		return client.getObjectMapper().convertValue(dataNode, new com.fasterxml.jackson.core.type.TypeReference<List<TrackingStatus>>(){
		});
	}

	/**
	 * Finds a waybill by TTN created on the given day (from 00:00:00 to 23:59:59 of that date).
	 */
	public Optional<InternetDocumentListItem> findWaybillByNumber(String ttn, LocalDateTime date){
		Objects.requireNonNull(date, "date must not be null");
		LocalDateTime startOfDay = date.toLocalDate().atStartOfDay();
		LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);
		return fetchWaybillByTtn(ttn, startOfDay, endOfDay);
	}

	/**
	 * Fetches one internet document (waybill) by its TTN (IntDocNumber), searching the last 30 days.
	 */
	public Optional<InternetDocumentListItem> fetchWaybillByTtn(String ttn){
		return fetchWaybillByTtn(ttn, LocalDateTime.now().minusDays(30), LocalDateTime.now());
	}

	/**
	 * Fetches one internet document (waybill) by its TTN (IntDocNumber) within a date range.
	 * Pages through results until the TTN is found, the API returns an empty or short page,
	 * so waybills beyond the first 100 results are still found.
	 *
	 * @throws IllegalArgumentException if the TTN is blank or {@code from} is after {@code to}
	 */
	public Optional<InternetDocumentListItem> fetchWaybillByTtn(String ttn, LocalDateTime from, LocalDateTime to){
		return findWaybillNodeByTtnPaged(ttn, from, to).map(node -> {
			try{
				return client.getObjectMapper().treeToValue(node, InternetDocumentListItem.class);
			}
			catch(JsonProcessingException e){
				throw new NovaPostApiException("Failed to parse waybill " + ttn + ": " + e.getOriginalMessage(), e);
			}
		});
	}

	/**
	 * Same lookup as {@link #fetchWaybillByTtn(String, LocalDateTime, LocalDateTime)}, but returns the raw JSON node
	 * so callers can read fields the record does not declare.
	 */
	public Optional<JsonNode> fetchWaybillNodeByTtn(String ttn, LocalDateTime from, LocalDateTime to){
		return findWaybillNodeByTtnPaged(ttn, from, to);
	}

	private static final int TTN_LOOKUP_PAGE_SIZE = 100;

	private Optional<JsonNode> findWaybillNodeByTtnPaged(String ttn, LocalDateTime from, LocalDateTime to){
		if(ttn == null || ttn.isBlank()){
			throw new IllegalArgumentException("TTN must not be null or blank");
		}
		Objects.requireNonNull(from, "from must not be null");
		Objects.requireNonNull(to, "to must not be null");
		if(from.isAfter(to)){
			throw new IllegalArgumentException("from (" + from + ") must not be after to (" + to + ")");
		}
		String wanted = ttn.trim();
		String previousFirst = null;
		for(int page = 1; page <= MAX_LOOKUP_PAGES; page++){
			// Paged mode (GetFullList=0) plus the documented IntDocNumber filter: normally the match is on page 1; if the
			// server ignores the filter the scan simply continues page by page.
			InternetDocumentListFilter filter = InternetDocumentListFilter.byNumber(wanted, from, to, page, TTN_LOOKUP_PAGE_SIZE);
			JsonNode dataNode = executeCallForData(() -> client.executeForRootNode(NovaPostClient.MODEL_INTERNET_DOCUMENT, "getDocumentList", filter), "getInternetDocumentList");
			if(!dataNode.isArray() || dataNode.isEmpty()){
				return Optional.empty();
			}
			for(JsonNode item : dataNode){
				if(wanted.equalsIgnoreCase(item.path("IntDocNumber").asText())){
					return Optional.of(item);
				}
			}
			if(dataNode.size() < TTN_LOOKUP_PAGE_SIZE){
				return Optional.empty();
			}
			// The same first element twice in a row means the server is not honouring Page; give up instead of looping.
			String first = dataNode.get(0).path("IntDocNumber").asText(null);
			if(first != null && first.equals(previousFirst)){
				return Optional.empty();
			}
			previousFirst = first;
		}
		return Optional.empty();
	}

	private static final int MAX_LOOKUP_PAGES = 1000;

	/** Same lookup as {@link #fetchWaybillNodeByTtn}: it pages through the whole range instead of reading only the first 100 waybills. */
	public Optional<JsonNode> findWaybillNodeByNumber(String ttn, LocalDateTime from, LocalDateTime to){
		return findWaybillNodeByTtnPaged(ttn, from, to);
	}

	public InternetDocumentResponse updateWaybillCargoByNumber(String ttn, LocalDateTime from, LocalDateTime to, Consumer<InternetDocumentSaveRequest.Builder> cargoCustomizer){
		JsonNode doc = findWaybillNodeByNumber(ttn, from, to)
				.orElseThrow(() -> new NovaPostApiException("Waybill with TTN " + ttn + " not found in date range " + from + " to " + to));

		InternetDocumentSaveRequest.Builder builder = InternetDocumentSaveRequest.builder()
				.ref(doc.path("Ref").asText(null))
				.payerType(doc.path("PayerType").asText("Sender"))
				.paymentMethod(doc.path("PaymentMethod").asText("NonCash"))
				.dateTime(LocalDate.now())
				.cargoType(doc.path("CargoType").asText("Parcel"))
				.volumeGeneral(decimal(doc.path("VolumeGeneral")))
				.weight(decimal(doc.path("Weight")))
				.serviceType(doc.path("ServiceType").asText("WarehouseWarehouse"))
				.seatsAmount(integer(doc.path("SeatsAmount"), 1))
				.description(doc.path("Description").asText(null))
				.cost(decimal(doc.path("Cost")))
				.citySender(doc.path("CitySender").asText(null))
				.sender(doc.path("Sender").asText(null))
				.senderAddress(doc.path("SenderAddress").asText(null))
				.contactSender(doc.path("ContactSender").asText(null))
				.sendersPhone(doc.path("SendersPhone").asText(null))
				.cityRecipient(doc.path("CityRecipient").asText(null))
				.recipient(doc.path("Recipient").asText(null))
				.recipientAddress(doc.path("RecipientAddress").asText(null))
				.contactRecipient(doc.path("ContactRecipient").asText(null))
				.recipientsPhone(doc.path("RecipientsPhone").asText(null));

		if(doc.has("OptionsSeat") && doc.path("OptionsSeat").isArray()){
			List<InternetDocumentSaveRequest.OptionSeat> seats = new ArrayList<>();
			for(JsonNode seatNode : doc.path("OptionsSeat")){
				seats.add(new InternetDocumentSaveRequest.OptionSeat(
						decimal(seatNode.path("volumetricVolume")),
						decimal(seatNode.path("volumetricWidth")),
						decimal(seatNode.path("volumetricLength")),
						decimal(seatNode.path("volumetricHeight")),
						decimal(seatNode.path("weight"))
				));
			}
			if(!seats.isEmpty()){
				builder.optionsSeat(seats);
			}
		}

		if(cargoCustomizer != null){
			cargoCustomizer.accept(builder);
		}

		return updateWaybill(builder.build());
	}

	/** The decimal in a raw answer node; {@code null} for a missing, empty or unreadable value. */
	private static BigDecimal decimal(JsonNode node){
		String text = node.asText(null);
		return text == null || text.isBlank() ? null : com.novapost.model.support.NpDecimalDeserializer.parse(text);
	}

	/** The whole number in a raw answer node, or {@code fallback} when it is missing, empty or unreadable. */
	private static Integer integer(JsonNode node, Integer fallback){
		BigDecimal value = decimal(node);
		return value == null ? fallback : Integer.valueOf(value.intValue());
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

	private static final System.Logger LOGGER = System.getLogger(NovaPostController.class.getName());

	/** A successful answer can still carry warnings, for example when {@code DateTime} was cut back to "today + 3 months". */
	private static void logWarnings(String operationName, List<String> warnings){
		if(warnings != null && !warnings.isEmpty()){
			LOGGER.log(System.Logger.Level.WARNING, "Nova Post warning in {0}: {1}", operationName, String.join("; ", warnings));
		}
	}

	public com.novapost.client.api.AddressApi address(){
		return client.address();
	}

	public com.novapost.client.api.CommonApi common(){
		return client.common();
	}

	public com.novapost.client.api.CounterpartyApi counterparty(){
		return client.counterparty();
	}

	public com.novapost.client.api.ContactPersonApi contactPerson(){
		return client.contactPerson();
	}

	public com.novapost.client.api.InternetDocumentApi internetDocument(){
		return client.internetDocument();
	}

	public com.novapost.client.api.ScanSheetApi scanSheet(){
		return client.scanSheet();
	}

	public com.novapost.client.api.AdditionalServiceApi additionalService(){
		return client.additionalService();
	}

	public com.novapost.client.api.CarCallApi courier(){
		return client.courier();
	}

	public com.novapost.client.api.TrackingApi tracking(){
		return client.tracking();
	}

	public com.novapost.client.api.PrintApi print(){
		return client.print();
	}

	private <T> List<T> unwrapData(ApiSupplier<NpResponse<T>> call, String operationName){
		NpResponse<T> response = executeCall(call, operationName);
		return response.data() != null ? response.data() : List.of();
	}

	private <T> NpResponse<T> executeCall(ApiSupplier<NpResponse<T>> call, String operationName){
		try{
			NpResponse<T> response = call.get();
			if(response.hasErrors()){
				throw new NovaPostApiException("Nova Post API error in " + operationName, response.errors(), response.errorCodes());
			}
			logWarnings(operationName, response.warnings());
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
						errors.add(com.novapost.model.support.NpMessageListDeserializer.text(err));
					}
				}
				if(!errors.isEmpty()){
					List<String> codes = new ArrayList<>();
					if(root.path("errorCodes").isArray()){
						root.path("errorCodes").forEach(c -> codes.add(com.novapost.model.support.NpMessageListDeserializer.text(c)));
					}
					throw new NovaPostApiException("Nova Post API error in " + operationName, errors, codes);
				}
			}
			if(root.path("warnings").isArray() && !root.path("warnings").isEmpty()){
				List<String> warnings = new ArrayList<>();
				root.path("warnings").forEach(w -> warnings.add(com.novapost.model.support.NpMessageListDeserializer.text(w)));
				logWarnings(operationName, warnings);
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
