package com.novapost.model.scansheet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request model for {@code ScanSheetGeneral/deleteScanSheet}.
 * Docs: https://developers.novaposhta.ua/view/model/a46fc4f4-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScanSheetDeleteRequest(
		@JsonProperty("ScanSheetRefs") List<String> scanSheetRefs
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private List<String> scanSheetRefs;

		public Builder scanSheetRefs(List<String> scanSheetRefs){ this.scanSheetRefs = scanSheetRefs; return this; }

		public ScanSheetDeleteRequest build(){
			return new ScanSheetDeleteRequest(scanSheetRefs);
		}
	}
}
