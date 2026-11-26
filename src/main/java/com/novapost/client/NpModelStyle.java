package com.novapost.client;

/**
 * Which model names the client sends.
 * <p>The portal's request examples use the {@code *General} names ({@code InternetDocumentGeneral}), while its prose says the
 * methods "work in the model" {@code InternetDocument}, {@code Address}, and so on. Both have to be supported until the
 * choice is confirmed against the live API (see {@code LiveContractSmokeTest}).
 */
public enum NpModelStyle{

	/** The names this library used before the {@code *General} ones: {@code Address}, {@code InternetDocument}, {@code TrackingDocument}... (default). */
	LEGACY,

	/** The names used in the documentation's request examples: {@code AddressGeneral}, {@code InternetDocumentGeneral}... */
	DOCUMENTED;

	public static NpModelStyle parse(String value){
		if(value == null || value.isBlank()){
			return LEGACY;
		}
		return switch(value.trim().toLowerCase()){
			case "documented", "general" -> DOCUMENTED;
			case "legacy" -> LEGACY;
			default -> throw new IllegalArgumentException("Unknown model style '" + value + "' (expected legacy or documented)");
		};
	}
}
