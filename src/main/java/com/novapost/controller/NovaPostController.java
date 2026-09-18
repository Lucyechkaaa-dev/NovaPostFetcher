package com.novapost.controller;

import com.novapost.client.NovaPostClient;
import com.novapost.config.AppConfig;
import com.novapost.model.*;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class NovaPostController{

	private final NovaPostClient client;

	public NovaPostController(NovaPostClient client){
		if(client == null){
			throw new IllegalArgumentException("NovaPostClient must not be null");
		}
		this.client = client;
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

	public List<Settlement> getSettlements(String query, int page, int limit){
		SettlementFilter filter = SettlementFilter.byString(query, page, limit);
		return unwrapData(() -> client.getSettlements(filter), "getSettlements");
	}

	public List<Warehouse> getWarehouses(String cityRef){
		return getWarehouses(cityRef, 1, 100);
	}

	public List<Warehouse> getWarehouses(String cityRef, int page, int limit){
		WarehouseFilter filter = WarehouseFilter.byCityRef(cityRef, page, limit);
		return unwrapData(() -> client.getWarehouses(filter), "getWarehouses");
	}

	public List<Warehouse> searchWarehouses(String cityRef, String searchString){
		WarehouseFilter filter = new WarehouseFilter(cityRef, null, null, null, searchString, null, 1, 50, null);
		return unwrapData(() -> client.getWarehouses(filter), "getWarehouses");
	}

	public List<SettlementStreet> searchStreets(String settlementRef, String streetName){
		SettlementStreetFilter filter = SettlementStreetFilter.of(settlementRef, streetName);
		return unwrapData(() -> client.searchSettlementStreets(filter), "searchSettlementStreets");
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

	public List<InternetDocumentListItem> getRecentWaybills(int days){
		return getWaybills(LocalDate.now().minusDays(Math.max(1, days)), LocalDate.now());
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

	public NovaPostClient getClient(){
		return client;
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

	@FunctionalInterface
	public interface ApiSupplier<T>{

		T get() throws IOException, InterruptedException;
	}
}
