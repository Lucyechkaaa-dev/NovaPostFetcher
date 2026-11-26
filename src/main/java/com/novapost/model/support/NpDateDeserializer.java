package com.novapost.model.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

/**
 * Reads a {@link LocalDate} from any date format Nova Poshta is known to return (see {@link NpDates}); empty values and the zero dates
 * become {@code null}. A value that is none of those goes to the mapper's problem handlers, so the client mapper
 * ({@link NpLenientValues}) turns it into {@code null} with a warning, and a strict mapper fails.
 */
public class NpDateDeserializer extends JsonDeserializer<LocalDate>{

	@Override
	public LocalDate deserialize(JsonParser parser, DeserializationContext context) throws IOException{
		if(parser.currentToken() != JsonToken.VALUE_STRING){
			return (LocalDate) context.handleUnexpectedToken(LocalDate.class, parser);
		}
		String text = parser.getText();
		if(NpDates.isBlankValue(text)){
			return null;
		}
		Optional<LocalDate> parsed = NpDates.parseDate(text);
		if(parsed.isPresent()){
			return parsed.get();
		}
		return (LocalDate) context.handleWeirdStringValue(LocalDate.class, text, "unsupported Nova Post date format");
	}
}
