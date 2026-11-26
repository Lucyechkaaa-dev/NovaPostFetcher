package com.novapost.model.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

/**
 * Reads a {@link Boolean} from the encodings the API mixes: {@code true}/{@code false}, {@code "true"}/{@code "false"},
 * {@code 1}/{@code 0}, {@code "1"}/{@code "0"}. Empty strings and the literal {@code "null"} become {@code null}.
 */
public class NpBooleanDeserializer extends JsonDeserializer<Boolean>{

	@Override
	public Boolean deserialize(JsonParser parser, DeserializationContext context) throws IOException{
		JsonToken token = parser.currentToken();
		if(token == JsonToken.VALUE_TRUE){
			return Boolean.TRUE;
		}
		if(token == JsonToken.VALUE_FALSE){
			return Boolean.FALSE;
		}
		if(token == JsonToken.VALUE_NUMBER_INT){
			return parser.getIntValue() != 0;
		}
		if(token == JsonToken.VALUE_STRING){
			String text = parser.getText().trim();
			if(isBlank(text)){
				return null;
			}
			Boolean parsed = parse(text);
			if(parsed != null){
				return parsed;
			}
			return (Boolean) context.handleWeirdStringValue(Boolean.class, text, "not a Nova Post boolean");
		}
		return (Boolean) context.handleUnexpectedToken(Boolean.class, parser);
	}

	private static boolean isBlank(String text){
		return text.isEmpty() || text.equalsIgnoreCase("null");
	}

	/**
	 * The boolean of a text value ({@code true}/{@code 1}/{@code yes}, {@code false}/{@code 0}/{@code no}, any case);
	 * {@code null} for an empty or unknown text.
	 */
	public static Boolean parse(String value){
		if(value == null){
			return null;
		}
		String text = value.trim();
		if(text.equalsIgnoreCase("true") || text.equals("1") || text.equalsIgnoreCase("yes")){
			return Boolean.TRUE;
		}
		if(text.equalsIgnoreCase("false") || text.equals("0") || text.equalsIgnoreCase("no")){
			return Boolean.FALSE;
		}
		return null;
	}
}
