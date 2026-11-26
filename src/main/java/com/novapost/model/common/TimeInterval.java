package com.novapost.model.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpTimeDeserializer;

import java.time.LocalTime;

/**
 * Response model for {@code CommonGeneral/getTimeIntervals}.
 * Docs: https://developers.novaposhta.ua/view/model/a55b2c64-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TimeInterval(
		@JsonProperty("Number") String number,
		@JsonProperty("Start") @JsonDeserialize(using = NpTimeDeserializer.class) LocalTime start,
		@JsonProperty("End") @JsonDeserialize(using = NpTimeDeserializer.class) LocalTime end
){
}
