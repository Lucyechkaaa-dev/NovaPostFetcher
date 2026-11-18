package com.novapost;

import com.novapost.model.PrintFormat;
import com.novapost.model.PrintableDocument;
import com.novapost.model.PrintableDocumentTransport;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrintableDocumentTransportTest {

	private static final byte[] PDF_BYTES = "%PDF-1.4 sticker bytes \u0000ÿ".getBytes(StandardCharsets.ISO_8859_1);

	@Test
	void base64RoundTripPreservesBytes() {
		PrintableDocument doc = PrintableDocument.of(PDF_BYTES);
		String encoded = doc.toBase64();

		assertEquals(Base64.getEncoder().encodeToString(PDF_BYTES), encoded);
		assertArrayEquals(PDF_BYTES, PrintableDocument.fromBase64(encoded).getBytes());
	}

	@Test
	void fromBase64TrimsWhitespaceAndKeepsFormatAndRefs() {
		String encoded = "  " + Base64.getEncoder().encodeToString(PDF_BYTES) + "\n";
		PrintableDocument doc = PrintableDocument.fromBase64(encoded, PrintFormat.ZEBRA, List.of("ref1"));

		assertArrayEquals(PDF_BYTES, doc.getBytes());
		assertEquals(PrintFormat.ZEBRA, doc.getFormat().orElseThrow());
		assertEquals(List.of("ref1"), doc.getDocumentRefs());
	}

	@Test
	void fromBase64RejectsInvalidInput() {
		assertThrows(NullPointerException.class, () -> PrintableDocument.fromBase64(null));
		assertThrows(IllegalArgumentException.class, () -> PrintableDocument.fromBase64("   "));
		assertThrows(IllegalArgumentException.class, () -> PrintableDocument.fromBase64("not base64!!"));
		assertThrows(IllegalArgumentException.class, () -> PrintableDocument.fromBase64(""));
	}

	@Test
	void transportJsonUsesExpectedKeys() throws Exception {
		PrintableDocument doc = PrintableDocument.of(PDF_BYTES, PrintFormat.ZEBRA, "20400000000000");
		String json = PrintableDocumentTransport.from(doc).toJson();

		assertTrue(json.contains("\"Format\":\"ZEBRA\""), json);
		assertTrue(json.contains("\"DocumentRefs\":[\"20400000000000\"]"), json);
		assertTrue(json.contains("\"Content\":\"" + Base64.getEncoder().encodeToString(PDF_BYTES) + "\""), json);
	}

	@Test
	void transportJsonRoundTripRestoresDocument() throws Exception {
		PrintableDocument doc = PrintableDocument.of(PDF_BYTES, PrintFormat.A4, List.of("r1", "r2"));

		String json = PrintableDocumentTransport.from(doc).toJson();
		PrintableDocument restored = PrintableDocumentTransport.fromJson(json).toDocument();

		assertArrayEquals(PDF_BYTES, restored.getBytes());
		assertEquals(PrintFormat.A4, restored.getFormat().orElseThrow());
		assertEquals(List.of("r1", "r2"), restored.getDocumentRefs());
	}

	@Test
	void transportWithoutFormatOmitsItFromJson() throws Exception {
		String json = PrintableDocumentTransport.from(PrintableDocument.of(PDF_BYTES)).toJson();

		assertFalse(json.contains("\"Format\""), json);
		assertEquals(PDF_BYTES.length, PrintableDocumentTransport.fromJson(json).toDocument().size());
	}

	@Test
	void transportRejectsBlankContent() {
		assertThrows(NullPointerException.class, () -> new PrintableDocumentTransport(null, null, null));
		assertThrows(IllegalArgumentException.class, () -> new PrintableDocumentTransport(null, null, " "));
	}
}
