package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpBooleanDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Settlement(
		@JsonProperty("Ref") String ref,
		@JsonProperty("SettlementType") String settlementType,
		@JsonProperty("Latitude") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal latitude,
		@JsonProperty("Longitude") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal longitude,
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
		@JsonProperty("Warehouse") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean warehouse,
		@JsonProperty("Delivery1") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery1,
		@JsonProperty("Delivery2") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery2,
		@JsonProperty("Delivery3") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery3,
		@JsonProperty("Delivery4") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery4,
		@JsonProperty("Delivery5") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery5,
		@JsonProperty("Delivery6") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery6,
		@JsonProperty("Delivery7") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery7,
		@JsonProperty("Conglomerates") JsonNode conglomerates
){

	/** The shape before the delivery-day flags and conglomerates were modelled. */
	public Settlement(String ref, String settlementType, BigDecimal latitude, BigDecimal longitude, String description,
	                  String descriptionRu, String settlementTypeDescription, String settlementTypeDescriptionRu,
	                  String region, String regionsDescription, String regionsDescriptionRu, String area,
	                  String areaDescription, String areaDescriptionRu, String index1, String index2,
	                  String indexCOATSU1, Boolean warehouse){
		this(ref, settlementType, latitude, longitude, description, descriptionRu, settlementTypeDescription,
				settlementTypeDescriptionRu, region, regionsDescription, regionsDescriptionRu, area, areaDescription,
				areaDescriptionRu, index1, index2, indexCOATSU1, warehouse, null, null, null, null, null, null, null, null);
	}
}
