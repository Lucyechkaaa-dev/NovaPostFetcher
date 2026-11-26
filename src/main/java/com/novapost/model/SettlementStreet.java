package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDecimalDeserializer;
import java.math.BigDecimal;

/**
 * One street from {@code AddressGeneral/searchSettlementStreets}. The method answers with a wrapper, see
 * {@link SettlementStreetSearchResult}; this record is an element of its {@code Addresses} array.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record SettlementStreet(
		@JsonProperty("SettlementRef") String settlementRef,
		@JsonProperty("SettlementStreetRef") String settlementStreetRef,
		@JsonProperty("SettlementStreetDescription") String settlementStreetDescription,
		@JsonProperty("SettlementStreetDescriptionRu") String settlementStreetDescriptionRu,
		@JsonProperty("Present") String present,
		@JsonProperty("StreetsType") String streetsType,
		@JsonProperty("StreetsTypeDescription") String streetsTypeDescription,
		@JsonProperty("Location") Location location
){

	/** Street coordinates: {@code {"lat":50.448,"lon":30.522}}. */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record Location(
			@JsonProperty("lat") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal lat,
			@JsonProperty("lon") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal lon
	){

	}

	/** The street's Ref (the documentation calls it {@code SettlementStreetRef}). */
	public String ref(){
		return settlementStreetRef;
	}

	/** The street's name without the type, for example {@code Хрещатик}. */
	public String description(){
		return settlementStreetDescription;
	}
}
