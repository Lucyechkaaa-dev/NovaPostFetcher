package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Settlement(
		@JsonProperty("Ref") String ref,
		@JsonProperty("SettlementType") String settlementType,
		@JsonProperty("Latitude") String latitude,
		@JsonProperty("Longitude") String longitude,
		@JsonProperty("Description") String description,
		@JsonProperty("DescriptionRu") String descriptionRu,
		@JsonProperty("SettlementTypeDescription") String settlementTypeDescription,
		@JsonProperty("SettlementTypeDescriptionRu") String settlementTypeDescriptionRu,
		@JsonProperty("Region") String region,
		@JsonProperty("RegionsDescription") String regionsDescription,
		@JsonProperty("RegionsDescriptionRu") String regionsDescriptionRu,
		@JsonProperty("Area") String area,
		@JsonProperty("AreaDescription") String areaDescription,
		@JsonProperty("AreaDescriptionRu") String areaDescriptionRu,
		@JsonProperty("Index1") String index1,
		@JsonProperty("Index2") String index2,
		@JsonProperty("IndexCOATSU1") String indexCOATSU1,
		@JsonProperty("Warehouse") String warehouse
){

}
