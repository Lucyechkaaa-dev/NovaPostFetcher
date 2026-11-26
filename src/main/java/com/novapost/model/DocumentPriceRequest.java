package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.novapost.model.document.CargoDetailItem;
import com.novapost.model.document.RedeliveryCalculate;
import com.novapost.model.support.NpDecimalDeserializer;
import com.novapost.model.support.NpDecimalSerializer;

import java.math.BigDecimal;
import java.util.List;

/**
 * Request of {@code InternetDocumentGeneral/getDocumentPrice}. For the documented cargo types ({@code Cargo},
 * {@code Documents}, {@code TiresWheels}, {@code Pallet}) see {@code CommonGeneral/getCargoTypes}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentPriceRequest(
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("Weight") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal weight,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("Cost") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal cost,
		@JsonProperty("CargoType") String cargoType,
		@JsonProperty("SeatsAmount") @JsonFormat(shape = JsonFormat.Shape.STRING) Integer seatsAmount,
		@JsonProperty("RedeliveryCalculate") RedeliveryCalculate redeliveryCalculate,
		@JsonProperty("PackCount") @JsonFormat(shape = JsonFormat.Shape.STRING) Integer packCount,
		@JsonProperty("PackRef") String packRef,
		@JsonProperty("Amount") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal amount,
		@JsonProperty("CargoDetails") List<CargoDetailItem> cargoDetails,
		@JsonProperty("CargoDescription") String cargoDescription
){

	/** The basic seven-field request (no backward delivery, packing or cargo details). */
	public DocumentPriceRequest(String citySender, String cityRecipient, BigDecimal weight, String serviceType, BigDecimal cost,
	                            String cargoType, Integer seatsAmount){
		this(citySender, cityRecipient, weight, serviceType, cost, cargoType, seatsAmount, null, null, null, null, null, null);
	}

	/** Adds a backward delivery of money ({@code RedeliveryCalculate: {CargoType: Money, Amount}}). */
	public DocumentPriceRequest withRedeliveryMoney(BigDecimal amount){
		return new DocumentPriceRequest(citySender, cityRecipient, weight, serviceType, cost, cargoType, seatsAmount,
				RedeliveryCalculate.builder().cargoType("Money").amount(amount).build(), packCount, packRef, this.amount,
				cargoDetails, cargoDescription);
	}

	/** Adds packing ({@code PackRef}, {@code PackCount}). */
	public DocumentPriceRequest withPacking(String packRef, Integer packCount){
		return new DocumentPriceRequest(citySender, cityRecipient, weight, serviceType, cost, cargoType, seatsAmount,
				redeliveryCalculate, packCount, packRef, amount, cargoDetails, cargoDescription);
	}
}
