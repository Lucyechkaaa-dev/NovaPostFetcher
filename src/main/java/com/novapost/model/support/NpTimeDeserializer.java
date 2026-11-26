package com.novapost.model.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Optional;

/** Reads a {@link LocalTime} from {@code 8:00}, {@code 08:00} or {@code 08:00:00}; empty values become {@code null}. */
public class NpTimeDeserializer extends JsonDeserializer<LocalTime>{

	@Override
	public LocalTime deserialize(JsonParser parser, DeserializationContext context) throws IOException{
		if(parser.currentToken() != JsonToken.VALUE_STRING){
			return (LocalTime) context.handleUnexpectedToken(LocalTime.class, parser);
		}
		String text = parser.getText();
		if(NpDates.isBlankValue(text)){
			return null;
		}
		Optional<LocalTime> parsed = NpDates.parseTime(text);
		if(parsed.isPresent()){
			return parsed.get();
		}
		return (LocalTime) context.handleWeirdStringValue(LocalTime.class, text, "unsupported Nova Post time format");
	}
}
