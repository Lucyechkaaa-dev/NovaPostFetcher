package com.novapost.model.courier;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateDeserializer;

import java.time.LocalDate;

/**
 * Response model for {@code CarCallGeneral/getAvailableOrdersList}.
 * Docs: https://developers.novaposhta.ua/view/model/96141693-eaae-11ef-84e0-48df37b921da
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record AvailableCourierCall(
		@JsonProperty("Number") String number,
		@JsonProperty("Ref") String ref,
		@JsonProperty("TimeIntervalDescription") String timeIntervalDescription,
		@JsonProperty("PreferredDeliveryDate") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate preferredDeliveryDate,
		@JsonProperty("SenderAddressDescription") String senderAddressDescription
){
}
