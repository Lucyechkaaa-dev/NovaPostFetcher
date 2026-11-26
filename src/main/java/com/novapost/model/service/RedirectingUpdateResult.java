package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDecimalDeserializer;
import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

/**
 * Response model for {@code AdditionalServiceGeneral/update}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RedirectingUpdateResult(
		@JsonProperty("SettlementType") String settlementType,
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("PhoneSender") String phoneSender,
		@JsonProperty("WarehouseDescription") String warehouseDescription,
		@JsonProperty("Number") String number,
		@JsonProperty("PhoneRecipient") String phoneRecipient,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("DocumentWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal documentWeight,
		@JsonProperty("SettlementRecipient") String settlementRecipient,
		@JsonProperty("NoteAddressRecipient") String noteAddressRecipient,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("InternationalDeliveryType") String internationalDeliveryType,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("SettlementRecipientDescription") String settlementRecipientDescription,
		@JsonProperty("EdrpouRecipient") String edrpouRecipient,
		@JsonProperty("AddressDescription") String addressDescription,
		@JsonProperty("CounterpartyRecipientRef") String counterpartyRecipientRef,
		@JsonProperty("RecipientSettlementStreet") String recipientSettlementStreet,
		@JsonProperty("Ref") String ref,
		@JsonProperty("WarehouseRef") String warehouseRef,
		@JsonProperty("CounterpartyRecipientDescription") String counterpartyRecipientDescription,
		@JsonProperty("CityRecipientDescription") String cityRecipientDescription,
		@JsonProperty("StreetDescription") String streetDescription,
		@JsonProperty("RecipientName") String recipientName
){
}
