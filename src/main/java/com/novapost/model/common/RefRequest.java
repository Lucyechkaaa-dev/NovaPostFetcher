package com.novapost.model.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code AddressGeneral/delete}, {@code CounterpartyGeneral/delete}, {@code CounterpartyGeneral/getCounterpartyOptions}, {@code ContactPersonGeneral/delete}, {@code AdditionalServiceGeneral/delete}, {@code CarCallGeneral/deleteCourierCall}.
 * Docs: https://developers.novaposhta.ua/view/model/96141693-eaae-11ef-84e0-48df37b921da
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RefRequest(
		@JsonProperty("Ref") String ref
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String ref;

		public Builder ref(String ref){ this.ref = ref; return this; }

		public RefRequest build(){
			return new RefRequest(ref);
		}
	}
}
