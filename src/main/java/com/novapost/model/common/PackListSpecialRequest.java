package com.novapost.model.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.novapost.model.support.NpBooleanDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import com.novapost.model.support.NpDecimalSerializer;
import com.novapost.model.support.NpFlagSerializer;
import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code CommonGeneral/getPackListSpecial}.
 * Docs: https://developers.novaposhta.ua/view/model/a55b2c64-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PackListSpecialRequest(
		@JsonProperty("Length") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal length,
		@JsonProperty("PackForSale") @JsonSerialize(using = NpFlagSerializer.class) @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean packForSale,
		@JsonProperty("Height") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal height,
		@JsonProperty("CargoType") String cargoType,
		@JsonProperty("VolumetricWeight") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal volumetricWeight,
		@JsonProperty("Width") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal width,
		@JsonProperty("New") @JsonSerialize(using = NpFlagSerializer.class) @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean isNew,
		@JsonProperty("SendingFrom") String sendingFrom
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private BigDecimal length;
		private Boolean packForSale;
		private BigDecimal height;
		private String cargoType;
		private BigDecimal volumetricWeight;
		private BigDecimal width;
		private Boolean isNew;
		private String sendingFrom;

		public Builder length(BigDecimal length){ this.length = length; return this; }
		public Builder packForSale(Boolean packForSale){ this.packForSale = packForSale; return this; }
		public Builder height(BigDecimal height){ this.height = height; return this; }
		public Builder cargoType(String cargoType){ this.cargoType = cargoType; return this; }
		public Builder volumetricWeight(BigDecimal volumetricWeight){ this.volumetricWeight = volumetricWeight; return this; }
		public Builder width(BigDecimal width){ this.width = width; return this; }
		public Builder isNew(Boolean isNew){ this.isNew = isNew; return this; }
		public Builder sendingFrom(String sendingFrom){ this.sendingFrom = sendingFrom; return this; }

		public PackListSpecialRequest build(){
			return new PackListSpecialRequest(length, packForSale, height, cargoType, volumetricWeight, width, isNew, sendingFrom);
		}
	}
}
