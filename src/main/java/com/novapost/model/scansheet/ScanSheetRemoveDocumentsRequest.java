package com.novapost.model.scansheet;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code ScanSheetGeneral/removeDocuments}.
 * Docs: https://developers.novaposhta.ua/view/model/a46fc4f4-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScanSheetRemoveDocumentsRequest(
		@JsonProperty("DocumentRefs") List<String> documentRefs,
		@JsonProperty("Ref") String ref
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private List<String> documentRefs;
		private String ref;

		public Builder documentRefs(List<String> documentRefs){ this.documentRefs = documentRefs; return this; }
		public Builder ref(String ref){ this.ref = ref; return this; }

		public ScanSheetRemoveDocumentsRequest build(){
			return new ScanSheetRemoveDocumentsRequest(documentRefs, ref);
		}
	}
}
