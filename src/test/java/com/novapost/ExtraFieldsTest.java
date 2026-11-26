package com.novapost;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.model.TrackingStatus;
import com.novapost.model.document.InternetDocument;
import com.novapost.projection.ModelProjector;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The two largest answers (a waybill with 124 fields, tracking with 107) keep whatever the documentation does not list in
 * {@code extra} instead of dropping it, so a field the API adds later is still reachable.
 */
class ExtraFieldsTest{

	private final ObjectMapper mapper = NovaPostClient.createDefaultMapper();

	@Test
	void waybillKeepsUndeclaredFields() throws Exception{
		String json = "{\"IntDocNumber\":\"20400009196210\",\"RecipientAddressDescription\":\"Відділення №1\",\"BrandNewFlag\":true,"
				+ "\"Nested\":{\"a\":[1,2]},\"Nothing\":null}";

		InternetDocument doc = mapper.readValue(json, InternetDocument.class);

		assertEquals("20400009196210", doc.intDocNumber());
		assertEquals("Відділення №1", doc.recipientAddressDescription(), "a documented field is not an extra");
		assertEquals(java.util.Set.of("BrandNewFlag", "Nested", "Nothing"), doc.extra().keySet());
		assertTrue(doc.extra().get("BrandNewFlag").asBoolean());
		assertEquals(2, doc.extra().get("Nested").path("a").size());
		assertTrue(doc.extra().get("Nothing").isNull());
	}

	@Test
	void extraIsNeverNull() throws Exception{
		assertTrue(mapper.readValue("{\"IntDocNumber\":\"1\"}", InternetDocument.class).extra().isEmpty());
		assertTrue(mapper.readValue("{\"Number\":\"1\"}", TrackingStatus.class).extra().isEmpty());
		// the canonical constructor with everything null (what a hand-built instance would pass) is normalised too
		for(Class<? extends Record> type : java.util.List.of(TrackingStatus.class, InternetDocument.class)){
			var canonical = java.util.Arrays.stream(type.getDeclaredConstructors())
					.max(java.util.Comparator.comparingInt(java.lang.reflect.Constructor::getParameterCount)).orElseThrow();
			Record allNull = (Record) canonical.newInstance(new Object[canonical.getParameterCount()]);
			assertTrue(((Map<?, ?>) type.getMethod("extra").invoke(allNull)).isEmpty(), type.getSimpleName());
		}
	}

	@Test
	void trackingKeepsUndeclaredFields() throws Exception{
		TrackingStatus status = mapper.readValue("{\"Number\":\"20400048799000\",\"StatusCode\":\"9\",\"ABrandNewField\":\"x\",\"AnotherOne\":7}", TrackingStatus.class);

		assertEquals("9", status.statusCode());
		assertEquals(java.util.Set.of("ABrandNewField", "AnotherOne"), status.extra().keySet());
		assertEquals("x", status.extra().get("ABrandNewField").asText());
		assertEquals(7, status.extra().get("AnotherOne").asInt());
	}

	@Test
	void serializingFlattensTheExtraFieldsBack() throws Exception{
		InternetDocument doc = mapper.readValue("{\"IntDocNumber\":\"1\",\"BrandNewFlag\":true}", InternetDocument.class);

		JsonNode out = mapper.readTree(mapper.writeValueAsString(doc));

		assertEquals("1", out.path("IntDocNumber").asText());
		assertTrue(out.path("BrandNewFlag").asBoolean(), "the extra field comes back at the top level: " + out);
		assertFalse(out.has("extra"), "and not wrapped in an 'extra' property: " + out);
	}

	@Test
	void projectionsStillWorkOnTheLargeModels(){
		ModelProjector projector = ModelProjector.getInstance();
		JsonNode node = mapper.valueToTree(Map.of("IntDocNumber", "2040", "Cost", "500", "Mystery", "m"));

		InternetDocument doc = projector.projectJsonToClass(node, InternetDocument.class);
		Map<String, Object> picked = projector.projectJsonToMap(node, Map.of("IntDocNumber", "", "Mystery", ""));

		assertEquals("2040", doc.intDocNumber());
		assertEquals("m", doc.extra().get("Mystery").asText());
		assertEquals("m", picked.get("Mystery"));
	}
}
