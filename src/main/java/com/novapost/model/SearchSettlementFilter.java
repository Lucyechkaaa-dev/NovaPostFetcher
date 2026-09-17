package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SearchSettlementFilter(
    @JsonProperty("CityName") String cityName,
    @JsonProperty("Limit") Integer limit,
    @JsonProperty("Page") Integer page
) {
    public static SearchSettlementFilter of(String cityName, int page, int limit) {
        return new SearchSettlementFilter(cityName, limit, page);
    }
}
