package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record WarehouseTypes(
                            @JsonProperty("Ref") String ref,
                            @JsonProperty("Description ") String description,
                            @JsonProperty("DescriptionRu") String descriptionRu){
}
