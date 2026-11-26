package com.novapost.model.document;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.novapost.model.support.NpDecimalDeserializer;
import com.novapost.model.support.NpDecimalSerializer;

import java.math.BigDecimal;

/**
 * Request model for the Nova Post API.
 * Docs: https://developers.novaposhta.ua/view/model/a90d323c-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RedeliveryCalculate(
		@JsonProperty("CargoType") String cargoType,
		@JsonProperty("Amount") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal amount
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String cargoType;
		private BigDecimal amount;

		public Builder cargoType(String cargoType){ this.cargoType = cargoType; return this; }
		public Builder amount(BigDecimal amount){ this.amount = amount; return this; }

		public RedeliveryCalculate build(){
			return new RedeliveryCalculate(cargoType, amount);
		}
	}
}
