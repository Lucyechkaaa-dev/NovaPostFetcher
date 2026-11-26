package com.novapost.model.support;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * Reads the message lists of the answer envelope ({@code errors}, {@code warnings}, the code lists) as a list of text.
 * The documentation shows lists of strings, but the live API also sends objects keyed by the waybill, for example
 * {@code "warnings":[{"ID_20400048799000":"Please enter a valid phone number from the express invoice to show full information"}]}
 * (seen on 2026-11-26 from {@code TrackingDocument/getStatusDocuments}). An object becomes {@code "key: message"} (several entries are joined by
 * {@code "; "}), numbers and booleans their text, anything else its JSON; a single value instead of an array is taken as a one-element list.
 */
public class NpMessageListDeserializer extends JsonDeserializer<List<String>>{

	@Override
	public List<String> deserialize(JsonParser parser, DeserializationContext context) throws IOException{
		JsonNode node = parser.readValueAsTree();
		List<String> messages = new ArrayList<>();
		if(node == null || node.isNull() || node.isMissingNode()){
			return messages;
		}
		if(node.isArray()){
			node.forEach(element -> messages.add(text(element)));
		}
		else{
			messages.add(text(node));
		}
		return messages;
	}

	/** A missing or {@code null} list reads as an empty one, so callers never have to null-check it. */
	@Override
	public List<String> getNullValue(DeserializationContext context){
		return new ArrayList<>();
	}

	/** One entry of a message list as text (the shapes are described above); also used for the raw answers the controller reads as a tree. */
	public static String text(JsonNode element){
		if(element.isValueNode()){
			return element.asText();
		}
		if(element.isObject() && !element.isEmpty()){
			StringJoiner joined = new StringJoiner("; ");
			element.fields().forEachRemaining(entry -> joined.add(entry.getKey() + ": " + text(entry.getValue())));
			return joined.toString();
		}
		return element.toString();
	}
}
