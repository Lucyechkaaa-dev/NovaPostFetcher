package com.novapost;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpModelStyle;
import com.novapost.model.InternetDocumentDeleteRequest;
import com.novapost.model.common.RefRequest;
import com.novapost.testsupport.ApiSpec;
import com.novapost.testsupport.ReadOnlyClient;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The guard that makes the live smoke tests safe to run with a real key (Nova Poshta has no sandbox for API 2.0).
 * A local server stands in for the API and records every request that reaches it.
 */
class ReadOnlyClientTest{

	private static final ObjectMapper JSON = new ObjectMapper();
	private static final String EMPTY_ANSWER = "{\"success\":true,\"data\":[],\"errors\":[],\"warnings\":[],\"info\":[],"
			+ "\"messageCodes\":[],\"errorCodes\":[],\"warningCodes\":[],\"infoCodes\":[]}";

	private HttpServer server;
	private final List<String> received = new CopyOnWriteArrayList<>(); // "modelName/calledMethod" of every request that arrived
	private ReadOnlyClient client;

	@BeforeEach
	void start() throws Exception{
		server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/", exchange -> {
			JsonNode body = JSON.readTree(exchange.getRequestBody().readAllBytes());
			received.add(body.path("modelName").asText() + "/" + body.path("calledMethod").asText());
			byte[] answer = EMPTY_ANSWER.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "application/json");
			exchange.sendResponseHeaders(200, answer.length);
			exchange.getResponseBody().write(answer);
			exchange.close();
		});
		server.start();
		client = new ReadOnlyClient("test-key", "http://127.0.0.1:" + server.getAddress().getPort() + "/", HttpClient.newHttpClient(),
				NovaPostClient.createDefaultMapper(), NpModelStyle.LEGACY);
	}

	@AfterEach
	void stop(){
		server.stop(0);
	}

	@Test
	void readsReachTheServer() throws Exception{
		client.common().getCargoTypes().rawRoot();
		client.address().getAreas().list();
		client.executeForRootNode("AdditionalServiceGeneral", "CheckPossibilityCreateReturn", Map.of("Number", "x"));
		client.getTrackingRoot("20400048799000", "");

		assertEquals(List.of("Common/getCargoTypes", "Address/getAreas", "AdditionalServiceGeneral/CheckPossibilityCreateReturn",
				"TrackingDocument/getStatusDocuments"), received);
	}

	@Test
	void everyMethodOfTheSpecIsClassifiedAndTheMutatingOnesNeverLeaveTheMachine() throws Exception{
		Set<String> mutating = new TreeSet<>();
		int refused = 0;
		for(ApiSpec.Binding b : ApiSpec.bindings()){
			boolean readsOnly = b.method().matches("(?i)^(get|search|check).*");
			assertEquals(readsOnly, ReadOnlyClient.isReadOnly(b.method()), b.model() + "/" + b.method());
			if(!readsOnly){
				mutating.add(b.method());
				assertThrows(IllegalStateException.class, () -> client.executeForRootNode(b.model(), b.method(), Map.of()), b.model() + "/" + b.method());
				refused++;
			}
		}

		// A new documented method that is not a plain read must be looked at by a person: extend this list on purpose, never by accident.
		assertEquals(new TreeSet<>(List.of("save", "update", "delete", "insertDocuments", "removeDocuments", "deleteScanSheet",
				"deleteCourierCall", "saveCourierCall", "updateCourierCall", "generateReport")), mutating);
		assertTrue(refused >= 25, "refused " + refused); // the loop really ran: 29 of the 75 documented bindings are not plain reads
		assertEquals(List.of(), received, "a refused call reached the server");
	}

	@Test
	void theTypedFacadesAreGuardedToo(){
		assertThrows(IllegalStateException.class, () -> client.internetDocument().delete(InternetDocumentDeleteRequest.of("ref")).list());
		assertThrows(IllegalStateException.class, () -> client.counterparty().delete(new RefRequest("ref")).list());
		assertThrows(IllegalStateException.class, () -> client.address().deleteCounterpartyAddress(new RefRequest("ref")).rawRoot());
		assertEquals(List.of(), received);
	}

	@Test
	void theGuardSurvivesAChangeOfModelStyle() throws Exception{
		NovaPostClient documented = client.withModelStyle(NpModelStyle.DOCUMENTED);

		assertInstanceOf(ReadOnlyClient.class, documented);
		assertThrows(IllegalStateException.class, () -> documented.executeForRootNode("InternetDocument", "save", Map.of()));
		documented.common().getCargoTypes().rawRoot();
		assertEquals(List.of("CommonGeneral/getCargoTypes"), received);
	}

	@Test
	void namesThatMerelyContainAReadingWordAreNotLetThrough(){
		for(String name : List.of("forgetAll", "resetSearch", "uncheck", "saveAndGet", "getAndDelete!", "", " get", "get ")){
			assertFalse(ReadOnlyClient.isReadOnly(name), name);
		}
		assertFalse(ReadOnlyClient.isReadOnly(null));
		assertTrue(ReadOnlyClient.isReadOnly("getDocumentList"));
		assertTrue(ReadOnlyClient.isReadOnly("searchSettlementStreets"));
		assertTrue(ReadOnlyClient.isReadOnly("CheckPossibilityChangeEW"));
	}
}
