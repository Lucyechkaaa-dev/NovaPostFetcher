package com.novapost.model.document;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import com.novapost.model.support.NpIntegerDeserializer;
import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.List;
import java.util.Map;

/**
 * Response model for {@code InternetDocumentGeneral/generateReport}.
 * Docs: https://developers.novaposhta.ua/view/model/a90d323c-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record GenerateReportItem(
		@JsonProperty("Ref") String ref,
		@JsonProperty("DateTime") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate dateTime,
		@JsonProperty("PreferredDeliveryDate") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate preferredDeliveryDate,
		@JsonProperty("Weight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal weight,
		@JsonProperty("SeatsAmount") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer seatsAmount,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("Cost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal cost,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("State") String state,
		@JsonProperty("SenderAddress") String senderAddress,
		@JsonProperty("RecipientAddress") String recipientAddress,
		@JsonProperty("CostOnSite") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal costOnSite,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("AfterpaymentOnGoodsCost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal afterpaymentOnGoodsCost,
		@JsonProperty("PackingNumber") String packingNumber
){
}
