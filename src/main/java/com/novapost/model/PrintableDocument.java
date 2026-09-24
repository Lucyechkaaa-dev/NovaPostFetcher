package com.novapost.model;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.printing.PDFPageable;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class PrintableDocument {

	private static final byte[] PDF_MAGIC_BYTES = new byte[]{0x25, 0x50, 0x44, 0x46}; // "%PDF"

	private final byte[] bytes;
	private final PrintFormat format;
	private final List<String> documentRefs;

	public PrintableDocument(byte[] bytes) {
		this(bytes, null, List.of());
	}

	public PrintableDocument(byte[] bytes, PrintFormat format, List<String> documentRefs) {
		Objects.requireNonNull(bytes, "bytes must not be null");
		if (bytes.length == 0) {
			throw new IllegalArgumentException("Document bytes must not be empty");
		}
		this.bytes = bytes.clone();
		this.format = format;
		this.documentRefs = documentRefs != null ? List.copyOf(documentRefs) : List.of();
	}

	public static PrintableDocument of(byte[] bytes) {
		return new PrintableDocument(bytes);
	}

	public static PrintableDocument of(byte[] bytes, PrintFormat format, String documentRef) {
		return new PrintableDocument(bytes, format, documentRef != null ? List.of(documentRef) : List.of());
	}

	public static PrintableDocument of(byte[] bytes, PrintFormat format, List<String> documentRefs) {
		return new PrintableDocument(bytes, format, documentRefs);
	}

	public byte[] getBytes() {
		return bytes.clone();
	}

	public int size() {
		return bytes.length;
	}

	public InputStream getInputStream() {
		return new ByteArrayInputStream(bytes);
	}

	public Optional<PrintFormat> getFormat() {
		return Optional.ofNullable(format);
	}

	public List<String> getDocumentRefs() {
		return documentRefs;
	}

	public boolean isPdf() {
		if (bytes.length < 4) {
			return false;
		}
		return bytes[0] == PDF_MAGIC_BYTES[0] &&
				bytes[1] == PDF_MAGIC_BYTES[1] &&
				bytes[2] == PDF_MAGIC_BYTES[2] &&
				bytes[3] == PDF_MAGIC_BYTES[3];
	}

	public Path saveToFile(Path targetPath) throws IOException {
		Objects.requireNonNull(targetPath, "targetPath must not be null");
		if (targetPath.getParent() != null) {
			Files.createDirectories(targetPath.getParent());
		}
		return Files.write(targetPath, bytes);
	}

	public File saveToFile(File targetFile) throws IOException {
		Objects.requireNonNull(targetFile, "targetFile must not be null");
		saveToFile(targetFile.toPath());
		return targetFile;
	}

	public Path saveToTempFile(String prefix, String suffix) throws IOException {
		Path temp = Files.createTempFile(
				prefix != null ? prefix : "novapost_print_",
				suffix != null ? suffix : ".pdf"
		);
		return Files.write(temp, bytes);
	}

	public int getPageCount() throws IOException {
		ensurePdf();
		try (PDDocument document = Loader.loadPDF(bytes)) {
			return document.getNumberOfPages();
		}
	}

	public List<BufferedImage> toImages() throws IOException {
		return toImages(300.0f);
	}

	public List<BufferedImage> toImages(float dpi) throws IOException {
		ensurePdf();
		try (PDDocument document = Loader.loadPDF(bytes)) {
			PDFRenderer renderer = new PDFRenderer(document);
			int pageCount = document.getNumberOfPages();
			List<BufferedImage> images = new ArrayList<>(pageCount);
			for (int i = 0; i < pageCount; i++) {
				images.add(renderer.renderImageWithDPI(i, dpi, ImageType.RGB));
			}
			return Collections.unmodifiableList(images);
		}
	}

	public BufferedImage toImage(int pageIndex, float dpi) throws IOException {
		ensurePdf();
		try (PDDocument document = Loader.loadPDF(bytes)) {
			int totalPages = document.getNumberOfPages();
			if (pageIndex < 0 || pageIndex >= totalPages) {
				throw new IndexOutOfBoundsException("Page index " + pageIndex + " out of bounds for page count " + totalPages);
			}
			PDFRenderer renderer = new PDFRenderer(document);
			return renderer.renderImageWithDPI(pageIndex, dpi, ImageType.RGB);
		}
	}

	public byte[] toPngImage(int pageIndex, float dpi) throws IOException {
		BufferedImage image = toImage(pageIndex, dpi);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ImageIO.write(image, "PNG", baos);
		return baos.toByteArray();
	}

	public byte[] toJpegImage(int pageIndex, float dpi, float quality) throws IOException {
		BufferedImage image = toImage(pageIndex, dpi);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("JPEG");
		if (!writers.hasNext()) {
			throw new IOException("No JPEG ImageWriter found in runtime");
		}
		ImageWriter writer = writers.next();
		try (ImageOutputStream ios = ImageIO.createImageOutputStream(baos)) {
			writer.setOutput(ios);
			ImageWriteParam param = writer.getDefaultWriteParam();
			if (param.canWriteCompressed()) {
				param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				param.setCompressionQuality(Math.clamp(quality, 0.0f, 1.0f));
			}
			writer.write(null, new IIOImage(image, null, null), param);
		} finally {
			writer.dispose();
		}
		return baos.toByteArray();
	}

	public List<Path> saveAsImages(Path outputDir, String baseName, String formatName, float dpi) throws IOException {
		Objects.requireNonNull(outputDir, "outputDir must not be null");
		Files.createDirectories(outputDir);
		String safeFormat = formatName != null ? formatName.toLowerCase() : "png";
		String safeBase = baseName != null && !baseName.isBlank() ? baseName : "label";

		List<BufferedImage> images = toImages(dpi);
		List<Path> savedPaths = new ArrayList<>(images.size());

		for (int i = 0; i < images.size(); i++) {
			String fileName = String.format("%s_page_%d.%s", safeBase, i + 1, safeFormat);
			Path filePath = outputDir.resolve(fileName);
			ImageIO.write(images.get(i), safeFormat, filePath.toFile());
			savedPaths.add(filePath);
		}
		return Collections.unmodifiableList(savedPaths);
	}

	public void print() throws PrinterException, IOException {
		print((PrintService) null);
	}

	public void print(String printerName) throws PrinterException, IOException {
		Objects.requireNonNull(printerName, "printerName must not be null");
		PrintService service = findPrinter(printerName)
				.orElseThrow(() -> new PrinterException("Printer not found: " + printerName));
		print(service);
	}

	public void print(PrintService printService) throws PrinterException, IOException {
		ensurePdf();
		try (PDDocument document = Loader.loadPDF(bytes)) {
			PrinterJob job = PrinterJob.getPrinterJob();
			if (printService != null) {
				job.setPrintService(printService);
			}
			job.setPageable(new PDFPageable(document));
			job.print();
		}
	}

	public static List<String> getAvailablePrinterNames() {
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		if (services == null || services.length == 0) {
			return List.of();
		}
		return Arrays.stream(services)
				.map(PrintService::getName)
				.toList();
	}

	public static Optional<String> getDefaultPrinterName() {
		PrintService defaultService = PrintServiceLookup.lookupDefaultPrintService();
		return Optional.ofNullable(defaultService).map(PrintService::getName);
	}

	public static Optional<PrintService> findPrinter(String printerName) {
		if (printerName == null || printerName.isBlank()) {
			return Optional.empty();
		}
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		if (services == null) {
			return Optional.empty();
		}
		return Arrays.stream(services)
				.filter(s -> s.getName().equalsIgnoreCase(printerName.trim()))
				.findFirst();
	}

	private void ensurePdf() throws IOException {
		if (!isPdf()) {
			throw new IOException("Payload does not have standard PDF header bytes");
		}
	}
}
