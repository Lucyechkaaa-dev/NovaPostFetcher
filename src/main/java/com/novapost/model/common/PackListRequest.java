package com.novapost.model.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.novapost.model.support.NpDecimalDeserializer;
import com.novapost.model.support.NpDecimalSerializer;
import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code CommonGeneral/getPackList}.
 * Docs: https://developers.novaposhta.ua/view/model/a55b2c64-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PackListRequest(
		@JsonProperty("Length") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal length,
		@JsonProperty("Width") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal width,
		@JsonProperty("Height") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal height,
		@JsonProperty("VolumetricWeight") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal volumetricWeight,
		@JsonProperty("TypeOfPacking") String typeOfPacking
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private BigDecimal length;
		private BigDecimal width;
		private BigDecimal height;
		private BigDecimal volumetricWeight;
		private String typeOfPacking;

		public Builder length(BigDecimal length){ this.length = length; return this; }
		public Builder width(BigDecimal width){ this.width = width; return this; }
		public Builder height(BigDecimal height){ this.height = height; return this; }
		public Builder volumetricWeight(BigDecimal volumetricWeight){ this.volumetricWeight = volumetricWeight; return this; }
		public Builder typeOfPacking(String typeOfPacking){ this.typeOfPacking = typeOfPacking; return this; }

		public PackListRequest build(){
			return new PackListRequest(length, width, height, volumetricWeight, typeOfPacking);
		}
	}
}
