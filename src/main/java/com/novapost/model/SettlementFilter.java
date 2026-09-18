package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SettlementFilter(
		@JsonProperty("FindByString") String findByString,
		@JsonProperty("Ref") String ref,
		@JsonProperty("RegionRef") String regionRef,
		@JsonProperty("Page") Integer page,
		@JsonProperty("Limit") Integer limit,
		@JsonProperty("Warehouse") String warehouse
){

	public static SettlementFilter byString(String query, int page, int limit){
		return new SettlementFilter(query, null, null, page, limit, null);
	}

	public static SettlementFilter byRef(String ref){
		return new SettlementFilter(null, ref, null, null, null, null);
	}
}
