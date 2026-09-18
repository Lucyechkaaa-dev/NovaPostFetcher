package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DocumentPriceResponse(
		@JsonProperty("Cost") Double cost,
		@JsonProperty("AssessedCost") Double assessedCost,
		@JsonProperty("CostRedelivery") Double costRedelivery,
		@JsonProperty("CostPack") Double costPack
){

}
