package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Request of {@code InternetDocumentGeneral/getDocumentDeliveryDate}.
 * <p>The documentation's example sends {@code "DateTime": "2024-05-31 15:00:00"}, while this library has always sent the
 * calendar date {@code dd.MM.yyyy}. Both are available: the {@link LocalDate} constructor keeps the old behaviour and
 * {@link #ofDateTime} sends the documented example format; the choice is checked by {@code LiveContractSmokeTest}.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record DocumentDeliveryDateRequest(
		@JsonProperty("DateTime") String dateTime,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("CityRecipient") String cityRecipient
){

	private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	private static final DateTimeFormatter DATE_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	/** Sends the calendar date as {@code dd.MM.yyyy}. */
	public DocumentDeliveryDateRequest(LocalDate date, String serviceType, String citySender, String cityRecipient){
		this(date != null ? date.format(DATE) : null, serviceType, citySender, cityRecipient);
	}

	/** Sends date and time as {@code yyyy-MM-dd HH:mm:ss}, the form used in the documentation's example. */
	public static DocumentDeliveryDateRequest ofDateTime(LocalDateTime dateTime, String serviceType, String citySender, String cityRecipient){
		return new DocumentDeliveryDateRequest(dateTime != null ? dateTime.format(DATE_TIME) : null, serviceType, citySender, cityRecipient);
	}
}
