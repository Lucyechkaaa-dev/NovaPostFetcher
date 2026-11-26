package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Request of {@code InternetDocumentGeneral/delete}. The documentation shows {@code DocumentRefs} as a plain string
 * ({@code "DocumentRefs": "0000..."}), so one Ref is sent as a string; several Refs are sent as an array.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InternetDocumentDeleteRequest(
		@JsonProperty("DocumentRefs") Object documentRefs
){

	/** One Ref goes out as a string (the documented form), several as an array. */
	public InternetDocumentDeleteRequest(List<String> refs){
		this(refs != null && refs.size() == 1 ? (Object) refs.getFirst() : refs);
	}

	public static InternetDocumentDeleteRequest of(String ref){
		return new InternetDocumentDeleteRequest((Object) ref);
	}

	public static InternetDocumentDeleteRequest of(List<String> refs){
		return new InternetDocumentDeleteRequest(refs);
	}
}
