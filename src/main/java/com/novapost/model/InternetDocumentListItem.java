package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InternetDocumentListItem(
		@JsonProperty("Ref") String ref,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("DateTime") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime dateTime,
		@JsonProperty("Cost") String cost,
		@JsonProperty("Weight") String weight,
		@JsonProperty("SeatsAmount") String seatsAmount,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("SenderDescription") String senderDescription,
		@JsonProperty("RecipientDescription") String recipientDescription,
		@JsonProperty("CityRecipientDescription") String cityRecipientDescription,
		@JsonProperty("CitySenderDescription") String citySenderDescription,
		@JsonProperty("StateName") String stateName,
		@JsonProperty("EstimatedDeliveryDate") @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime estimatedDeliveryDate
){

}
