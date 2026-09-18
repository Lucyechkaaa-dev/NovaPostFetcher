package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record InternetDocumentResponse(
		@JsonProperty("Ref") String ref,
		@JsonProperty("CostOnSite") String costOnSite,
		@JsonProperty("EstimatedDeliveryDate") @JsonFormat(pattern = "dd.MM.yyyy") LocalDate estimatedDeliveryDate,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("TypeDocument") String typeDocument
){

}
