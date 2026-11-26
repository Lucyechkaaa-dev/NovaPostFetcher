package com.novapost;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpModelStyle;
import com.novapost.client.NpResult;
import com.novapost.model.NpResponse;
import com.novapost.model.TrackingDocumentRef;
import com.novapost.model.TrackingRequest;
import com.novapost.model.TrackingStatus;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The envelope of an answer is not always the tidy shape of the documentation. These cases come from the live API.
 */
class NpEnvelopeShapesTest{

	private static final ObjectMapper MAPPER = NovaPostClient.createDefaultMapper();

	/**
	 * What Nova Poshta really answered on 2026-11-26 to {@code TrackingDocument/getStatusDocuments} sent without an API key
	 * (the envelope as received; {@code data} is cut down to the status line). The warning is an <em>object</em> keyed by the
	 * waybill number, not a string, although the documentation shows {@code "warnings": []}.
	 */
	private static final String LIVE_TRACKING_WITHOUT_KEY = """
			{"success":true,"data":[{"Number":"20400048799000","StatusCode":"3","Status":"Номер не знайдено"}],"errors":[],
			"warnings":[{"ID_20400048799000":"Please enter a valid phone number from the express invoice to show full information"}],
			"info":[],"messageCodes":[],"errorCodes":[],"warningCodes":[],"infoCodes":[]}""";

	private static NpResponse<TrackingStatus> read(String json) throws Exception{
		JavaType type = MAPPER.getTypeFactory().constructParametricType(NpResponse.class, TrackingStatus.class);
		return MAPPER.readValue(json, type);
	}

	@Test
	void aWarningThatIsAnObjectIsKeptAsText() throws Exception{
		NpResponse<TrackingStatus> response = read(LIVE_TRACKING_WITHOUT_KEY);

		assertTrue(response.success());
		assertEquals(List.of("ID_20400048799000: Please enter a valid phone number from the express invoice to show full information"), response.warnings());
		assertEquals("3", response.data().get(0).statusCode());
		assertTrue(response.hasWarnings());
		assertFalse(response.hasErrors());
	}

	@Test
	void everyListOfTheEnvelopeAcceptsStringsNumbersObjectsAndASingleValue() throws Exception{
		NpResponse<TrackingStatus> response = read("""
				{"success":false,"data":[],
				"errors":["plain", {"ID_1":"keyed", "ID_2":"second"}, 7, ["nested"]],
				"warnings":"one single warning",
				"info":{"anything":1},
				"messageCodes":[20001401442], "errorCodes":["20001401442"], "warningCodes":[], "infoCodes":null}""");

		assertEquals(List.of("plain", "ID_1: keyed; ID_2: second", "7", "[\"nested\"]"), response.errors());
		assertEquals(List.of("one single warning"), response.warnings());
		assertEquals(List.of("20001401442"), response.messageCodes());
		assertEquals(List.of("20001401442"), response.errorCodes());
		assertEquals(List.of(), response.warningCodes());
		assertTrue(response.hasErrors());
	}

	@Test
	void theTypedTrackingFacadeSurvivesTheLiveAnswerAndSurfacesTheWarning() throws Exception{
		HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
		server.createContext("/", exchange -> {
			exchange.getRequestBody().readAllBytes();
			byte[] answer = LIVE_TRACKING_WITHOUT_KEY.getBytes(StandardCharsets.UTF_8);
			exchange.getResponseHeaders().add("Content-Type", "application/json");
			exchange.sendResponseHeaders(200, answer.length);
			exchange.getResponseBody().write(answer);
			exchange.close();
		});
		server.start();
		try{
			NovaPostClient client = new NovaPostClient("", "http://127.0.0.1:" + server.getAddress().getPort() + "/", HttpClient.newHttpClient(),
					NovaPostClient.createDefaultMapper(), NpModelStyle.LEGACY);

			NpResult<TrackingStatus> result = client.tracking().getStatusDocuments(TrackingRequest.builder()
					.documents(List.of(TrackingDocumentRef.builder().documentNumber("20400048799000").phone("").build())).build()).result();

			assertEquals(1, result.data().size());
			assertEquals("Номер не знайдено", result.data().get(0).status());
			assertEquals(List.of("ID_20400048799000: Please enter a valid phone number from the express invoice to show full information"), result.warnings());
		}
		finally{
			server.stop(0);
		}
	}
}
