package com.novapost.model;

import java.time.LocalDateTime;

import java.math.BigDecimal;

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
		assertEquals(new BigDecimal("11"), t.documentWeight());
		assertEquals(0, new BigDecimal("247.28").compareTo(t.documentCost()));
		assertEquals(0, new BigDecimal("11017").compareTo(t.announcedPrice()));
		// one answer, four spellings of a date: dd-MM-yyyy HH:mm:ss, dd.MM.yyyy HH:mm:ss, yyyy-MM-dd HH:mm:ss
		assertEquals(LocalDateTime.of(2026, 11, 10, 17, 12, 46), t.dateCreated());
		assertEquals(LocalDateTime.of(2026, 11, 11, 17, 10, 43), t.recipientDateTime());
		assertEquals(LocalDateTime.of(2026, 11, 11, 12, 0, 0), t.scheduledDeliveryDate());
		assertEquals(LocalDateTime.of(2026, 11, 11, 11, 26, 5), t.actualDeliveryDate());
		assertTrue(t.possibilityCreateClaim());
	}
}
