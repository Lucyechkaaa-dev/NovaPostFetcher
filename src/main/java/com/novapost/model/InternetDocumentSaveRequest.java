package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InternetDocumentSaveRequest(
		@JsonProperty("Ref") String ref,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("DateTime") @JsonFormat(pattern = "dd.MM.yyyy") LocalDate dateTime,
		@JsonProperty("CargoType") String cargoType,
		@JsonProperty("VolumeGeneral") String volumeGeneral,
		@JsonProperty("Weight") String weight,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("SeatsAmount") String seatsAmount,
		@JsonProperty("Description") String description,
		@JsonProperty("Cost") String cost,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("Sender") String sender,
		@JsonProperty("SenderAddress") String senderAddress,
		@JsonProperty("ContactSender") String contactSender,
		@JsonProperty("SendersPhone") String sendersPhone,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("Recipient") String recipient,
		@JsonProperty("RecipientAddress") String recipientAddress,
		@JsonProperty("ContactRecipient") String contactRecipient,
		@JsonProperty("RecipientsPhone") String recipientsPhone,
		@JsonProperty("AfterpaymentOnGoodsCost") String afterpaymentOnGoodsCost,
		@JsonProperty("OptionsSeat") List<OptionSeat> optionsSeat
){

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record OptionSeat(
			@JsonProperty("volumetricVolume") String volumetricVolume,
			@JsonProperty("volumetricWidth") String volumetricWidth,
			@JsonProperty("volumetricLength") String volumetricLength,
			@JsonProperty("volumetricHeight") String volumetricHeight,
			@JsonProperty("weight") String weight
	){

	}
}
