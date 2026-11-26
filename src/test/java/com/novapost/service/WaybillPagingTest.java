package com.novapost.service;

import com.novapost.client.NovaPostClient;
import com.novapost.controller.NovaPostController;
import com.novapost.model.InternetDocumentListItem;
import com.novapost.testsupport.RecordingHttpClient;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * {@code getDocumentList} paging, as the documentation describes it: {@code GetFullList=1} returns at most 100 records and
 * ignores paging, {@code GetFullList=0} pages without a total limit and needs {@code Page}.
 */
class WaybillPagingTest{

	private static final LocalDateTime DAY = LocalDateTime.of(2026, 11, 25, 0, 0);

	private static String docs(String prefix, int count){
		List<String> items = new ArrayList<>();
		for(int i = 0; i < count; i++){
			items.add("{\"Ref\":\"" + prefix + "-" + i + "\",\"IntDocNumber\":\"2040" + prefix + i + "\"}");
		}
		return RecordingHttpClient.envelope("[" + String.join(",", items) + "]");
	}

	private static NovaPostClient client(RecordingHttpClient http){
		return new NovaPostClient("k", NovaPostClient.DEFAULT_API_URL, http, NovaPostClient.createDefaultMapper());
	}

	@Test
	void everyPageIsRequestedInPagedMode(){
		RecordingHttpClient http = new RecordingHttpClient(body -> body.contains("\"Page\":1") ? docs("p1", 100) : docs("p2", 7));

		List<InternetDocumentListItem> all = new WaybillFetcherService(client(http), 0).fetchAllDocuments(DAY, DAY);

		assertEquals(107, all.size());
		assertEquals(2, http.calls());
		for(int i = 0; i < http.calls(); i++){
			var props = http.request(i).path("methodProperties");
			assertEquals(0, props.path("GetFullList").asInt(-1), "request " + i + " must use paged mode: " + props);
			assertEquals(i + 1, props.path("Page").asInt());
		}
	}

	@Test
	void aServerThatIgnoresPageDoesNotLoopForever(){
		// always the same full page, whatever Page says
		RecordingHttpClient http = RecordingHttpClient.always(docs("same", 100));

		List<InternetDocumentListItem> all = new WaybillFetcherService(client(http), 0).fetchAllDocuments(DAY, DAY);

		assertEquals(100, all.size(), "the repeated page is dropped");
		assertEquals(2, http.calls(), "stops as soon as a page repeats the previous one");
	}

	@Test
	void ttnLookupAsksForTheNumberInPagedMode(){
		RecordingHttpClient http = RecordingHttpClient.always(docs("a", 1));
		NovaPostController controller = new NovaPostController(client(http));

		Optional<InternetDocumentListItem> found = controller.fetchWaybillByTtn("2040a0", DAY.minusDays(3), DAY);

		assertTrue(found.isPresent());
		assertEquals(1, http.calls());
		var props = http.request(0).path("methodProperties");
		assertEquals("2040a0", props.path("IntDocNumber").asText());
		assertEquals(0, props.path("GetFullList").asInt(-1));
		assertEquals("22.11.2026", props.path("DateTimeFrom").asText());
	}

	@Test
	void ttnLookupPagesOnWhenTheServerIgnoresTheNumberFilter(){
		RecordingHttpClient http = new RecordingHttpClient(body -> body.contains("\"Page\":1") ? docs("x", 100) : docs("target", 1));
		NovaPostController controller = new NovaPostController(client(http));

		assertEquals("2040target0", controller.fetchWaybillByTtn("2040target0", DAY.minusDays(30), DAY).orElseThrow().intDocNumber());
		assertEquals(2, http.calls());
	}

	@Test
	void updateByNumberFindsADocumentBeyondTheFirstHundred(){
		// findWaybillNodeByNumber used to read only the first page of 100
		RecordingHttpClient http = new RecordingHttpClient(body -> body.contains("\"Page\":1") ? docs("x", 100) : docs("target", 1));
		NovaPostController controller = new NovaPostController(client(http));

		assertTrue(controller.findWaybillNodeByNumber("2040target0", DAY.minusDays(30), DAY).isPresent());
	}

	@Test
	void lookupDoesNotLoopWhenTheServerIgnoresPageAndNumber(){
		RecordingHttpClient http = RecordingHttpClient.always(docs("same", 100));
		NovaPostController controller = new NovaPostController(client(http));

		assertTrue(controller.fetchWaybillByTtn("not-there", DAY.minusDays(30), DAY).isEmpty());
		assertEquals(2, http.calls());
	}
}
