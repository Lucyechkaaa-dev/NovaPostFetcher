package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for {@code AdditionalServiceGeneral/getReturnReasonsSubtypes}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReturnReasonSubtypesRequest(
		@JsonProperty("ReasonRef") String reasonRef
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String reasonRef;

		public Builder reasonRef(String reasonRef){ this.reasonRef = reasonRef; return this; }

		public ReturnReasonSubtypesRequest build(){
			return new ReturnReasonSubtypesRequest(reasonRef);
		}
	}
}
