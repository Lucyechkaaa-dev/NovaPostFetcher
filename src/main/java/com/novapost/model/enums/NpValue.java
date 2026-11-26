package com.novapost.model.enums;

/**
 * An API value with a fixed set of documented spellings. Every enum has an {@code UNKNOWN} constant: the API may add values
 * later, and a new value must not break parsing, so {@link #parse} falls back to {@code UNKNOWN} instead of throwing.
 */
public interface NpValue{

	/** The exact text the API uses, for example {@code WarehouseWarehouse}. */
	String value();

	/** Finds the constant with the given API text (case-insensitive), or {@code unknown}. */
	static <E extends Enum<E> & NpValue> E parse(Class<E> type, String text, E unknown){
		if(text != null){
			String t = text.trim();
			for(E e : type.getEnumConstants()){
				if(e != unknown && e.value().equalsIgnoreCase(t)){
					return e;
				}
			}
		}
		return unknown;
	}
}
