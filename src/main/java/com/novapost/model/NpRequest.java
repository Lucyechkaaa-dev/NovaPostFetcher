package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record NpRequest<T>(
		@JsonProperty("apiKey") String apiKey,
		@JsonProperty("modelName") String modelName,
		@JsonProperty("calledMethod") String calledMethod,
		@JsonProperty("methodProperties") T methodProperties
){

}
