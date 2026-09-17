package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SettlementStreetFilter(
    @JsonProperty("StreetName") String streetName,
    @JsonProperty("SettlementRef") String settlementRef,
    @JsonProperty("Page") Integer page,
    @JsonProperty("Limit") Integer limit
) {
    public static SettlementStreetFilter of(String settlementRef, String streetName) {
        return new SettlementStreetFilter(streetName, settlementRef, null, null);
    }
}
