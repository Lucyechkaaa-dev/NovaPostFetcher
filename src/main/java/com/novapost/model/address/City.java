package com.novapost.model.address;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpBooleanDeserializer;
import com.novapost.model.support.NpIntegerDeserializer;

import java.util.List;
import java.util.Map;

/**
 * Response model for {@code AddressGeneral/getCities}.
 * Docs: https://developers.novaposhta.ua/view/model/a0cf0f5f-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record City(
		@JsonProperty("Description") String description,
		@JsonProperty("DescriptionRu") String descriptionRu,
		@JsonProperty("Ref") String ref,
		@JsonProperty("Delivery1") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery1,
		@JsonProperty("Delivery2") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery2,
		@JsonProperty("Delivery3") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery3,
		@JsonProperty("Delivery4") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery4,
		@JsonProperty("Delivery5") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery5,
		@JsonProperty("Delivery6") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery6,
		@JsonProperty("Delivery7") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean delivery7,
		@JsonProperty("Area") String area,
		@JsonProperty("SettlementType") String settlementType,
		@JsonProperty("IsBranch") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean isBranch,
		@JsonProperty("PreventEntryNewStreetsUser") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean preventEntryNewStreetsUser,
		@JsonProperty("Conglomerates") JsonNode conglomerates,
		@JsonProperty("CityID") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer cityID,
		@JsonProperty("SettlementTypeDescriptionRu") String settlementTypeDescriptionRu,
		@JsonProperty("SettlementTypeDescription") String settlementTypeDescription
){
}
