package com.novapost.model.courier;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpDateDeserializer;
import com.novapost.model.support.NpIntegerDeserializer;
import java.time.LocalDate;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code CarCallGeneral/getOrdersListCourierCall}.
 * Docs: https://developers.novaposhta.ua/view/model/96141693-eaae-11ef-84e0-48df37b921da
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourierCallFilter(
		@JsonProperty("StatusCode") String statusCode,
		@JsonProperty("ContactSenderPhone") String contactSenderPhone,
		@JsonProperty("DateTime") @JsonFormat(pattern = "dd.MM.yyyy") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate dateTime,
		@JsonProperty("Page") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer page,
		@JsonProperty("Limit") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer limit,
		@JsonProperty("SenderRef") String senderRef,
		@JsonProperty("Number") String number,
		@JsonProperty("PreferredDeliveryDate") @JsonFormat(pattern = "dd.MM.yyyy") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate preferredDeliveryDate
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String statusCode;
		private String contactSenderPhone;
		private LocalDate dateTime;
		private Integer page;
		private Integer limit;
		private String senderRef;
		private String number;
		private LocalDate preferredDeliveryDate;

		public Builder statusCode(String statusCode){ this.statusCode = statusCode; return this; }
		public Builder contactSenderPhone(String contactSenderPhone){ this.contactSenderPhone = contactSenderPhone; return this; }
		public Builder dateTime(LocalDate dateTime){ this.dateTime = dateTime; return this; }
		public Builder page(Integer page){ this.page = page; return this; }
		public Builder limit(Integer limit){ this.limit = limit; return this; }
		public Builder senderRef(String senderRef){ this.senderRef = senderRef; return this; }
		public Builder number(String number){ this.number = number; return this; }
		public Builder preferredDeliveryDate(LocalDate preferredDeliveryDate){ this.preferredDeliveryDate = preferredDeliveryDate; return this; }

		public CourierCallFilter build(){
			return new CourierCallFilter(statusCode, contactSenderPhone, dateTime, page, limit, senderRef, number, preferredDeliveryDate);
		}
	}
}
