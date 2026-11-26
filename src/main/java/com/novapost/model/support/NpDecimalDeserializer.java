package com.novapost.model.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Reads a {@link BigDecimal} from a JSON number or from the text forms the API mixes ({@code "12.50"}, {@code "12,50"}, {@code " 1 200.5 "}).
 * Empty text and the literal {@code null} become {@code null}. The scale of the text is kept ({@code "816.00"} stays {@code 816.00}).
 */
public class NpDecimalDeserializer extends JsonDeserializer<BigDecimal>{

	@Override
	public BigDecimal deserialize(JsonParser parser, DeserializationContext context) throws IOException{
		JsonToken token = parser.currentToken();
		if(token == JsonToken.VALUE_NUMBER_INT || token == JsonToken.VALUE_NUMBER_FLOAT){
			return parser.getDecimalValue();
		}
		if(token != JsonToken.VALUE_STRING){
			return (BigDecimal) context.handleUnexpectedToken(BigDecimal.class, parser);
		}
		String text = parser.getText().trim();
		if(text.isEmpty() || text.equalsIgnoreCase("null")){
			return null;
		}
		BigDecimal parsed = parse(text);
		if(parsed != null){
			return parsed;
		}
		return (BigDecimal) context.handleWeirdStringValue(BigDecimal.class, text, "not a Nova Post decimal");
	}

	/** {@code null} when the text is not a decimal number. */
	public static BigDecimal parse(String text){
		String t = text.replace(" ", "").replace(" ", "");
		if(t.indexOf(',') >= 0){
			t = t.indexOf('.') >= 0 ? t.replace(",", "") : t.replace(',', '.');
		}
		try{
			return new BigDecimal(t);
		}
		catch(NumberFormatException e){
			return null;
		}
	}
}
