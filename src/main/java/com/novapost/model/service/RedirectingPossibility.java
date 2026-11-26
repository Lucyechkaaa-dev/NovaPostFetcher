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
 * Response model for {@code AdditionalServiceGeneral/checkPossibilityForRedirecting}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RedirectingPossibility(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Number") String number,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("WarehouseRef") String warehouseRef,
		@JsonProperty("WarehouseDescription") String warehouseDescription,
		@JsonProperty("AddressDescription") String addressDescription,
		@JsonProperty("StreetDescription") String streetDescription,
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("CityRecipientDescription") String cityRecipientDescription,
		@JsonProperty("SettlementRecipient") String settlementRecipient,
		@JsonProperty("SettlementRecipientDescription") String settlementRecipientDescription,
		@JsonProperty("SettlementType") String settlementType,
		@JsonProperty("CounterpartyRecipientRef") String counterpartyRecipientRef,
		@JsonProperty("CounterpartyRecipientDescription") String counterpartyRecipientDescription,
		@JsonProperty("RecipientName") String recipientName,
		@JsonProperty("PhoneSender") String phoneSender,
		@JsonProperty("PhoneRecipient") String phoneRecipient,
		@JsonProperty("DocumentWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal documentWeight
){
}
