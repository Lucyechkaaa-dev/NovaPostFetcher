package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for {@code AdditionalServiceGeneral/CheckPossibilityCreateReturn}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReturnEditCheckRequest(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Address") String address
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String ref;
		private String address;

		public Builder ref(String ref){ this.ref = ref; return this; }
		public Builder address(String address){ this.address = address; return this; }

		public ReturnEditCheckRequest build(){
			return new ReturnEditCheckRequest(ref, address);
		}
	}
}
