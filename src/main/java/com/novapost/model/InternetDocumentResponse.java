package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateDeserializer;

import java.time.LocalDate;
import com.novapost.model.support.NpDecimalDeserializer;
import java.math.BigDecimal;

/** Answer of {@code InternetDocumentGeneral/save} and {@code update}. */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InternetDocumentResponse(
		@JsonProperty("Ref") String ref,
		@JsonProperty("CostOnSite") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal costOnSite,
		@JsonProperty("EstimatedDeliveryDate") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate estimatedDeliveryDate,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("TypeDocument") String typeDocument
){

}
