package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateTimeDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

/**
 * Response model for {@code AdditionalServiceGeneral/getReturnOrdersList}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReturnOrder(
		@JsonProperty("OrderRef") String orderRef,
		@JsonProperty("OrderNumber") String orderNumber,
		@JsonProperty("OrderStatus") String orderStatus,
		@JsonProperty("DocumentNumber") String documentNumber,
		@JsonProperty("CounterpartyRecipient") String counterpartyRecipient,
		@JsonProperty("ContactPersonRecipient") String contactPersonRecipient,
		@JsonProperty("AddressRecipient") String addressRecipient,
		@JsonProperty("DeliveryCost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal deliveryCost,
		@JsonProperty("EstimatedDeliveryDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime estimatedDeliveryDate,
		@JsonProperty("ExpressWaybillNumber") String expressWaybillNumber,
		@JsonProperty("ExpressWaybillStatus") String expressWaybillStatus
){
}
