package com.novapost.controller;

import java.util.List;

public class NovaPostApiException extends RuntimeException{

	private final List<String> errors;

	public NovaPostApiException(String message){
		super(message);
		this.errors = List.of();
	}

	public NovaPostApiException(String message, Throwable cause){
		super(message, cause);
		this.errors = List.of();
	}

	public NovaPostApiException(String message, List<String> errors){
		super(message + (errors != null && !errors.isEmpty() ? ": " + String.join(", ", errors) : ""));
		this.errors = errors != null ? List.copyOf(errors) : List.of();
	}

	public List<String> getErrors(){
		return errors;
	}
}
