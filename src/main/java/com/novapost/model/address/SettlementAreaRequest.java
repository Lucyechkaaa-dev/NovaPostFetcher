package com.novapost.model.address;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for {@code AddressGeneral/getSettlementAreas}.
 * Docs: https://developers.novaposhta.ua/view/model/a0cf0f5f-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SettlementAreaRequest(
		@JsonProperty("Ref") String ref
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String ref;

		public Builder ref(String ref){ this.ref = ref; return this; }

		public SettlementAreaRequest build(){
			return new SettlementAreaRequest(ref);
		}
	}
}
