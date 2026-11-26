package com.novapost.testsupport;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpModelStyle;

import java.io.IOException;
import java.net.http.HttpClient;
import java.time.Duration;
import java.util.regex.Pattern;

/**
 * A {@link NovaPostClient} that refuses to send anything but reads. The live smoke tests use it, because Nova Poshta has no
 * sandbox for API 2.0: whatever a test sends goes to the real account that owns the key. A method is let through only when its
 * name starts with {@code get}, {@code search} or {@code check}; {@code save}, {@code update}, {@code delete},
 * {@code insertDocuments}, {@code removeDocuments}, {@code deleteScanSheet}, {@code generateReport} and the rest are refused
 * <em>before</em> a request is built, so nothing leaves the machine. Every facade ends in
 * {@link #executeForRootNode}, so the typed API is covered as well.
 */
public final class ReadOnlyClient extends NovaPostClient{

	private static final Pattern READ_ONLY = Pattern.compile("(?i)^(get|search|check)[a-z0-9]*$");

	private final HttpClient http;

	public ReadOnlyClient(String apiKey, String apiUrl, HttpClient http, ObjectMapper mapper, NpModelStyle style){
		super(apiKey, apiUrl, http, mapper, style);
		this.http = http;
	}

	/** A guarded client for the real endpoint. */
	public static ReadOnlyClient of(String apiKey, NpModelStyle style){
		HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(15)).build();
		return new ReadOnlyClient(apiKey, DEFAULT_API_URL, http, createDefaultMapper(), style);
	}

	/** True for a method that only reads. */
	public static boolean isReadOnly(String calledMethod){
		return calledMethod != null && READ_ONLY.matcher(calledMethod).matches();
	}

	@Override
	public NovaPostClient withModelStyle(NpModelStyle style){
		return new ReadOnlyClient(getApiKey(), getApiUrl(), http, getObjectMapper(), style);
	}

	@Override
	public <T> JsonNode executeForRootNode(String modelName, String calledMethod, T properties) throws IOException, InterruptedException{
		if(!isReadOnly(calledMethod)){
			throw new IllegalStateException("ReadOnlyClient refused " + modelName + "/" + calledMethod
					+ ": only get*, search* and check* calls may be sent from a live test");
		}
		return super.executeForRootNode(modelName, calledMethod, properties);
	}
}
