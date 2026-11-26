package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpBooleanDeserializer;

/**
 * Response model for {@code AdditionalServiceGeneral/CheckPossibilityChangeEW}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChangeEwPossibility(
		@JsonProperty("CanChangeSender") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangeSender,
		@JsonProperty("CanChangeRecipient") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangeRecipient,
		@JsonProperty("CanChangePayerTypeOrPaymentMethod") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangePayerTypeOrPaymentMethod,
		@JsonProperty("CanChangeBackwardDeliveryDocuments") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangeBackwardDeliveryDocuments,
		@JsonProperty("CanChangeBackwardDeliveryMoney") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangeBackwardDeliveryMoney,
		@JsonProperty("CanChangeCash2Card") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangeCash2Card,
		@JsonProperty("CanChangeBackwardDeliveryOther") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangeBackwardDeliveryOther,
		@JsonProperty("CanChangeAfterpaymentType") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangeAfterpaymentType,
		@JsonProperty("CanChangeLiftingOnFloor") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangeLiftingOnFloor,
		@JsonProperty("CanChangeLiftingOnFloorWithElevator") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangeLiftingOnFloorWithElevator,
		@JsonProperty("CanChangeFillingWarranty") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canChangeFillingWarranty,
		@JsonProperty("SenderCounterparty") String senderCounterparty,
		@JsonProperty("ContactPersonSender") String contactPersonSender,
		@JsonProperty("SenderPhone") String senderPhone,
		@JsonProperty("RecipientCounterparty") String recipientCounterparty,
		@JsonProperty("ContactPersonRecipient") String contactPersonRecipient,
		@JsonProperty("RecipientPhone") String recipientPhone,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("PaymentMethod") String paymentMethod
){
}
