package com.novapost.model.courier;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for {@code CarCallGeneral/getAvailableOrdersList}.
 * Docs: https://developers.novaposhta.ua/view/model/96141693-eaae-11ef-84e0-48df37b921da
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AvailableCourierCallsRequest(
		@JsonProperty("Number") String number
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String number;

		public Builder number(String number){ this.number = number; return this; }

		public AvailableCourierCallsRequest build(){
			return new AvailableCourierCallsRequest(number);
		}
	}
}
