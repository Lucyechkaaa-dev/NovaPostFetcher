package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateTimeDeserializer;

import java.time.LocalDateTime;
import com.novapost.model.support.NpDateDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import com.novapost.model.support.NpIntegerDeserializer;

/**
 * One waybill from {@code InternetDocumentGeneral/getDocumentList}. The documentation shows {@code дд.мм.рррр} for the
 * dates while other methods return {@code yyyy-MM-dd HH:mm:ss}; {@link NpDateTimeDeserializer} accepts all of them.
 * The remaining dates stay raw strings because their format is not stable; {@code NpDates} parses them on demand.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InternetDocumentListItem(
		@JsonProperty("Ref") String ref,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("DateTime") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateTime,
		@JsonProperty("CostOnSite") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal costOnSite,
		@JsonProperty("Weight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal weight,
		@JsonProperty("SeatsAmount") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer seatsAmount,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("SenderDescription") String senderDescription,
		@JsonProperty("RecipientDescription") String recipientDescription,
		@JsonProperty("CityRecipientDescription") String cityRecipientDescription,
		@JsonProperty("CitySenderDescription") String citySenderDescription,
		@JsonProperty("StateName") String stateName,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("EstimatedDeliveryDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime estimatedDeliveryDate,
		@JsonProperty("PreferredDeliveryDate") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate preferredDeliveryDate,
		@JsonProperty("RecipientDateTime") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate recipientDateTime,
		@JsonProperty("EWDateCreated") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate ewDateCreated,
		@JsonProperty("Cost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal cost,
		@JsonProperty("SenderAddress") String senderAddress,
		@JsonProperty("RecipientAddress") String recipientAddress,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("AfterpaymentOnGoodsCost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal afterpaymentOnGoodsCost,
		@JsonProperty("PackingNumber") String packingNumber,
		@JsonProperty("RejectionReason") String rejectionReason,
		@JsonProperty("StateId") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer stateId,
		@JsonProperty("ScanSheetNumber") String scanSheetNumber
){

	/** The shape before the remaining fields of the documented response were modelled. */
	public InternetDocumentListItem(String ref, String intDocNumber, LocalDateTime dateTime, BigDecimal costOnSite, BigDecimal weight,
	                                Integer seatsAmount, String citySender, String cityRecipient, String senderDescription,
	                                String recipientDescription, String cityRecipientDescription, String citySenderDescription,
	                                String stateName, String payerType, LocalDateTime estimatedDeliveryDate){
		this(ref, intDocNumber, dateTime, costOnSite, weight, seatsAmount, citySender, cityRecipient, senderDescription,
				recipientDescription, cityRecipientDescription, citySenderDescription, stateName, payerType,
				estimatedDeliveryDate, null, null, null, null, null, null, null, null, null, null, null, null);
	}
}
