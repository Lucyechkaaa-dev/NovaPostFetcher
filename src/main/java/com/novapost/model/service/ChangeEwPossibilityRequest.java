package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for {@code AdditionalServiceGeneral/CheckPossibilityChangeEW}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChangeEwPossibilityRequest(
		@JsonProperty("IntDocNumber") String intDocNumber
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String intDocNumber;

		public Builder intDocNumber(String intDocNumber){ this.intDocNumber = intDocNumber; return this; }

		public ChangeEwPossibilityRequest build(){
			return new ChangeEwPossibilityRequest(intDocNumber);
		}
	}
}
