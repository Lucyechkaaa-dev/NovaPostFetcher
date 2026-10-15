package com.novapost.service;

import com.novapost.client.NovaPostClient;
import com.novapost.db.DatabaseConfig;
import com.novapost.db.DatabaseManager;
import com.novapost.model.InternetDocumentListItem;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NovaPostSyncServiceTest {

	@Test
	void testSyncInternetDocumentsUsesWaybillFetcherService() throws SQLException {
		List<String> capturedRequests = new ArrayList<>();

		WaybillFetcherServiceTest.MockScriptedHttpClient httpClient = new WaybillFetcherServiceTest.MockScriptedHttpClient((req, reqBody) -> {
			capturedRequests.add(reqBody);
			if (reqBody.contains("01.08.2026") && reqBody.contains("\"Page\":1")) {
				return WaybillFetcherServiceTest.generateResponseJson(100, "batch-1");
			} else if (reqBody.contains("01.08.2026") && reqBody.contains("\"Page\":2")) {
				return WaybillFetcherServiceTest.generateResponseJson(5, "batch-2");
			}
			return WaybillFetcherServiceTest.generateResponseJson(0, "empty");
		});

		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, httpClient, NovaPostClient.createDefaultMapper());
		WaybillFetcherService waybillFetcherService = new WaybillFetcherService(client, 0);

		List<List<InternetDocumentListItem>> insertedBatches = new ArrayList<>();

		// DatabaseManager subclass overriding batch insert to capture items without requiring a live MySQL server
		DatabaseManager stubDbManager = new DatabaseManager(new DatabaseConfig("jdbc:mysql://localhost:3306/dummy", "u", "p")) {
			@Override
			public int insertInternetDocuments(List<InternetDocumentListItem> documents) {
				insertedBatches.add(new ArrayList<>(documents));
				return documents.size();
			}
		};

		NovaPostSyncService syncService = new NovaPostSyncService(client, stubDbManager, waybillFetcherService);

		LocalDate day = LocalDate.of(2026, 8, 1);
		int syncedCount = syncService.syncInternetDocuments(day, day);

		assertEquals(105, syncedCount);
		assertEquals(2, insertedBatches.size());
		assertEquals(100, insertedBatches.get(0).size());
		assertEquals(5, insertedBatches.get(1).size());
	}

	@Test
	void testSyncStreetsForCity() throws Exception {
		List<String> capturedRequests = new ArrayList<>();

		WaybillFetcherServiceTest.MockScriptedHttpClient httpClient = new WaybillFetcherServiceTest.MockScriptedHttpClient((req, reqBody) -> {
			capturedRequests.add(reqBody);
			if (reqBody.contains("\"calledMethod\":\"getStreet\"")) {
				return """
				{
				  "success": true,
				  "data": [
				    {
				      "Ref": "street-ref-1",
				      "Description": "Khreshchatyk",
				      "StreetsTypeRef": "type-ref-1",
				      "StreetsType": "str."
				    },
				    {
				      "Ref": "street-ref-2",
				      "Description": "Shevchenka",
				      "StreetsTypeRef": "type-ref-1",
				      "StreetsType": "blvd."
				    }
				  ],
				  "errors": [],
				  "warnings": []
				}
				""";
			}
			return "{\"success\":true,\"data\":[],\"errors\":[]}";
		});

		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, httpClient, NovaPostClient.createDefaultMapper());

		List<com.novapost.model.Street> insertedStreets = new ArrayList<>();
		DatabaseManager stubDbManager = new DatabaseManager(new DatabaseConfig("jdbc:mysql://localhost:3306/dummy", "u", "p")) {
			@Override
			public int insertStreets(List<com.novapost.model.Street> streets) {
				insertedStreets.addAll(streets);
				return streets.size();
			}
		};

		NovaPostSyncService syncService = new NovaPostSyncService(client, stubDbManager, 0);
		int inserted = syncService.syncStreetsForCity("city-ref-kyiv");

		assertEquals(2, inserted);
		assertEquals(2, insertedStreets.size());
		assertEquals("street-ref-1", insertedStreets.get(0).ref());
		assertEquals("Khreshchatyk", insertedStreets.get(0).description());
		assertEquals("city-ref-kyiv", insertedStreets.get(0).cityRef());
		assertEquals("city-ref-kyiv", insertedStreets.get(1).cityRef());
	}
}
