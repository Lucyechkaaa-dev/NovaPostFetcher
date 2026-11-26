package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Response model for {@code AdditionalServiceGeneral/getChangeEWOrdersList}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ChangeEwOrder(
		@JsonProperty("OrderRef") String orderRef,
		@JsonProperty("OrderNumber") String orderNumber,
		@JsonProperty("OrderStatus") String orderStatus,
		@JsonProperty("DocumentNumber") String documentNumber,
		@JsonProperty("DateTime") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate dateTime,
		@JsonProperty("BeforeChangeSenderCounterparty") String beforeChangeSenderCounterparty,
		@JsonProperty("AfterChangeChangeSenderCounterparty") String afterChangeChangeSenderCounterparty,
		@JsonProperty("Cost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal cost,
		@JsonProperty("BeforeChangeSenderPhone") String beforeChangeSenderPhone,
		@JsonProperty("AfterChangeSenderPhone") String afterChangeSenderPhone
){
}
