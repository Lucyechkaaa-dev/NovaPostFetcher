package com.novapost.model.scansheet;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpBooleanDeserializer;
import com.novapost.model.support.NpDateTimeDeserializer;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

/**
 * Response model for {@code ScanSheetGeneral/getScanSheetList}.
 * Docs: https://developers.novaposhta.ua/view/model/a46fc4f4-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScanSheetListItem(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Number") String number,
		@JsonProperty("DateTime") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateTime,
		@JsonProperty("Printed") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean printed
){
}
