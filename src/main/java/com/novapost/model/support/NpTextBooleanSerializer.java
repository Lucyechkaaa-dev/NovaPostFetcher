package com.novapost.model.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/** Writes a {@link Boolean} request flag the way some documentation examples show it, as the string {@code "true"} or {@code "false"}. */
public class NpTextBooleanSerializer extends JsonSerializer<Boolean>{

	@Override
	public void serialize(Boolean value, JsonGenerator generator, SerializerProvider provider) throws IOException{
		generator.writeString(value ? "true" : "false");
	}
}
