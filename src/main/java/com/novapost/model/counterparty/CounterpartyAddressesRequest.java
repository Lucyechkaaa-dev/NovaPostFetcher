package com.novapost.model.counterparty;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code CounterpartyGeneral/getCounterpartyAddresses}.
 * Docs: https://developers.novaposhta.ua/view/model/a28f4b04-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CounterpartyAddressesRequest(
		@JsonProperty("Ref") String ref,
		@JsonProperty("CounterpartyProperty") String counterpartyProperty
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String ref;
		private String counterpartyProperty;

		public Builder ref(String ref){ this.ref = ref; return this; }
		public Builder counterpartyProperty(String counterpartyProperty){ this.counterpartyProperty = counterpartyProperty; return this; }

		public CounterpartyAddressesRequest build(){
			return new CounterpartyAddressesRequest(ref, counterpartyProperty);
		}
	}
}
