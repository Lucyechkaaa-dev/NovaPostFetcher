package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NpResponse<T>(
		@JsonProperty("success") boolean success,
		@JsonProperty("data") List<T> data,
		@JsonProperty("errors") List<String> errors,
		@JsonProperty("warnings") List<String> warnings,
		@JsonProperty("info") Object info,
		@JsonProperty("messageCodes") List<String> messageCodes,
		@JsonProperty("errorCodes") List<String> errorCodes
){

	public boolean hasErrors(){
		return !success || (errors != null && !errors.isEmpty());
	}
}
