package com.novapost.model.scansheet;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for {@code ScanSheetGeneral/getScanSheet}.
 * Docs: https://developers.novaposhta.ua/view/model/a46fc4f4-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScanSheetRequest(
		@JsonProperty("Ref") String ref,
		@JsonProperty("CounterpartyRef") String counterpartyRef
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String ref;
		private String counterpartyRef;

		public Builder ref(String ref){ this.ref = ref; return this; }
		public Builder counterpartyRef(String counterpartyRef){ this.counterpartyRef = counterpartyRef; return this; }

		public ScanSheetRequest build(){
			return new ScanSheetRequest(ref, counterpartyRef);
		}
	}
}
