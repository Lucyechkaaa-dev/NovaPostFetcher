package com.novapost;

import com.novapost.model.PrintFormat;
import com.novapost.model.PrintableDocument;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PrintableDocumentTest {

	private byte[] createSamplePdf() throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try (PDDocument doc = new PDDocument()) {
			doc.addPage(new PDPage());
			doc.save(baos);
		}
		return baos.toByteArray();
	}

	@Test
	void testPdfDetectionAndProperties() throws Exception {
		byte[] pdfBytes = createSamplePdf();
		PrintableDocument doc = PrintableDocument.of(pdfBytes, PrintFormat.ZEBRA, "20400048799000");

		assertTrue(doc.isPdf());
		assertEquals(pdfBytes.length, doc.size());
		assertEquals(PrintFormat.ZEBRA, doc.getFormat().orElse(null));
		assertEquals(List.of("20400048799000"), doc.getDocumentRefs());
		assertEquals(1, doc.getPageCount());

		PrintableDocument nonPdf = PrintableDocument.of(new byte[]{1, 2, 3, 4});
		assertFalse(nonPdf.isPdf());
		assertThrows(IOException.class, nonPdf::getPageCount);
	}

	@Test
	void testSaveToFile(@TempDir Path tempDir) throws Exception {
		byte[] pdfBytes = createSamplePdf();
		PrintableDocument doc = PrintableDocument.of(pdfBytes);

		Path target = tempDir.resolve("subfolder/sample.pdf");
		doc.saveToFile(target);

		assertTrue(Files.exists(target));
		assertArrayEquals(pdfBytes, Files.readAllBytes(target));

		Path tempFile = doc.saveToTempFile("test_", ".pdf");
		assertTrue(Files.exists(tempFile));
		Files.deleteIfExists(tempFile);
	}

	@Test
	void testRasterizeToImageAndPng() throws Exception {
		byte[] pdfBytes = createSamplePdf();
		PrintableDocument doc = PrintableDocument.of(pdfBytes);

		List<BufferedImage> images = doc.toImages(72.0f);
		assertEquals(1, images.size());
		assertNotNull(images.getFirst());

		BufferedImage singlePage = doc.toImage(0, 72.0f);
		assertNotNull(singlePage);

		byte[] pngBytes = doc.toPngImage(0, 72.0f);
		assertNotNull(pngBytes);
		assertTrue(pngBytes.length > 0);
		// PNG magic header: 0x89 0x50 0x4E 0x47
		assertEquals((byte) 0x89, pngBytes[0]);
		assertEquals((byte) 0x50, pngBytes[1]);
		assertEquals((byte) 0x4E, pngBytes[2]);
		assertEquals((byte) 0x47, pngBytes[3]);
	}

	@Test
	void testSaveAsImages(@TempDir Path tempDir) throws Exception {
		byte[] pdfBytes = createSamplePdf();
		PrintableDocument doc = PrintableDocument.of(pdfBytes);

		List<Path> savedImages = doc.saveAsImages(tempDir, "label_out", "png", 72.0f);
		assertEquals(1, savedImages.size());
		assertTrue(Files.exists(savedImages.getFirst()));
		assertTrue(savedImages.getFirst().getFileName().toString().startsWith("label_out_page_1.png"));
	}

	@Test
	void testPrinterListing() {
		List<String> printers = PrintableDocument.getAvailablePrinterNames();
		assertNotNull(printers);
	}
}
