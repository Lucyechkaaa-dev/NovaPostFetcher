package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentPriceRequest(
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("Weight") String weight,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("Cost") String cost,
		@JsonProperty("CargoType") String cargoType,
		@JsonProperty("SeatsAmount") String seatsAmount
){

}
