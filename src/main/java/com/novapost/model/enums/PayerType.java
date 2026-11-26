package com.novapost.model.enums;

/** {@code PayerType}: who pays for the delivery. */
public enum PayerType implements NpValue{
	SENDER("Sender"),
	RECIPIENT("Recipient"),
	THIRD_PERSON("ThirdPerson"),
	UNKNOWN("");

	private final String value;

	PayerType(String value){
		this.value = value;
	}

	@Override
	public String value(){
		return value;
	}

	public static PayerType of(String text){
		return NpValue.parse(PayerType.class, text, UNKNOWN);
	}
}
