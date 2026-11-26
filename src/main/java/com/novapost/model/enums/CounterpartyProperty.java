package com.novapost.model.enums;

/** {@code CounterpartyProperty}: the role of a counterparty. */
public enum CounterpartyProperty implements NpValue{
	SENDER("Sender"),
	RECIPIENT("Recipient"),
	THIRD_PERSON("ThirdPerson"),
	UNKNOWN("");

	private final String value;

	CounterpartyProperty(String value){
		this.value = value;
	}

	@Override
	public String value(){
		return value;
	}

	public static CounterpartyProperty of(String text){
		return NpValue.parse(CounterpartyProperty.class, text, UNKNOWN);
	}
}
