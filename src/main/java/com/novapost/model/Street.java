package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Street(
		@JsonProperty("Ref") String ref,
		@JsonProperty("Description") String description,
		@JsonProperty("StreetsTypeRef") String streetsTypeRef,
		@JsonProperty("StreetsType") String streetsType,
		@JsonProperty("CityRef") String cityRef
){

	public Street withCityRef(String cityRef){
		return new Street(ref, description, streetsTypeRef, streetsType, cityRef);
	}
}
