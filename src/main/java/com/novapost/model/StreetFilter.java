package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StreetFilter(
		@JsonProperty("CityRef") String cityRef,
		@JsonProperty("FindByString") String findByString,
		@JsonProperty("Page") Integer page,
		@JsonProperty("Limit") Integer limit
){

	public static StreetFilter byCity(String cityRef, int page, int limit){
		return new StreetFilter(cityRef, null, page, limit);
	}

	public static StreetFilter byCityAndName(String cityRef, String streetName, int page, int limit){
		return new StreetFilter(cityRef, streetName, page, limit);
	}

	public static StreetFilter of(String cityRef){
		return new StreetFilter(cityRef, null, 1, 500);
	}
}
