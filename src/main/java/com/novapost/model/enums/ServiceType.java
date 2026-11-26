package com.novapost.model.enums;

/** {@code ServiceType}: the delivery technology (from where to where). */
public enum ServiceType implements NpValue{
	DOORS_DOORS("DoorsDoors"),
	DOORS_WAREHOUSE("DoorsWarehouse"),
	WAREHOUSE_WAREHOUSE("WarehouseWarehouse"),
	WAREHOUSE_DOORS("WarehouseDoors"),
	UNKNOWN("");

	private final String value;

	ServiceType(String value){
		this.value = value;
	}

	@Override
	public String value(){
		return value;
	}

	public static ServiceType of(String text){
		return NpValue.parse(ServiceType.class, text, UNKNOWN);
	}
}
