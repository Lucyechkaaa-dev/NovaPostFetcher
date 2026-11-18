package com.novapost.model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.novapost.client.NovaPostClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TrackingStatusTest {

	private static final String JSON = """
			[{"Number":"20400560705237","StatusCode":"9","DateCreated":"10-11-2026 17:12:46",
			  "Status":"Відправлення отримано","RecipientDateTime":"11.11.2026 17:10:43",
			  "CargoType":"Parcel","DocumentWeight":11,"FactualWeight":"11.00",
			  "DocumentCost":"247.28","AnnouncedPrice":11017,"AfterpaymentOnGoodsCost":0,
			  "CityRecipient":"Зарічани","WarehouseRecipient":"Відділення №1: вул. Бердичівське шосе, 4",
			  "ScheduledDeliveryDate":"11-11-2026 12:00:00","ActualDeliveryDate":"2026-11-11 11:26:05",
			  "PossibilityCreateClaim":true,"SomeFieldWeDoNotDeclare":"ignored"}]
			""";

	@Test
	void parsesTrackingArray() throws Exception {
		List<TrackingStatus> list = NovaPostClient.createDefaultMapper()
				.readValue(JSON, new TypeReference<List<TrackingStatus>>() {});

		assertEquals(1, list.size());
		TrackingStatus t = list.getFirst();
		assertEquals("20400560705237", t.number());
		assertEquals("9", t.statusCode());
		assertEquals("Відправлення отримано", t.status());
		assertEquals(11.0, t.documentWeight());
		assertEquals("247.28", t.documentCost());
		assertEquals(11017.0, t.announcedPrice());
		assertEquals("2026-11-11 11:26:05", t.actualDeliveryDate());
		assertTrue(t.possibilityCreateClaim());
	}
}
