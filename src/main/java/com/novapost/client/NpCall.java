package com.novapost.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.novapost.controller.NovaPostApiException;
import com.novapost.model.NpResponse;
import com.novapost.projection.ModelProjector;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * One prepared call of a Nova Poshta method. It is lazy: nothing is sent until a terminal method runs.
 * <ul>
 *   <li>{@link #response()} — the typed envelope; throws checked I/O exceptions, never inspects {@code success};</li>
 *   <li>{@link #list()} / {@link #first()} — the typed data; unchecked, throws {@link NovaPostApiException} when the API reports errors;
 *       {@link #result()} also returns the warnings of the answer;</li>
 *   <li>{@link #as(Class)} / {@link #project(Map)} — the same data projected onto any class or onto selected fields.</li>
 * </ul>
 */
public final class NpCall<R>{

	private static final System.Logger LOGGER = System.getLogger(NpCall.class.getName());

	private final NovaPostClient client;
	private final NpModel model;
	private final String method;
	private final Object properties;
	private final Class<R> type;

	NpCall(NovaPostClient client, NpModel model, String method, Object properties, Class<R> type){
		this.client = client;
		this.model = model;
		this.method = method;
		this.properties = properties;
		this.type = type;
	}

	public NpModel model(){
		return model;
	}

	public String method(){
		return method;
	}

	public Object properties(){
		return properties;
	}

	/** The model name that will actually be sent, after the client's {@link NpModelStyle} is applied. */
	public String modelName(){
		return client.resolveModelName(model.legacyName());
	}

	public NpResponse<R> response() throws IOException, InterruptedException{
		return client.execute(model.legacyName(), method, properties, type);
	}

	public JsonNode rawRoot() throws IOException, InterruptedException{
		return client.executeForRootNode(model.legacyName(), method, properties);
	}

	/** The data plus the warnings of a successful answer; throws {@link NovaPostApiException} when the API reports errors. */
	public NpResult<R> result(){
		NpResponse<R> response = unchecked(this::response);
		failOnErrors(response.hasErrors(), response.errors(), response.errorCodes());
		NpResult<R> result = new NpResult<>(response.data(), response.warnings(), response.warningCodes(), response.infoCodes());
		if(result.hasWarnings()){
			LOGGER.log(System.Logger.Level.WARNING, "Nova Post warning in {0}/{1}: {2}", modelName(), method, String.join("; ", result.warnings()));
		}
		return result;
	}

	/** The typed data; the warnings are logged. Use {@link #result()} to read them. */
	public List<R> list(){
		return result().data();
	}

	public Optional<R> first(){
		return list().stream().findFirst();
	}

	/** The raw {@code data} array, after the same error check as {@link #list()}. */
	public JsonNode data(){
		JsonNode root = unchecked(this::rawRoot);
		boolean failed = !root.path("success").asBoolean(true);
		List<String> errors = strings(root.path("errors"));
		failOnErrors(failed || !errors.isEmpty(), errors, strings(root.path("errorCodes")));
		return root.path("data");
	}

	public <T> List<T> as(Class<T> target){
		return ModelProjector.getInstance().projectJsonArrayToClass(data(), target);
	}

	public List<Map<String, Object>> project(Map<String, Object> targetFields){
		return ModelProjector.getInstance().projectJsonArrayToMap(data(), targetFields);
	}

	@Override
	public String toString(){
		return "NpCall[" + modelName() + "/" + method + "]";
	}

	private void failOnErrors(boolean failed, List<String> errors, List<String> codes){
		if(failed){
			throw new NovaPostApiException("Nova Post API error in " + modelName() + "/" + method, errors, codes);
		}
	}

	private static List<String> strings(JsonNode array){
		java.util.ArrayList<String> out = new java.util.ArrayList<>();
		if(array.isArray()){
			array.forEach(n -> out.add(n.asText()));
		}
		return out;
	}

	private <T> T unchecked(IoSupplier<T> supplier){
		try{
			return supplier.get();
		}
		catch(InterruptedException e){
			Thread.currentThread().interrupt();
			throw new NovaPostApiException("Interrupted during " + modelName() + "/" + method, e);
		}
		catch(IOException e){
			throw new NovaPostApiException("Communication failure during " + modelName() + "/" + method + ": " + e.getMessage(), e);
		}
	}

	@FunctionalInterface
	private interface IoSupplier<T>{

		T get() throws IOException, InterruptedException;
	}
}
