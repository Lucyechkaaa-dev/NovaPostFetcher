package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateTimeDeserializer;

import java.time.LocalDateTime;

/**
 * Response model for {@code AdditionalServiceGeneral/save}, {@code AdditionalServiceGeneral/update}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ServicePricing(
		@JsonProperty("Pricing") JsonNode pricing,
		@JsonProperty("ScheduledDeliveryDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime scheduledDeliveryDate
){
}
