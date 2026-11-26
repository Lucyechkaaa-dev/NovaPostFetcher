package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDecimalDeserializer;
import com.novapost.model.support.NpIntegerDeserializer;

import java.math.BigDecimal;

/** Answer of {@code InternetDocumentGeneral/getDocumentPrice}; the API returns the amounts as strings. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentPriceResponse(
		@JsonProperty("Cost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal cost,
		@JsonProperty("AssessedCost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal assessedCost,
		@JsonProperty("CostRedelivery") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal costRedelivery,
		@JsonProperty("CostPack") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal costPack,
		@JsonProperty("TZoneInfo") TzoneInfo tzoneInfo
){

	/** The shape before the tariff zone was modelled. */
	public DocumentPriceResponse(BigDecimal cost, BigDecimal assessedCost, BigDecimal costRedelivery, BigDecimal costPack){
		this(cost, assessedCost, costRedelivery, costPack, null);
	}

	/** Tariff zone of the delivery: {@code {"TzoneName":"Тарифназона:Тарифназона4","TzoneID":"4"}}. */
	@JsonIgnoreProperties(ignoreUnknown = true)
	public record TzoneInfo(
			@JsonProperty("TzoneName") String tzoneName,
			@JsonProperty("TzoneID") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer tzoneId
	){

	}
}
