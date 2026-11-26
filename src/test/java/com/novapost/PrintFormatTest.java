package com.novapost;

import com.novapost.model.PrintFormat;
import com.novapost.model.PrintRequest;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/** URL forms follow the "Друк" section of the documentation (PrintGeneral). */
class PrintFormatTest {

	private static final String KEY = "test-api-key-123";

	@Test
	void testZebraUrlGenerationSingleTtn() {
		String url = PrintFormat.ZEBRA.buildUrl(KEY, List.of("20400048799000"));

		assertEquals(
				"https://my.novaposhta.ua/orders/printMarking100x100/orders/20400048799000/type/pdf/apiKey/test-api-key-123/zebra",
				url
		);
		assertTrue(PrintFormat.ZEBRA.isZebraFlag());
		assertEquals("printMarking100x100", PrintFormat.ZEBRA.getEndpointMethod());
	}

	@Test
	void testA4IsTheDocumented85x85SheetWithSixLabels() {
		String url = PrintFormat.A4.buildUrl(KEY, List.of("20400048799000"));

		assertEquals(
				"https://my.novaposhta.ua/orders/printMarking85x85/orders/20400048799000/type/pdf8/apiKey/test-api-key-123",
				url
		);
		assertFalse(PrintFormat.A4.isZebraFlag());
		assertEquals("printMarking85x85", PrintFormat.A4.getEndpointMethod());
		assertEquals(url, PrintFormat.MARKING_A4.buildUrl(KEY, List.of("20400048799000")));
	}

	@Test
	void testLegacyMarkingsStillAvailable() {
		assertEquals(
				"https://my.novaposhta.ua/orders/printMarkings/orders/20400048799000/type/pdf/apiKey/test-api-key-123",
				PrintFormat.MARKINGS_LEGACY_A4.buildUrl(KEY, List.of("20400048799000"))
		);
	}

	@Test
	void testSeveralDocumentsAreCommaSeparatedAsDocumented() {
		List<String> ttns = List.of("20400048799001", "20400048799002");

		assertEquals(
				"https://my.novaposhta.ua/orders/printMarking100x100/orders/20400048799001,20400048799002/type/pdf/apiKey/test-api-key-123/zebra",
				PrintFormat.ZEBRA.buildUrl(KEY, ttns)
		);
		assertEquals(
				"https://my.novaposhta.ua/orders/printMarking85x85/orders/20400048799001,20400048799002/type/pdf8/apiKey/test-api-key-123",
				PrintFormat.A4.buildUrl(KEY, ttns)
		);
		assertEquals(
				"https://my.novaposhta.ua/orders/printDocument/orders/20400048799001,20400048799002/type/pdf/apiKey/test-api-key-123",
				PrintFormat.WAYBILL_A4.buildUrl(KEY, ttns)
		);
	}

	@Test
	void testWaybillUrlGeneration() {
		assertEquals(
				"https://my.novaposhta.ua/orders/printDocument/orders/20400048799000/type/pdf/apiKey/test-api-key-123",
				PrintFormat.WAYBILL_A4.buildUrl(KEY, List.of("20400048799000"))
		);
	}

	@Test
	@SuppressWarnings("deprecation")
	void testWaybillA5IsTheSameDocumentAsA4() {
		// The documentation has no A5 layout; the constant is kept only for source compatibility.
		assertEquals(PrintFormat.WAYBILL_A4.buildUrl(KEY, List.of("1")), PrintFormat.WAYBILL_A5.buildUrl(KEY, List.of("1")));
	}

	@Test
	void testScanSheetUsesRefsArrayAsDocumented() {
		String sheetRef = "b4f2c019-3221-11eb-810a-005056b24375";

		assertEquals(
				"https://my.novaposhta.ua/scanSheet/printScanSheet/refs[]/b4f2c019-3221-11eb-810a-005056b24375/type/pdf/apiKey/test-api-key-123",
				PrintFormat.SCAN_SHEET.buildUrl(KEY, List.of(sheetRef))
		);
		assertEquals(
				"https://my.novaposhta.ua/scanSheet/printScanSheet/refs[]/105-00000595/type/pdf/apiKey/test-api-key-123",
				PrintFormat.SCAN_SHEET.buildUrl(KEY, List.of("105-00000595"))
		);
	}

	@Test
	void testScanSheetTakesExactlyOneId() {
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.SCAN_SHEET.buildUrl(KEY, List.of("a-1", "b-2")));
	}

	@Test
	void testHtmlOutputAndArrayStyleAndPaging() {
		PrintRequest html = PrintRequest.builder().doc(PrintRequest.Doc.DOCUMENT).type(PrintRequest.Type.HTML)
				.arrayStyle(true).ids(List.of("20600000002260", "20600000002261")).build();
		assertEquals(
				"https://my.novaposhta.ua/orders/printDocument/orders[]/20600000002260/orders[]/20600000002261/type/html/apiKey/test-api-key-123",
				html.toUrl(KEY)
		);

		PrintRequest paged = PrintRequest.builder().doc(PrintRequest.Doc.MARKING_100X100).type(PrintRequest.Type.PDF)
				.zebra(true).ids(List.of("20400491686552", "20400491686068")).limit(106).page(1).build();
		// the zebra flag is a bare trailing key, so it has to stay last when limit/page are present
		assertEquals(
				"https://my.novaposhta.ua/orders/printMarking100x100/orders/20400491686552,20400491686068/type/pdf/apiKey/test-api-key-123/limit/106/page/1/zebra",
				paged.toUrl(KEY)
		);
	}

	@Test
	void testIdsCannotInjectPathSegments() {
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.A4.buildUrl(KEY, List.of("1/../../x")));
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.A4.buildUrl(KEY, List.of("1 2")));
	}

	@Test
	void testInvalidParametersValidation() {
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.ZEBRA.buildUrl(null, List.of("20400048799000")));
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.ZEBRA.buildUrl("   ", List.of("20400048799000")));
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.ZEBRA.buildUrl("valid-key", null));
		assertThrows(IllegalArgumentException.class, () -> PrintFormat.ZEBRA.buildUrl("valid-key", List.of()));
	}
}
