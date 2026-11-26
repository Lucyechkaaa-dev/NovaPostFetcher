package com.novapost.model.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Keeps one odd value from costing a whole answer. The models type their fields ({@code LocalDate}, {@code BigDecimal}, {@code Integer},
 * {@code Boolean}...), and the live API is not always as tidy as its documentation; a value that cannot be read as the field's type is
 * therefore read as {@code null} and reported once per field in the log, instead of failing a response of thousands of records.
 * <p>Installed by {@code NovaPostClient.createDefaultMapper()}. A mapper without this handler ({@code clearProblemHandlers()}) fails on such a
 * value; the strict documentation tests use one. Only fields of a wrapper or reference type are affected, never primitives.
 */
public final class NpLenientValues extends DeserializationProblemHandler{

	private static final System.Logger LOGGER = System.getLogger(NpLenientValues.class.getName());
	private static final Set<Class<?>> SCALARS = Set.of(LocalDate.class, LocalDateTime.class, LocalTime.class, BigDecimal.class,
			Integer.class, Long.class, Double.class, Boolean.class);



	private final Set<String> reported = ConcurrentHashMap.newKeySet();

	@Override
	public Object handleWeirdStringValue(DeserializationContext context, Class<?> targetType, String value, String failureMessage){
		return leniently(context, targetType, value);
	}

	@Override
	public Object handleWeirdNumberValue(DeserializationContext context, Class<?> targetType, Number value, String failureMessage){
		return leniently(context, targetType, value);
	}

	/**
	 * An object or an array where a scalar belongs (PHP backends send {@code []} or {@code {}} for "nothing") is skipped and read as {@code null}.
	 * The scalar fields of the models use the deserializers of this package, which ask here; a whole number is read by
	 * {@link NpIntegerDeserializer} for that reason (Jackson's own one cannot take this answer for an object).
	 */
	@Override
	public Object handleUnexpectedToken(DeserializationContext context, JavaType targetType, JsonToken token, JsonParser parser,
	                                    String failureMessage) throws IOException{
		Class<?> type = targetType.getRawClass();
		if(SCALARS.contains(type) && (token == JsonToken.START_OBJECT || token == JsonToken.START_ARRAY)){
			parser.skipChildren();
			return leniently(context, type, token == JsonToken.START_OBJECT ? "{...}" : "[...]");
		}
		return NOT_HANDLED;
	}

	private Object leniently(DeserializationContext context, Class<?> targetType, Object value){
		if(targetType.isPrimitive()){
			return NOT_HANDLED;
		}
		JsonParser parser = context.getParser();
		String name = parser == null ? null : parser.getParsingContext().getCurrentName();
		String property = name == null ? "?" : name;
		if(reported.add(targetType.getSimpleName() + ":" + property)){
			LOGGER.log(System.Logger.Level.WARNING, "Nova Post sent {0}={1}, which cannot be read as {2}; the field is left null "
					+ "(reported once per field)", property, value, targetType.getSimpleName());
		}
		return null;
	}
}
