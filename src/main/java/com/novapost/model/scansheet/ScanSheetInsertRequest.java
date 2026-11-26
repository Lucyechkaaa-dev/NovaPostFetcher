package com.novapost.model.scansheet;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateDeserializer;

import java.time.LocalDate;
import java.util.List;

/**
 * Request model for {@code ScanSheetGeneral/insertDocuments}.
 * Docs: https://developers.novaposhta.ua/view/model/a46fc4f4-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScanSheetInsertRequest(
		@JsonProperty("DocumentRefs") List<String> documentRefs,
		@JsonProperty("Ref") String ref,
		@JsonProperty("Date") @JsonFormat(pattern = "dd.MM.yyyy") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate date
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private List<String> documentRefs;
		private String ref;
		private LocalDate date;

		public Builder documentRefs(List<String> documentRefs){ this.documentRefs = documentRefs; return this; }
		public Builder ref(String ref){ this.ref = ref; return this; }
		public Builder date(LocalDate date){ this.date = date; return this; }

		public ScanSheetInsertRequest build(){
			return new ScanSheetInsertRequest(documentRefs, ref, date);
		}
	}
}
