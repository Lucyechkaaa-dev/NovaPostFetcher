package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.novapost.model.support.NpBooleanDeserializer;
import com.novapost.model.support.NpFlagSerializer;

/** Request of {@code AddressGeneral/getSettlements} (the directory of Ukrainian settlements). */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SettlementFilter(
		@JsonProperty("FindByString") String findByString,
		@JsonProperty("Ref") String ref,
		@JsonProperty("RegionRef") String regionRef,
		@JsonProperty("Page") Integer page,
		@JsonProperty("Limit") Integer limit,
		@JsonProperty("Warehouse") @JsonSerialize(using = NpFlagSerializer.class) @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean warehouse,
		@JsonProperty("AreaRef") String areaRef
){

	/** The shape before the {@code AreaRef} filter was modelled. */
	public SettlementFilter(String findByString, String ref, String regionRef, Integer page, Integer limit, Boolean warehouse){
		this(findByString, ref, regionRef, page, limit, warehouse, null);
	}

	public static SettlementFilter byString(String query, int page, int limit){
		return new SettlementFilter(query, null, null, page, limit, null);
	}

	public static SettlementFilter byRef(String ref){
		return new SettlementFilter(null, ref, null, null, null, null);
	}

	/** Settlements of one region (oblast); the Ref comes from {@code getAreas} or {@code getSettlementAreas}. */
	public static SettlementFilter byArea(String areaRef, int page, int limit){
		return new SettlementFilter(null, null, null, page, limit, null, areaRef);
	}
}
