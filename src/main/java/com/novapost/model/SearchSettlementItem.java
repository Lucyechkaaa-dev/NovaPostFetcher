package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpIntegerDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SearchSettlementItem(
		@JsonProperty("TotalCount") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer totalCount,
		@JsonProperty("Addresses") List<SearchSettlementAddress> addresses,
		// the documented example repeats the fields of the first address on the wrapper itself
		@JsonProperty("StreetsAvailability") Boolean streetsAvailability,
		@JsonProperty("RegionTypesCode") String regionTypesCode,
		@JsonProperty("AddressDeliveryAllowed") Boolean addressDeliveryAllowed,
		@JsonProperty("Warehouses") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer warehouses,
		@JsonProperty("MainDescription") String mainDescription,
		@JsonProperty("Area") String area,
		@JsonProperty("Region") String region,
		@JsonProperty("SettlementTypeCode") String settlementTypeCode,
		@JsonProperty("Ref") String ref,
		@JsonProperty("DeliveryCity") String deliveryCity,
		@JsonProperty("Present") String present,
		@JsonProperty("RegionTypes") String regionTypes,
		@JsonProperty("ParentRegionCode") String parentRegionCode,
		@JsonProperty("ParentRegionTypes") String parentRegionTypes
){

	/** The shape before the wrapper-level fields of the documented example were modelled. */
	public SearchSettlementItem(Integer totalCount, List<SearchSettlementAddress> addresses){
		this(totalCount, addresses, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	public record SearchSettlementAddress(
			@JsonProperty("AddressDeliveryAllowed") Boolean addressDeliveryAllowed,
			@JsonProperty("Area") String area,
			@JsonProperty("DeliveryCity") String deliveryCity,
			@JsonProperty("MainDescription") String mainDescription,
			@JsonProperty("ParentRegionCode") String parentRegionCode,
			@JsonProperty("Present") String present,
			@JsonProperty("Ref") String ref,
			@JsonProperty("Region") String region,
			@JsonProperty("SettlementTypeCode") String settlementTypeCode,
			@JsonProperty("StreetsAvailability") Boolean streetsAvailability,
			@JsonProperty("Warehouses") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer warehouses,
			@JsonProperty("RegionTypesCode") String regionTypesCode,
			@JsonProperty("RegionTypes") String regionTypes,
			@JsonProperty("ParentRegionTypes") String parentRegionTypes
	){

		/** The shape before the region type fields were modelled. */
		public SearchSettlementAddress(Boolean addressDeliveryAllowed, String area, String deliveryCity, String mainDescription,
		                               String parentRegionCode, String present, String ref, String region, String settlementTypeCode,
		                               Boolean streetsAvailability, Integer warehouses){
			this(addressDeliveryAllowed, area, deliveryCity, mainDescription, parentRegionCode, present, ref, region,
					settlementTypeCode, streetsAvailability, warehouses, null, null, null);
		}
	}
}
