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
 * Request model for the Nova Post API.
 * Docs: https://developers.novaposhta.ua/view/model/a99d2f28-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record TrackingDocumentRef(
		@JsonProperty("DocumentNumber") String documentNumber,
		@JsonProperty("Phone") String phone
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String documentNumber;
		private String phone;

		public Builder documentNumber(String documentNumber){ this.documentNumber = documentNumber; return this; }
		public Builder phone(String phone){ this.phone = phone; return this; }

		public TrackingDocumentRef build(){
			return new TrackingDocumentRef(documentNumber, phone);
		}
	}
}
