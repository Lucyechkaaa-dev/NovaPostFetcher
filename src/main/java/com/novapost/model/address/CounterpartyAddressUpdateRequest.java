package com.novapost.model.address;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code AddressGeneral/update}.
 * Docs: https://developers.novaposhta.ua/view/model/a0cf0f5f-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CounterpartyAddressUpdateRequest(
		@JsonProperty("CounterpartyRef") String counterpartyRef,
		@JsonProperty("StreetRef") String streetRef,
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("Flat") String flat,
		@JsonProperty("Note") String note,
		@JsonProperty("Ref") String ref
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String counterpartyRef;
		private String streetRef;
		private String buildingNumber;
		private String flat;
		private String note;
		private String ref;

		public Builder counterpartyRef(String counterpartyRef){ this.counterpartyRef = counterpartyRef; return this; }
		public Builder streetRef(String streetRef){ this.streetRef = streetRef; return this; }
		public Builder buildingNumber(String buildingNumber){ this.buildingNumber = buildingNumber; return this; }
		public Builder flat(String flat){ this.flat = flat; return this; }
		public Builder note(String note){ this.note = note; return this; }
		public Builder ref(String ref){ this.ref = ref; return this; }

		public CounterpartyAddressUpdateRequest build(){
			return new CounterpartyAddressUpdateRequest(counterpartyRef, streetRef, buildingNumber, flat, note, ref);
		}
	}
}
