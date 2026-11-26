package com.novapost.model.courier;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateDeserializer;
import com.novapost.model.support.NpDateTimeDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

/**
 * Response model for {@code CarCallGeneral/getOrdersListCourierCall}.
 * Docs: https://developers.novaposhta.ua/view/model/96141693-eaae-11ef-84e0-48df37b921da
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CourierCall(
		@JsonProperty("TimeIntervalDescription") String timeIntervalDescription,
		@JsonProperty("SenderDescription") String senderDescription,
		@JsonProperty("Status") String status,
		@JsonProperty("DateTime") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateTime,
		@JsonProperty("FactualWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal factualWeight,
		@JsonProperty("ContactSenderDescription") String contactSenderDescription,
		@JsonProperty("PreferredDeliveryDate") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate preferredDeliveryDate,
		@JsonProperty("AggregatedOrderInfo") JsonNode aggregatedOrderInfo,
		@JsonProperty("AvailableActions") JsonNode availableActions,
		@JsonProperty("SenderAddressDescription") String senderAddressDescription,
		@JsonProperty("ContactSenderPhone") String contactSenderPhone,
		@JsonProperty("StatusDescription") String statusDescription,
		@JsonProperty("SenderSettlementRef") String senderSettlementRef,
		@JsonProperty("Ref") String ref,
		@JsonProperty("TimeInterval") String timeInterval,
		@JsonProperty("PlanedWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal planedWeight,
		@JsonProperty("Number") String number
){
}
