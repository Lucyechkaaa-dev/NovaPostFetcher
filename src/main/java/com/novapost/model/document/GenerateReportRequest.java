package com.novapost.model.document;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateDeserializer;
import java.time.LocalDate;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code InternetDocumentGeneral/generateReport}.
 * Docs: https://developers.novaposhta.ua/view/model/a90d323c-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record GenerateReportRequest(
		@JsonProperty("DocumentRefs") List<String> documentRefs,
		@JsonProperty("Type") String type,
		@JsonProperty("DateTime") @JsonFormat(pattern = "dd.MM.yyyy") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate dateTime
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private List<String> documentRefs;
		private String type;
		private LocalDate dateTime;

		public Builder documentRefs(List<String> documentRefs){ this.documentRefs = documentRefs; return this; }
		public Builder type(String type){ this.type = type; return this; }
		public Builder dateTime(LocalDate dateTime){ this.dateTime = dateTime; return this; }

		public GenerateReportRequest build(){
			return new GenerateReportRequest(documentRefs, type, dateTime);
		}
	}
}
