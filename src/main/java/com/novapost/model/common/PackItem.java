package com.novapost.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDecimalDeserializer;

import java.math.BigDecimal;

/**
 * Response model for {@code CommonGeneral/getPackList}.
 * Docs: https://developers.novaposhta.ua/view/model/a55b2c64-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PackItem(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Description") String description,
		@JsonProperty("DescriptionRu") String descriptionRu,
		@JsonProperty("Length") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal length,
		@JsonProperty("Width") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal width,
		@JsonProperty("Height") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal height,
		@JsonProperty("VolumetricWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal volumetricWeight,
		@JsonProperty("TypeOfPacking") String typeOfPacking
){
}
