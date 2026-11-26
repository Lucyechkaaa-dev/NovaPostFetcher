package com.novapost.model.support;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;

/**
 * Writes a {@link BigDecimal} as a JSON <em>string</em> in plain notation ({@code "816.00"}, never {@code 8.16E+2}): the documentation's
 * request examples quote every number, and so did the {@code String} fields these values replace.
 */
public class NpDecimalSerializer extends JsonSerializer<BigDecimal>{

	@Override
	public void serialize(BigDecimal value, JsonGenerator generator, SerializerProvider provider) throws IOException{
		generator.writeString(value.toPlainString());
	}
}
