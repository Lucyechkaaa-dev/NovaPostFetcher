package com.novapost.model;

import java.util.List;

public enum PrintFormat {
	ZEBRA("orders", "printMarking100x100", true, "100x100 mm thermal label with QR code for Zebra printers"),
	ZEBRA_100X100("orders", "printMarking100x100", true, "100x100 mm thermal label with QR code for Zebra printers"),
	ZEBRA_85X85("orders", "printMarking85x85", true, "85x85 mm thermal label for Zebra printers"),
	A4("orders", "printMarkings", false, "A4 sheet with 6 marking labels and QR codes"),
	MARKING_A4("orders", "printMarkings", false, "A4 sheet with 6 marking labels and QR codes"),
	WAYBILL_A4("orders", "printDocument", false, "Full Express Waybill (TTN) on A4 sheet"),
	WAYBILL_A5("orders", "printDocument", false, "Full Express Waybill (TTN) on A5 sheet"),
	SCAN_SHEET("scanSheet", "printScanSheet", false, "Shipment registry handover sheet (Scan sheet)");

	private final String module;
	private final String endpointMethod;
	private final boolean zebraFlag;
	private final String description;

	PrintFormat(String module, String endpointMethod, boolean zebraFlag, String description) {
		this.module = module;
		this.endpointMethod = endpointMethod;
		this.zebraFlag = zebraFlag;
		this.description = description;
	}

	public String getModule() {
		return module;
	}

	public String getEndpointMethod() {
		return endpointMethod;
	}

	public boolean isZebraFlag() {
		return zebraFlag;
	}

	public String getDescription() {
		return description;
	}

	public String buildUrl(String apiKey, List<String> documentRefs) {
		if (apiKey == null || apiKey.isBlank()) {
			throw new IllegalArgumentException("API key must not be null or blank");
		}
		if (documentRefs == null || documentRefs.isEmpty()) {
			throw new IllegalArgumentException("Document reference list must not be empty");
		}

		String joinedRefs = String.join("/", documentRefs.stream().map(String::trim).toList());
		StringBuilder sb = new StringBuilder("https://my.novaposhta.ua/");

		if ("scanSheet".equals(module)) {
			sb.append("scanSheet/printScanSheet/refs/").append(joinedRefs);
		} else {
			sb.append("orders/").append(endpointMethod).append("/orders/").append(joinedRefs);
		}

		sb.append("/type/pdf/apiKey/").append(apiKey.trim());

		if (zebraFlag) {
			sb.append("/zebra");
		}
		return sb.toString();
	}
}
