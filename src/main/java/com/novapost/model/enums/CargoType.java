package com.novapost.model.enums;

/**
 * {@code CargoType}. {@code CommonGeneral/getCargoTypes} is the authoritative list; this enum holds the values that appear in the
 * documentation's examples. ({@code getDocumentPrice} lists only Cargo, Documents, TiresWheels and Pallet, while the postomat
 * examples of {@code save} use Parcel.)
 */
public enum CargoType implements NpValue{
	CARGO("Cargo"),
	DOCUMENTS("Documents"),
	TIRES_WHEELS("TiresWheels"),
	PALLET("Pallet"),
	PARCEL("Parcel"),
	MONEY("Money"),
	UNKNOWN("");

	private final String value;

	CargoType(String value){
		this.value = value;
	}

	@Override
	public String value(){
		return value;
	}

	public static CargoType of(String text){
		return NpValue.parse(CargoType.class, text, UNKNOWN);
	}
}
