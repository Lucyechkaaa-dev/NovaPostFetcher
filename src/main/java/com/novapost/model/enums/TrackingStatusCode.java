package com.novapost.model.enums;

/** The tracking status codes listed in the "Трекінг" section of the documentation ({@code StatusCode} of {@code getStatusDocuments}). */
public enum TrackingStatusCode implements NpValue{
	/** The sender created the waybill but has not handed the shipment over yet. */
	CREATED_NOT_HANDED_OVER("1"),
	DELETED("2"),
	NUMBER_NOT_FOUND("3"),
	/** The shipment is in the sender's city (inter-regional shipments). */
	IN_SENDER_CITY("4"),
	/** The shipment is in the city (Локал стандарт / Локал експрес, delivery within the city). */
	IN_CITY_LOCAL("41"),
	EN_ROUTE_TO_CITY("5"),
	IN_DESTINATION_CITY("6"),
	ARRIVED_AT_BRANCH("7"),
	ARRIVED_AT_BRANCH_LOADED_TO_POSTOMAT("8"),
	RECEIVED("9"),
	/** Received; the money transfer can be collected at the branch cash desk. */
	RECEIVED_MONEY_AT_CASH_DESK("10"),
	/** Received; the money transfer has been paid out. */
	RECEIVED_MONEY_PAID_OUT("11"),
	BEING_ASSEMBLED("12"),
	EN_ROUTE_TO_UKRAINE("15"),
	EN_ROUTE_TO_RECIPIENT("101"),
	/** The recipient refused; a return order was created by the sender. */
	REFUSED_RETURN_ORDERED("102"),
	REFUSED("103"),
	ADDRESS_CHANGED("104"),
	STORAGE_STOPPED("105"),
	RECEIVED_BACKWARD_WAYBILL_CREATED("106"),
	MOVED_FROM_PUDO_TO_MAIN_BRANCH("107"),
	DELIVERY_ATTEMPT_FAILED("111"),
	DELIVERY_DATE_MOVED_BY_RECIPIENT("112"),
	DESTROYED_BY_HOSTILE_ATTACK("124"),
	UNKNOWN("");

	private final String code;

	TrackingStatusCode(String code){
		this.code = code;
	}

	@Override
	public String value(){
		return code;
	}

	public static TrackingStatusCode of(String code){
		return NpValue.parse(TrackingStatusCode.class, code, UNKNOWN);
	}

	/** True for the statuses after which the shipment has reached the recipient (9, 10, 11). */
	public boolean isDelivered(){
		return this == RECEIVED || this == RECEIVED_MONEY_AT_CASH_DESK || this == RECEIVED_MONEY_PAID_OUT;
	}
}
