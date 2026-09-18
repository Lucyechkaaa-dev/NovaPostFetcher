package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentDeliveryDateRequest(
		@JsonProperty("DateTime") @JsonFormat(pattern = "dd.MM.yyyy") LocalDate dateTime,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("CityRecipient") String cityRecipient
){

}
