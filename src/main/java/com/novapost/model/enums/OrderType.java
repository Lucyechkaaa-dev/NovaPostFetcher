package com.novapost.model.enums;

/** {@code OrderType}: the constant that tells an {@code AdditionalServiceGeneral} request which service it orders. */
public enum OrderType implements NpValue{
	REDIRECTING("orderRedirecting"),
	CARGO_RETURN("orderCargoReturn"),
	CHANGE_EW("orderChangeEW"),
	UNKNOWN("");

	private final String value;

	OrderType(String value){
		this.value = value;
	}

	@Override
	public String value(){
		return value;
	}

	public static OrderType of(String text){
		return NpValue.parse(OrderType.class, text, UNKNOWN);
	}
}
