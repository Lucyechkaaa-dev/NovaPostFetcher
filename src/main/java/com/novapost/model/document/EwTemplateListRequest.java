package com.novapost.model.document;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.novapost.model.support.NpBooleanDeserializer;
import com.novapost.model.support.NpIntegerDeserializer;
import com.novapost.model.support.NpTextBooleanSerializer;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code InternetDocumentGeneral/getEWTemplateList}.
 * Docs: https://developers.novaposhta.ua/view/model/a90d323c-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record EwTemplateListRequest(
		@JsonProperty("Page") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer page,
		@JsonProperty("Limit") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer limit,
		@JsonProperty("PickupNumber") String pickupNumber,
		@JsonProperty("State") String state,
		@JsonProperty("IsPickupTable") @JsonSerialize(using = NpTextBooleanSerializer.class) @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean isPickupTable
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private Integer page;
		private Integer limit;
		private String pickupNumber;
		private String state;
		private Boolean isPickupTable;

		public Builder page(Integer page){ this.page = page; return this; }
		public Builder limit(Integer limit){ this.limit = limit; return this; }
		public Builder pickupNumber(String pickupNumber){ this.pickupNumber = pickupNumber; return this; }
		public Builder state(String state){ this.state = state; return this; }
		public Builder isPickupTable(Boolean isPickupTable){ this.isPickupTable = isPickupTable; return this; }

		public EwTemplateListRequest build(){
			return new EwTemplateListRequest(page, limit, pickupNumber, state, isPickupTable);
		}
	}
}
