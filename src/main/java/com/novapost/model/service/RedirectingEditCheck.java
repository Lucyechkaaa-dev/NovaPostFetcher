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
public record RedirectingEditCheck(
		@JsonProperty("PhoneSender") String phoneSender,
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("PhoneRecipient") String phoneRecipient,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("DocumentWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal documentWeight,
		@JsonProperty("Ref") String ref,
		@JsonProperty("CityRecipientDescription") String cityRecipientDescription,
		@JsonProperty("Number") String number,
		@JsonProperty("SettlementRecipient") String settlementRecipient,
		@JsonProperty("SettlementRecipientDescription") String settlementRecipientDescription,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("SettlementType") String settlementType,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("CounterpartyRecipientRef") String counterpartyRecipientRef,
		@JsonProperty("WarehouseRef") String warehouseRef,
		@JsonProperty("CounterpartyRecipientDescription") String counterpartyRecipientDescription,
		@JsonProperty("WarehouseDescription") String warehouseDescription,
		@JsonProperty("AddressDescription") String addressDescription,
		@JsonProperty("RecipientName") String recipientName,
		@JsonProperty("StreetDescription") String streetDescription
){
}
