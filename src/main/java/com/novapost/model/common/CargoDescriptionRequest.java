package com.novapost.model.common;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpIntegerDeserializer;

/**
 * Request model for {@code CommonGeneral/getCargoDescriptionList}.
 * Docs: https://developers.novaposhta.ua/view/model/a55b2c64-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CargoDescriptionRequest(
		@JsonProperty("FindByString") String findByString,
		@JsonProperty("Page") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer page
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String findByString;
		private Integer page;

		public Builder findByString(String findByString){ this.findByString = findByString; return this; }
		public Builder page(Integer page){ this.page = page; return this; }

		public CargoDescriptionRequest build(){
			return new CargoDescriptionRequest(findByString, page);
		}
	}
}
