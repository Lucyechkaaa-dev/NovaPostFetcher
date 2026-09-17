package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SettlementStreet(
    @JsonProperty("Ref") String ref,
    @JsonProperty("Description") String description,
    @JsonProperty("StreetsType") String streetsType,
    @JsonProperty("StreetsTypeDescription") String streetsTypeDescription,
    @JsonProperty("SettlementRef") String settlementRef,
    @JsonProperty("SettlementStreetRef") String settlementStreetRef,
    @JsonProperty("Present") String present
) {}
