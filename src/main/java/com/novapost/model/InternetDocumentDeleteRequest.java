package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InternetDocumentDeleteRequest(
		@JsonProperty("DocumentRefs") List<String> documentRefs
){

	public static InternetDocumentDeleteRequest of(String ref){
		return new InternetDocumentDeleteRequest(List.of(ref));
	}

	public static InternetDocumentDeleteRequest of(List<String> refs){
		return new InternetDocumentDeleteRequest(refs);
	}
}
