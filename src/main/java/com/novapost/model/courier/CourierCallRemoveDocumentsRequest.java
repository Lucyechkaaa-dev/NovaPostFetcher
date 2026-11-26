package com.novapost.model.courier;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code CarCallGeneral/removeDocuments}.
 * Docs: https://developers.novaposhta.ua/view/model/96141693-eaae-11ef-84e0-48df37b921da
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourierCallRemoveDocumentsRequest(
		@JsonProperty("LinkedDocuments") List<String> linkedDocuments
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private List<String> linkedDocuments;

		public Builder linkedDocuments(List<String> linkedDocuments){ this.linkedDocuments = linkedDocuments; return this; }

		public CourierCallRemoveDocumentsRequest build(){
			return new CourierCallRemoveDocumentsRequest(linkedDocuments);
		}
	}
}
