package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateDeserializer;
import com.novapost.model.support.NpDateTimeDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Response model for {@code AdditionalServiceGeneral/getRedirectionOrdersList}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RedirectingOrder(
		@JsonProperty("OrderRef") String orderRef,
		@JsonProperty("OrderNumber") String orderNumber,
		@JsonProperty("DateTime") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateTime,
		@JsonProperty("DocumentNumber") String documentNumber,
		@JsonProperty("Note") String note,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("RecipientAddress") String recipientAddress,
		@JsonProperty("CounterpartyRecipient") String counterpartyRecipient,
		@JsonProperty("RecipientName") String recipientName,
		@JsonProperty("PhoneRecipient") String phoneRecipient,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("DeliveryCost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal deliveryCost,
		@JsonProperty("EstimatedDeliveryDate") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate estimatedDeliveryDate,
		@JsonProperty("ExpressWaybillNumber") String expressWaybillNumber,
		@JsonProperty("ExpressWaybillStatus") String expressWaybillStatus
){
}
