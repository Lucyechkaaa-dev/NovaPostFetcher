package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model for {@code AdditionalServiceGeneral/getReturnReasonsSubtypes}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ReturnReasonSubtype(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Description") String description,
		@JsonProperty("ReasonRef") String reasonRef
){
}
