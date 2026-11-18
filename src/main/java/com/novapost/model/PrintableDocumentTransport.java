package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Objects;

/**
 * JSON-transportable form of a {@link PrintableDocument}. The binary content is carried as a Base64 string
 * under the {@code Content} key, so the payload can travel in any JSON body.
 *
 * <pre>
 * {"Format":"ZEBRA","DocumentRefs":["20400000000000"],"Content":"JVBERi0xLjQK..."}
 * </pre>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record PrintableDocumentTransport(
		@JsonProperty("Format") PrintFormat format,
		@JsonProperty("DocumentRefs") List<String> documentRefs,
		@JsonProperty("Content") String content
) {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	public PrintableDocumentTransport {
		Objects.requireNonNull(content, "content must not be null");
		if (content.isBlank()) {
			throw new IllegalArgumentException("content must not be blank");
		}
		documentRefs = documentRefs != null ? List.copyOf(documentRefs) : List.of();
	}

	/** Wraps a document, Base64-encoding its bytes. */
	public static PrintableDocumentTransport from(PrintableDocument document) {
		Objects.requireNonNull(document, "document must not be null");
		return new PrintableDocumentTransport(
				document.getFormat().orElse(null),
				document.getDocumentRefs(),
				document.toBase64()
		);
	}

	/**
	 * Summary for logs and the IntelliJ debugger. Omits the Base64 content,
	 * which can be megabytes long, and shows only its length.
	 */
	@Override
	public String toString() {
		return "PrintableDocumentTransport{" +
				"format=" + (format != null ? format : "none") +
				", documentRefs=" + documentRefs +
				", contentLength=" + content.length() + " chars" +
				'}';
	}

	/** Decodes the Base64 content back into a {@link PrintableDocument}. */
	public PrintableDocument toDocument() {
		return PrintableDocument.fromBase64(content, format, documentRefs);
	}

	/** Serializes this transport object to a JSON string. */
	public String toJson() throws JsonProcessingException {
		return MAPPER.writeValueAsString(this);
	}

	/** Parses a JSON string produced by {@link #toJson()}. */
	public static PrintableDocumentTransport fromJson(String json) throws JsonProcessingException {
		Objects.requireNonNull(json, "json must not be null");
		return MAPPER.readValue(json, PrintableDocumentTransport.class);
	}
}
