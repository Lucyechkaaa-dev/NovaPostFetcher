package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code AdditionalServiceGeneral/CheckPossibilityCreateReturn}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReturnPossibilityRequest(
		@JsonProperty("Number") String number
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String number;

		public Builder number(String number){ this.number = number; return this; }

		public ReturnPossibilityRequest build(){
			return new ReturnPossibilityRequest(number);
		}
	}
}
