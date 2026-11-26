package com.novapost.client.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.model.SearchSettlementFilter;
import com.novapost.model.SearchSettlementItem;
import com.novapost.model.SettlementStreetFilter;
import com.novapost.model.SettlementStreetSearchResult;
import com.novapost.model.address.CounterpartyAddressSaveRequest;
import com.novapost.model.address.CounterpartyAddressResult;
import com.novapost.model.common.RefRequest;
import com.novapost.model.common.RefResult;
import com.novapost.model.address.CounterpartyAddressUpdateRequest;
import com.novapost.model.SettlementFilter;
import com.novapost.model.Settlement;
import com.novapost.model.address.CityFilter;
import com.novapost.model.address.City;
import com.novapost.model.address.Area;
import com.novapost.model.WarehouseFilter;
import com.novapost.model.Warehouse;
import com.novapost.model.WarehouseTypes;
import com.novapost.model.StreetFilter;
import com.novapost.model.Street;
import com.novapost.model.address.SettlementCountryRegionRequest;
import com.novapost.model.address.SettlementRegion;
import com.novapost.model.address.SettlementAreaRequest;
import com.novapost.model.address.SettlementArea;

/**
 * Facade for the "Address" section of the Nova Post API documentation
 * (https://developers.novaposhta.ua/view/model/a0cf0f5f-8512-11ec-8ced-005056b2dbe1).
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java.
 */
public final class AddressApi{

	private final NovaPostClient client;

	public AddressApi(NovaPostClient client){
		this.client = client;
	}

	/**
	 * {@code AddressGeneral/searchSettlements} (docs pages a0eb83ab).
	 */
	public NpCall<SearchSettlementItem> searchSettlements(SearchSettlementFilter request){
		return client.call(NpModel.ADDRESS, "searchSettlements", request, SearchSettlementItem.class);
	}

	/**
	 * {@code AddressGeneral/searchSettlementStreets} (docs pages a1329635).
	 */
	public NpCall<SettlementStreetSearchResult> searchSettlementStreets(SettlementStreetFilter request){
		return client.call(NpModel.ADDRESS, "searchSettlementStreets", request, SettlementStreetSearchResult.class);
	}

	/**
	 * {@code AddressGeneral/save} (docs pages a155d0d9).
	 */
	public NpCall<CounterpartyAddressResult> saveCounterpartyAddress(CounterpartyAddressSaveRequest request){
		return client.call(NpModel.ADDRESS, "save", request, CounterpartyAddressResult.class);
	}

	/**
	 * {@code AddressGeneral/delete} (docs pages a177069a).
	 */
	public NpCall<RefResult> deleteCounterpartyAddress(RefRequest request){
		return client.call(NpModel.ADDRESS, "delete", request, RefResult.class);
	}

	/**
	 * {@code AddressGeneral/update} (docs pages a19ba934).
	 */
	public NpCall<CounterpartyAddressResult> updateCounterpartyAddress(CounterpartyAddressUpdateRequest request){
		return client.call(NpModel.ADDRESS, "update", request, CounterpartyAddressResult.class);
	}

	/**
	 * {@code AddressGeneral/getSettlements} (docs pages a1c42723).
	 * <p>Keep a copy of this directory on your side; the documentation recommends refreshing it once a month. FindByString searches in Ukrainian only.
	 */
	public NpCall<Settlement> getSettlements(SettlementFilter request){
		return client.call(NpModel.ADDRESS_GENERAL, "getSettlements", request, Settlement.class);
	}

	/**
	 * {@code AddressGeneral/getCities} (docs pages a1e6f0a7).
	 * <p>The Ref of a city is what CitySender / CityRecipient of a waybill need. The documentation recommends refreshing this directory daily.
	 */
	public NpCall<City> getCities(CityFilter request){
		return client.call(NpModel.ADDRESS, "getCities", request, City.class);
	}

	/**
	 * {@code AddressGeneral/getAreas} (docs pages a20ee6e4).
	 */
	public NpCall<Area> getAreas(){
		return client.call(NpModel.ADDRESS, "getAreas", null, Area.class);
	}

	/**
	 * {@code AddressGeneral/getWarehouses} (docs pages a2322f38).
	 */
	public NpCall<Warehouse> getWarehouses(WarehouseFilter request){
		return client.call(NpModel.ADDRESS_GENERAL, "getWarehouses", request, Warehouse.class);
	}

	/**
	 * {@code AddressGeneral/getWarehouseTypes} (docs pages a2587b53).
	 */
	public NpCall<WarehouseTypes> getWarehouseTypes(){
		return client.call(NpModel.ADDRESS_GENERAL, "getWarehouseTypes", null, WarehouseTypes.class);
	}

	/**
	 * {@code AddressGeneral/getStreet} (docs pages a27c20d7).
	 */
	public NpCall<Street> getStreet(StreetFilter request){
		return client.call(NpModel.ADDRESS, "getStreet", request, Street.class);
	}

	/**
	 * {@code AddressGeneral/getSettlementCountryRegion} (docs pages a98072f9).
	 */
	public NpCall<SettlementRegion> getSettlementCountryRegion(SettlementCountryRegionRequest request){
		return client.call(NpModel.ADDRESS_GENERAL, "getSettlementCountryRegion", request, SettlementRegion.class);
	}

	/**
	 * {@code AddressGeneral/getSettlementAreas} (docs pages c0bfb1a3).
	 */
	public NpCall<SettlementArea> getSettlementAreas(SettlementAreaRequest request){
		return client.call(NpModel.ADDRESS_GENERAL, "getSettlementAreas", request, SettlementArea.class);
	}
}
