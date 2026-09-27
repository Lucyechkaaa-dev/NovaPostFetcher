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
}
