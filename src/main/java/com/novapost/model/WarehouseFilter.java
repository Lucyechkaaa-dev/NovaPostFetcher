package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record WarehouseFilter(
		@JsonProperty("CityRef") String cityRef,
		@JsonProperty("CityName") String cityName,
		@JsonProperty("SettlementRef") String settlementRef,
		@JsonProperty("WarehouseId") String warehouseId,
		@JsonProperty("FindByString") String findByString,
		@JsonProperty("TypeOfWarehouseRef") String typeOfWarehouseRef,
		@JsonProperty("Page") Integer page,
		@JsonProperty("Limit") Integer limit,
		@JsonProperty("Language") String language
){

	public static WarehouseFilter byCityRef(String cityRef){
		return new WarehouseFilter(cityRef, null, null, null, null, null, null, null, null);
	}

	public static WarehouseFilter byCityRef(String cityRef, int page, int limit){
		return new WarehouseFilter(cityRef, null, null, null, null, null, page, limit, null);
	}

	public static WarehouseFilter bySettlementRef(String settlementRef){
		return new WarehouseFilter(null, null, settlementRef, null, null, null, null, null, null);
	}
}
