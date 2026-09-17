package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SearchSettlementItem(
    @JsonProperty("TotalCount") Integer totalCount,
    @JsonProperty("Addresses") List<SearchSettlementAddress> addresses
) {
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
        @JsonProperty("Warehouses") Integer warehouses
    ) {}
}
