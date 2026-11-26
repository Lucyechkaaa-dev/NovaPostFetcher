package com.novapost.model.document;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpIntegerDeserializer;

import java.util.List;
import java.util.Map;

/**
 * Request model for the Nova Post API.
 * Docs: https://developers.novaposhta.ua/view/model/a90d323c-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CargoDetailItem(
		@JsonProperty("CargoDescription") String cargoDescription,
		@JsonProperty("Amount") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer amount
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String cargoDescription;
		private Integer amount;

		public Builder cargoDescription(String cargoDescription){ this.cargoDescription = cargoDescription; return this; }
		public Builder amount(Integer amount){ this.amount = amount; return this; }

		public CargoDetailItem build(){
			return new CargoDetailItem(cargoDescription, amount);
		}
	}
}
