package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpMessageListDeserializer;

import java.util.List;

/**
 * The envelope of every response: the API always answers HTTP 200, so errors are only visible here.
 * <p>The message lists are read tolerantly: next to plain strings the live API sends objects such as
 * {@code {"ID_20400048799000":"Please enter a valid phone number..."}} (see {@link NpMessageListDeserializer}).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NpResponse<T>(
		@JsonProperty("success") boolean success,
		@JsonProperty("data") List<T> data,
		@JsonProperty("errors") @JsonDeserialize(using = NpMessageListDeserializer.class) List<String> errors,
		@JsonProperty("warnings") @JsonDeserialize(using = NpMessageListDeserializer.class) List<String> warnings,
		@JsonProperty("info") Object info,
		@JsonProperty("messageCodes") @JsonDeserialize(using = NpMessageListDeserializer.class) List<String> messageCodes,
		@JsonProperty("errorCodes") @JsonDeserialize(using = NpMessageListDeserializer.class) List<String> errorCodes,
		@JsonProperty("warningCodes") @JsonDeserialize(using = NpMessageListDeserializer.class) List<String> warningCodes,
		@JsonProperty("infoCodes") @JsonDeserialize(using = NpMessageListDeserializer.class) List<String> infoCodes
){

	/** Envelope without the warning/info code lists (the shape before they were modelled). */
	public NpResponse(boolean success, List<T> data, List<String> errors, List<String> warnings, Object info,
	                  List<String> messageCodes, List<String> errorCodes){
		this(success, data, errors, warnings, info, messageCodes, errorCodes, null, null);
	}

	public boolean hasErrors(){
		return !success || (errors != null && !errors.isEmpty());
	}

	public boolean hasWarnings(){
		return warnings != null && !warnings.isEmpty();
	}
}
