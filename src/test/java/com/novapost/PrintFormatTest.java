package com.novapost;

import com.novapost.model.PrintFormat;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrintFormatTest {

	@Test
	void testZebraUrlGenerationSingleTtn() {
		String apiKey = "test-api-key-123";
		String ttn = "20400048799000";

		String url = PrintFormat.ZEBRA.buildUrl(apiKey, List.of(ttn));

		assertEquals(
				"https://my.novaposhta.ua/orders/printMarking100x100/orders/20400048799000/type/pdf/apiKey/test-api-key-123/zebra",
				url
		);
		assertTrue(PrintFormat.ZEBRA.isZebraFlag());
		assertEquals("printMarking100x100", PrintFormat.ZEBRA.getEndpointMethod());
	}

	@Test
	void testA4UrlGenerationSingleTtn() {
		String apiKey = "test-api-key-123";
		String ttn = "20400048799000";

		String url = PrintFormat.A4.buildUrl(apiKey, List.of(ttn));

		assertEquals(
				"https://my.novaposhta.ua/orders/printMarkings/orders/20400048799000/type/pdf/apiKey/test-api-key-123",
				url
		);
		assertFalse(PrintFormat.A4.isZebraFlag());
		assertEquals("printMarkings", PrintFormat.A4.getEndpointMethod());
	}

	@Test
	void testZebraUrlGenerationMultipleTtns() {
		String apiKey = "test-api-key-123";
		List<String> ttns = List.of("20400048799001", "20400048799002");

		String url = PrintFormat.ZEBRA.buildUrl(apiKey, ttns);

		assertEquals(
				"https://my.novaposhta.ua/orders/printMarking100x100/orders/20400048799001/20400048799002/type/pdf/apiKey/test-api-key-123/zebra",
				url
		);
	}

	@Test
	void testA4UrlGenerationMultipleTtns() {
		String apiKey = "test-api-key-123";
		List<String> ttns = List.of("20400048799001", "20400048799002");

		String url = PrintFormat.A4.buildUrl(apiKey, ttns);

		assertEquals(
				"https://my.novaposhta.ua/orders/printMarkings/orders/20400048799001/20400048799002/type/pdf/apiKey/test-api-key-123",
				url
		);
	}

	@Test
	void testWaybillUrlGeneration() {
		String apiKey = "test-api-key-123";
		String ttn = "20400048799000";

		String url = PrintFormat.WAYBILL_A4.buildUrl(apiKey, List.of(ttn));

		assertEquals(
				"https://my.novaposhta.ua/orders/printDocument/orders/20400048799000/type/pdf/apiKey/test-api-key-123",
				url
		);
	}

	@Test
	void testScanSheetUrlGeneration() {
		String apiKey = "test-api-key-123";
		String sheetRef = "b4f2c019-3221-11eb-810a-005056b24375";

		String url = PrintFormat.SCAN_SHEET.buildUrl(apiKey, List.of(sheetRef));

		assertEquals(
				"https://my.novaposhta.ua/scanSheet/printScanSheet/refs/b4f2c019-3221-11eb-810a-005056b24375/type/pdf/apiKey/test-api-key-123",
				url
		);
	}

	@Test
	void testInvalidParametersValidation() {
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.ZEBRA.buildUrl(null, List.of("20400048799000")));
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.ZEBRA.buildUrl("   ", List.of("20400048799000")));
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.ZEBRA.buildUrl("valid-key", null));
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.ZEBRA.buildUrl("valid-key", List.of()));
	}
}
