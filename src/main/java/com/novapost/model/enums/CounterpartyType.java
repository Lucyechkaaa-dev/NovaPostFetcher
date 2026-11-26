package com.novapost.model.enums;

/** {@code CounterpartyType} and {@code RecipientType}: a private person or an organization. */
public enum CounterpartyType implements NpValue{
	PRIVATE_PERSON("PrivatePerson"),
	ORGANIZATION("Organization"),
	UNKNOWN("");

	private final String value;

	CounterpartyType(String value){
		this.value = value;
	}

	@Override
	public String value(){
		return value;
	}

	public static CounterpartyType of(String text){
		return NpValue.parse(CounterpartyType.class, text, UNKNOWN);
	}
}
