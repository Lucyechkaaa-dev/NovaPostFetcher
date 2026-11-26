package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * {@code data[0]} of {@code AddressGeneral/searchSettlementStreets}: the streets are not the elements of {@code data}
 * themselves but of the {@code Addresses} array inside the single wrapper object.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SettlementStreetSearchResult(
		@JsonProperty("TotalCount") Integer totalCount,
		@JsonProperty("Addresses") List<SettlementStreet> addresses
){

	public List<SettlementStreet> streets(){
		return addresses != null ? addresses : List.of();
	}
}
