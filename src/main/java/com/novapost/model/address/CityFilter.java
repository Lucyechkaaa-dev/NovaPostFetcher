package com.novapost.model.address;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpIntegerDeserializer;

/**
 * Request model for {@code AddressGeneral/getCities}.
 * Docs: https://developers.novaposhta.ua/view/model/a0cf0f5f-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CityFilter(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Page") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer page,
		@JsonProperty("FindByString") String findByString,
		@JsonProperty("Limit") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer limit
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String ref;
		private Integer page;
		private String findByString;
		private Integer limit;

		public Builder ref(String ref){ this.ref = ref; return this; }
		public Builder page(Integer page){ this.page = page; return this; }
		public Builder findByString(String findByString){ this.findByString = findByString; return this; }
		public Builder limit(Integer limit){ this.limit = limit; return this; }

		public CityFilter build(){
			return new CityFilter(ref, page, findByString, limit);
		}
	}
}
