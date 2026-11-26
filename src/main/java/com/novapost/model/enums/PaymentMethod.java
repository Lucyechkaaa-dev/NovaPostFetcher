package com.novapost.model.enums;

/** {@code PaymentMethod}: the form of payment. */
public enum PaymentMethod implements NpValue{
	CASH("Cash"),
	NON_CASH("NonCash"),
	UNKNOWN("");

	private final String value;

	PaymentMethod(String value){
		this.value = value;
	}

	@Override
	public String value(){
		return value;
	}

	public static PaymentMethod of(String text){
		return NpValue.parse(PaymentMethod.class, text, UNKNOWN);
	}
}
