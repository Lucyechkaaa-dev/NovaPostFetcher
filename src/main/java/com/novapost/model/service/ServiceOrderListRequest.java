package com.novapost.model.service;

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
 * Request model for {@code AdditionalServiceGeneral/getRedirectionOrdersList}, {@code AdditionalServiceGeneral/getReturnOrdersList}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ServiceOrderListRequest(
		@JsonProperty("Number") String number,
		@JsonProperty("Ref") String ref,
		@JsonProperty("BeginDate") @JsonFormat(pattern = "dd.MM.yyyy") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate beginDate,
		@JsonProperty("EndDate") @JsonFormat(pattern = "dd.MM.yyyy") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate endDate,
		@JsonProperty("Page") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer page,
		@JsonProperty("Limit") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer limit
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String number;
		private String ref;
		private LocalDate beginDate;
		private LocalDate endDate;
		private Integer page;
		private Integer limit;

		public Builder number(String number){ this.number = number; return this; }
		public Builder ref(String ref){ this.ref = ref; return this; }
		public Builder beginDate(LocalDate beginDate){ this.beginDate = beginDate; return this; }
		public Builder endDate(LocalDate endDate){ this.endDate = endDate; return this; }
		public Builder page(Integer page){ this.page = page; return this; }
		public Builder limit(Integer limit){ this.limit = limit; return this; }

		public ServiceOrderListRequest build(){
			return new ServiceOrderListRequest(number, ref, beginDate, endDate, page, limit);
		}
	}
}
