package com.novapost.model.scansheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model for {@code ScanSheetGeneral/deleteScanSheet}.
 * Docs: https://developers.novaposhta.ua/view/model/a46fc4f4-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScanSheetDeleteResult(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Number") String number,
		@JsonProperty("Error") String error
){
}
