package com.novapost.model.scansheet;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateTimeDeserializer;
import com.novapost.model.support.NpIntegerDeserializer;

import java.time.LocalDateTime;

/**
 * Response model for {@code ScanSheetGeneral/getScanSheet}.
 * Docs: https://developers.novaposhta.ua/view/model/a46fc4f4-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ScanSheet(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Number") String number,
		@JsonProperty("DateTime") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateTime,
		@JsonProperty("Count") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer count,
		@JsonProperty("CitySenderRef") String citySenderRef,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("SenderAddressRef") String senderAddressRef,
		@JsonProperty("SenderAddress") String senderAddress,
		@JsonProperty("SenderRef") String senderRef,
		@JsonProperty("Sender") String sender
){
}
