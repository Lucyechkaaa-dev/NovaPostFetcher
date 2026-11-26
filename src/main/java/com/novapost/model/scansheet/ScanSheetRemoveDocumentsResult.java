package com.novapost.model.scansheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Response model for {@code ScanSheetGeneral/removeDocuments}.
 * Docs: https://developers.novaposhta.ua/view/model/a46fc4f4-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScanSheetRemoveDocumentsResult(
		@JsonProperty("DocumentRefs") JsonNode documentRefs
){
}
