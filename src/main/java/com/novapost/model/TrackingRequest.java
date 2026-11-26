package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code TrackingDocumentGeneral/getStatusDocuments}.
 * Docs: https://developers.novaposhta.ua/view/model/a99d2f28-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrackingRequest(
		@JsonProperty("Documents") List<TrackingDocumentRef> documents
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private List<TrackingDocumentRef> documents;

		public Builder documents(List<TrackingDocumentRef> documents){ this.documents = documents; return this; }

		public TrackingRequest build(){
			return new TrackingRequest(documents);
		}
	}
}
