package com.novapost.model.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/** Writes a {@link Boolean} request flag the way the documentation shows it, as the string {@code "1"} or {@code "0"}. */
public class NpFlagSerializer extends JsonSerializer<Boolean>{

	@Override
	public void serialize(Boolean value, JsonGenerator generator, SerializerProvider provider) throws IOException{
		generator.writeString(value ? "1" : "0");
	}
}
