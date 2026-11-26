package com.novapost.model.address;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for {@code AddressGeneral/save}.
 * Docs: https://developers.novaposhta.ua/view/model/a0cf0f5f-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CounterpartyAddressSaveRequest(
		@JsonProperty("CounterpartyRef") String counterpartyRef,
		@JsonProperty("StreetRef") String streetRef,
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("Flat") String flat,
		@JsonProperty("Note") String note
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

		public Builder counterpartyRef(String counterpartyRef){ this.counterpartyRef = counterpartyRef; return this; }
		public Builder streetRef(String streetRef){ this.streetRef = streetRef; return this; }
		public Builder buildingNumber(String buildingNumber){ this.buildingNumber = buildingNumber; return this; }
		public Builder flat(String flat){ this.flat = flat; return this; }
		public Builder note(String note){ this.note = note; return this; }

		public CounterpartyAddressSaveRequest build(){
			return new CounterpartyAddressSaveRequest(counterpartyRef, streetRef, buildingNumber, flat, note);
		}
	}
}
