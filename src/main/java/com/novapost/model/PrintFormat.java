package com.novapost.model;

import java.util.List;

/**
 * Ready-made print layouts. Each one maps to a {@link PrintRequest}; use {@link PrintRequest#builder()} directly for
 * {@code html} output, paging, or the {@code orders[]} URL form.
 */
public enum PrintFormat {
	/** Marking 100x100 for a Zebra thermal printer (documented: {@code printMarking100x100}, {@code zebra}). */
	ZEBRA(PrintRequest.Doc.MARKING_100X100, PrintRequest.Type.PDF, true, "100x100 mm thermal label with QR code for Zebra printers"),
	ZEBRA_100X100(PrintRequest.Doc.MARKING_100X100, PrintRequest.Type.PDF, true, "100x100 mm thermal label with QR code for Zebra printers"),
	/** @deprecated the documentation only describes the Zebra layout for 100x100 */
	@Deprecated
	ZEBRA_85X85(PrintRequest.Doc.MARKING_85X85, PrintRequest.Type.PDF, true, "85x85 mm thermal label for Zebra printers (not in the documentation)"),
	/** Marking on an A4 sheet with 6 labels (documented: {@code printMarking85x85}, {@code type/pdf8}). */
	A4(PrintRequest.Doc.MARKING_85X85, PrintRequest.Type.PDF8, false, "A4 sheet with 6 marking labels"),
	MARKING_A4(PrintRequest.Doc.MARKING_85X85, PrintRequest.Type.PDF8, false, "A4 sheet with 6 marking labels"),
	/** The older {@code printMarkings} method, which the current documentation no longer lists. */
	MARKINGS_LEGACY_A4(PrintRequest.Doc.MARKINGS_LEGACY, PrintRequest.Type.PDF, false, "A4 sheet via the legacy printMarkings method"),
	WAYBILL_A4(PrintRequest.Doc.DOCUMENT, PrintRequest.Type.PDF, false, "Full Express Waybill (TTN) on A4 sheet"),
	/** @deprecated the documentation has no A5 variant; this produces exactly the same URL as {@link #WAYBILL_A4} */
	@Deprecated
	WAYBILL_A5(PrintRequest.Doc.DOCUMENT, PrintRequest.Type.PDF, false, "Express waybill (no separate A5 layout exists in the documentation)"),
	SCAN_SHEET(PrintRequest.Doc.SCAN_SHEET, PrintRequest.Type.PDF, false, "Shipment registry handover sheet (Scan sheet)");

	private final PrintRequest.Doc doc;
	private final PrintRequest.Type type;
	private final boolean zebraFlag;
	private final String description;

	PrintFormat(PrintRequest.Doc doc, PrintRequest.Type type, boolean zebraFlag, String description) {
		this.doc = doc;
		this.type = type;
		this.zebraFlag = zebraFlag;
		this.description = description;
	}

	public String getModule() {
		return doc.module();
	}

	public String getEndpointMethod() {
		return doc.method();
	}

	public boolean isZebraFlag() {
		return zebraFlag;
	}

	public String getDescription() {
		return description;
	}

	public PrintRequest.Type getType() {
		return type;
	}

	/** The request for the given documents (a scan sheet takes exactly one). */
	public PrintRequest toRequest(List<String> documentRefs) {
		if (documentRefs == null || documentRefs.isEmpty()) {
			throw new IllegalArgumentException("Document reference list must not be empty");
		}
		return PrintRequest.builder().doc(doc).type(type).zebra(zebraFlag).ids(documentRefs).build();
	}

	public String buildUrl(String apiKey, List<String> documentRefs) {
		if (apiKey == null || apiKey.isBlank()) {
			throw new IllegalArgumentException("API key must not be null or blank");
		}
		return toRequest(documentRefs).toUrl(apiKey);
	}
}
