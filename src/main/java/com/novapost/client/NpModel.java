package com.novapost.client;

import java.util.Set;

/** Nova Poshta API models, with the names used in both {@link NpModelStyle styles}. */
public enum NpModel{

	ADDRESS("Address"),
	ADDRESS_GENERAL("AddressGeneral"),
	COMMON("Common"),
	/** Methods that exist only under the {@code CommonGeneral} name (for example {@code getPackListSpecial}). */
	COMMON_GENERAL("CommonGeneral"),
	COUNTERPARTY("Counterparty"),
	CONTACT_PERSON("ContactPerson"),
	INTERNET_DOCUMENT("InternetDocument"),
	TRACKING_DOCUMENT("TrackingDocument"),
	SCAN_SHEET("ScanSheet"),
	ADDITIONAL_SERVICE("AdditionalService"),
	/** Courier pickup: introduced with the {@code General} name only. */
	CAR_CALL("CarCallGeneral");

	private static final Set<String> LEGACY_NAMES = Set.of("Address", "Common", "Counterparty", "ContactPerson",
			"InternetDocument", "TrackingDocument", "ScanSheet", "AdditionalService");

	private final String legacyName;

	NpModel(String legacyName){
		this.legacyName = legacyName;
	}

	/** The name used by the {@link NpModelStyle#LEGACY} style. */
	public String legacyName(){
		return legacyName;
	}

	/** The name used in the documentation's request examples. */
	public String documentedName(){
		return documentedName(legacyName);
	}

	public String nameFor(NpModelStyle style){
		return style == NpModelStyle.DOCUMENTED ? documentedName() : legacyName;
	}

	/** Maps a legacy model name to its documented {@code *General} name; names that are already documented or unknown stay as they are. */
	public static String documentedName(String modelName){
		return modelName != null && LEGACY_NAMES.contains(modelName) ? modelName + "General" : modelName;
	}
}
