package com.novapost.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response model for {@code CommonGeneral/getCargoTypes}, {@code CommonGeneral/getBackwardDeliveryCargoTypes}, {@code CommonGeneral/getTypesOfPayersForRedelivery}, {@code CommonGeneral/getServiceTypes}.
 * Docs: https://developers.novaposhta.ua/view/model/a55b2c64-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record RefDescription(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Description") String description
){
}
