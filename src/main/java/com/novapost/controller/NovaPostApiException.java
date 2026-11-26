package com.novapost.controller;

import java.util.List;

public class NovaPostApiException extends RuntimeException{

	private final List<String> errors;
	private final List<String> errorCodes;

	public NovaPostApiException(String message){
		super(message);
		this.errors = List.of();
		this.errorCodes = List.of();
	}

	public NovaPostApiException(String message, Throwable cause){
		super(message, cause);
		this.errors = List.of();
		this.errorCodes = List.of();
	}

	public NovaPostApiException(String message, List<String> errors){
		this(message, errors, List.of());
	}

	/**
	 * @param errorCodes the numeric codes from {@code errorCodes}; {@code CommonGeneral/getMessageCodeText} explains them
	 */
	public NovaPostApiException(String message, List<String> errors, List<String> errorCodes){
		super(message + (errors != null && !errors.isEmpty() ? ": " + String.join(", ", errors) : "")
				+ (errorCodes != null && !errorCodes.isEmpty() ? " [codes: " + String.join(", ", errorCodes) + "]" : ""));
		this.errors = errors != null ? List.copyOf(errors) : List.of();
		this.errorCodes = errorCodes != null ? List.copyOf(errorCodes) : List.of();
	}

	public List<String> getErrors(){
		return errors;
	}

	public List<String> getErrorCodes(){
		return errorCodes;
	}
}
