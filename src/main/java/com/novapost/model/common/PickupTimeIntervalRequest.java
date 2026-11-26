package com.novapost.model.common;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateDeserializer;
import java.time.LocalDate;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code CommonGeneral/getPickupTimeIntervals}.
 * Docs: https://developers.novaposhta.ua/view/model/a55b2c64-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PickupTimeIntervalRequest(
		@JsonProperty("SenderCityRef") String senderCityRef,
		@JsonProperty("DateTime") @JsonFormat(pattern = "dd.MM.yyyy") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate dateTime
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String senderCityRef;
		private LocalDate dateTime;

		public Builder senderCityRef(String senderCityRef){ this.senderCityRef = senderCityRef; return this; }
		public Builder dateTime(LocalDate dateTime){ this.dateTime = dateTime; return this; }

		public PickupTimeIntervalRequest build(){
			return new PickupTimeIntervalRequest(senderCityRef, dateTime);
		}
	}
}
