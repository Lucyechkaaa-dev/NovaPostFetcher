package com.novapost.model.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Reads an {@link Integer} from a JSON number or from text ({@code "5"}, {@code " 5 "}, {@code "5.0"}); empty text and the literal
 * {@code null} become {@code null}. A fraction ({@code 5.5}), a value outside the range of an int and a non-number go to the mapper's problem
 * handlers ({@link NpLenientValues}). Jackson's own integer deserializer cannot take part in that: when an object or an array arrives where the
 * number belongs, it asks the handler for a {@code String} and breaks on the {@code null} that "read it as nothing" has to answer with.
 */
public class NpIntegerDeserializer extends JsonDeserializer<Integer>{

	@Override
	public Integer deserialize(JsonParser parser, DeserializationContext context) throws IOException{
		JsonToken token = parser.currentToken();
		if(token == JsonToken.VALUE_NUMBER_INT || token == JsonToken.VALUE_NUMBER_FLOAT){
			return whole(parser.getDecimalValue(), parser.getText(), context, true);
		}
		if(token != JsonToken.VALUE_STRING){
			return (Integer) context.handleUnexpectedToken(Integer.class, parser);
		}
		String text = parser.getText().trim();
		if(text.isEmpty() || text.equalsIgnoreCase("null")){
			return null;
		}
		BigDecimal number = NpDecimalDeserializer.parse(text);
		if(number == null){
			return (Integer) context.handleWeirdStringValue(Integer.class, text, "not a whole number");
		}
		return whole(number, text, context, false);
	}

	private static Integer whole(BigDecimal number, String text, DeserializationContext context, boolean fromNumberToken) throws IOException{
		try{
			return number.intValueExact();
		}
		catch(ArithmeticException e){
			String reason = "not a whole number that fits an int";
			return fromNumberToken
					? (Integer) context.handleWeirdNumberValue(Integer.class, number, reason)
					: (Integer) context.handleWeirdStringValue(Integer.class, text, reason);
		}
	}
}
