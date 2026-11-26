package com.novapost.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.model.SettlementStreet;
import com.novapost.model.TrackingStatus;
import com.novapost.testsupport.RecordingHttpClient;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class NovaPostControllerRegressionTest{

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static NovaPostController controller(RecordingHttpClient http){
		return new NovaPostController(new NovaPostClient("k", NovaPostClient.DEFAULT_API_URL, http, NovaPostClient.createDefaultMapper()));
	}

	/** Answers a tracking request with one status per requested document. */
	private static String trackingAnswer(String body){
		try{
			List<String> out = new ArrayList<>();
			for(JsonNode d : MAPPER.readTree(body).path("methodProperties").path("Documents")){
				out.add("{\"Number\":\"" + d.path("DocumentNumber").asText() + "\",\"StatusCode\":\"9\",\"Status\":\"Відправлення отримано\"}");
			}
			return RecordingHttpClient.envelope("[" + String.join(",", out) + "]");
		}
		catch(Exception e){
			throw new IllegalStateException(e);
		}
	}

	@Test
	void trackingReturnsTheStatusesInsteadOfAnEmptyList(){
		// the controller used to read "data" from an already-unwrapped array and always returned an empty list
		RecordingHttpClient http = new RecordingHttpClient(NovaPostControllerRegressionTest::trackingAnswer);

		List<TrackingStatus> statuses = controller(http).getTracking("20400000009237");

		assertEquals(1, statuses.size());
		assertEquals("20400000009237", statuses.getFirst().number());
		assertEquals("9", statuses.getFirst().statusCode());
		assertEquals("Відправлення отримано", statuses.getFirst().status());
		assertEquals(1, controller(http).getTrackingNode("20400000009237").size());
	}

	@Test
	void trackingWithPhoneSendsItAlong(){
		RecordingHttpClient http = new RecordingHttpClient(NovaPostControllerRegressionTest::trackingAnswer);

		controller(http).getTracking("20400000009237", "380501112233");

		var doc = http.request(0).path("methodProperties").path("Documents").get(0);
		assertEquals("380501112233", doc.path("Phone").asText());
		assertEquals("getStatusDocuments", http.request(0).path("calledMethod").asText());
	}

	@Test
	void trackingIsSplitIntoRequestsOfAtMost100(){
		RecordingHttpClient http = new RecordingHttpClient(NovaPostControllerRegressionTest::trackingAnswer);
		List<String> ttns = new ArrayList<>();
		for(int i = 0; i < 250; i++){
			ttns.add(String.format("2040%010d", i));
		}

		List<TrackingStatus> statuses = controller(http).getTracking(ttns);

		assertEquals(3, http.calls());
		assertEquals(100, http.request(0).path("methodProperties").path("Documents").size());
		assertEquals(100, http.request(1).path("methodProperties").path("Documents").size());
		assertEquals(50, http.request(2).path("methodProperties").path("Documents").size());
		assertEquals(250, statuses.size());
		assertEquals(ttns.getFirst(), statuses.getFirst().number());
		assertEquals(ttns.getLast(), statuses.getLast().number());
	}

	@Test
	void trackingErrorsSurfaceWithTheirCodes(){
		RecordingHttpClient http = RecordingHttpClient.always("{\"success\":false,\"data\":[],\"errors\":[\"API key is invalid\"],\"errorCodes\":[\"20000200068\"]}");

		NovaPostApiException e = assertThrows(NovaPostApiException.class, () -> controller(http).getTracking("20400000009237"));

		assertEquals(List.of("API key is invalid"), e.getErrors());
		assertEquals(List.of("20000200068"), e.getErrorCodes());
		assertTrue(e.getMessage().contains("20000200068"), e.getMessage());
	}

	@Test
	void searchStreetsUnwrapsTheAddressesArray(){
		String answer = RecordingHttpClient.envelope("[{\"TotalCount\":\"2\",\"Addresses\":["
				+ "{\"SettlementRef\":\"s\",\"SettlementStreetRef\":\"r1\",\"SettlementStreetDescription\":\"Хрещатик\",\"Present\":\"вул. Хрещатик\",\"StreetsTypeDescription\":\"вул.\"},"
				+ "{\"SettlementRef\":\"s\",\"SettlementStreetRef\":\"r2\",\"SettlementStreetDescription\":\"Хрещатицька\",\"Present\":\"вул. Хрещатицька\",\"StreetsTypeDescription\":\"вул.\"}]}]");
		RecordingHttpClient http = RecordingHttpClient.always(answer);
		NovaPostController controller = controller(http);

		List<SettlementStreet> streets = controller.searchStreets("s", "Хрещ");
		assertEquals(2, streets.size());
		assertEquals("Хрещатик", streets.getFirst().description());
		assertEquals("r2", streets.get(1).ref());

		List<Map<String, Object>> projected = controller.searchStreets("s", "Хрещ", Map.of("Present", "", "SettlementStreetRef", ""));
		assertEquals(2, projected.size());
		assertEquals("вул. Хрещатик", projected.getFirst().get("Present"));

		List<SettlementStreet> asClass = controller.searchStreets("s", "Хрещ", SettlementStreet.class);
		assertEquals("r1", asClass.getFirst().settlementStreetRef());
	}

	@Test
	void waybillRecipesReachTheWireThroughTheLifecycleLayer(){
		RecordingHttpClient http = RecordingHttpClient.always(RecordingHttpClient.envelope(
				"[{\"Ref\":\"r\",\"CostOnSite\":\"45\",\"EstimatedDeliveryDate\":\"26.11.2026\",\"IntDocNumber\":\"20600000002545\",\"TypeDocument\":\"InternetDocument\"}]"));
		com.novapost.model.ParticipantRouting routing = com.novapost.model.ParticipantRouting.builder()
				.citySenderRef("cs").senderRef("s").senderAddressRef("sa").contactSenderRef("cps").sendersPhone("380660000000")
				.cityRecipientRef("cr").recipientRef("r").recipientAddressRef("ra").contactRecipientRef("cpr").recipientsPhone("380660000001").build();

		var response = controller(http).createWaybill(routing, com.novapost.model.CargoDetails.ofBox("Goods", 1.5, 500, 30, 40, 20),
				b -> b.toPostomat("11/3002").redBoxBarcode("RB1"));

		assertEquals("20600000002545", response.intDocNumber());
		assertEquals(java.time.LocalDate.of(2026, 11, 26), response.estimatedDeliveryDate());
		var props = http.request(0).path("methodProperties");
		assertEquals("save", http.request(0).path("calledMethod").asText());
		assertEquals("11/3002", props.path("RecipientWarehouseIndex").asText());
		assertEquals("RB1", props.path("RedBoxBarcode").asText());
		assertEquals("30", props.path("OptionsSeat").get(0).path("volumetricWidth").asText());
	}

	@Test
	void deliveryDateCanBeAskedWithTheTimeOfDay(){
		RecordingHttpClient http = RecordingHttpClient.always(RecordingHttpClient.envelope(
				"[{\"DeliveryDate\":{\"date\":\"2026-11-27 18:00:00.000000\",\"timezone_type\":3,\"timezone\":\"Europe/Kiev\"}}]"));
		NovaPostController controller = controller(http);

		var withTime = controller.estimateDeliveryDateAt("c1", "c2", "WarehouseWarehouse", java.time.LocalDateTime.of(2026, 11, 25, 15, 30, 0));
		var dateOnly = controller.estimateDeliveryDate("c1", "c2", "WarehouseWarehouse", java.time.LocalDateTime.of(2026, 11, 25, 15, 30, 0));

		assertEquals(java.time.LocalDateTime.of(2026, 11, 27, 18, 0), withTime.orElseThrow());
		assertEquals(java.time.LocalDateTime.of(2026, 11, 27, 18, 0), dateOnly.orElseThrow());
		assertEquals("2026-11-25 15:30:00", http.request(0).path("methodProperties").path("DateTime").asText());
		assertEquals("25.11.2026", http.request(1).path("methodProperties").path("DateTime").asText());
	}

	@Test
	void warningsAreNotErrors(){
		RecordingHttpClient http = RecordingHttpClient.always("{\"success\":true,\"data\":[{\"Ref\":\"x\",\"Description\":\"d\"}],\"errors\":[],\"warnings\":[\"DateTime was corrected\"]}");

		assertEquals(1, controller(http).getWarehouseTypes().size());
	}

	/** The live tracking answer (seen 2026-11-26) carries its warning as an object keyed by the waybill, not as a string; the text must not be lost. */
	@Test
	void aWarningKeyedByTheWaybillKeepsItsText(){
		RecordingHttpClient http = RecordingHttpClient.always("{\"success\":true,\"data\":[{\"Number\":\"20400048799000\",\"StatusCode\":\"3\",\"Status\":\"Номер не знайдено\"}],"
				+ "\"errors\":[],\"warnings\":[{\"ID_20400048799000\":\"Please enter a valid phone number from the express invoice to show full information\"}]}");
		java.util.logging.Logger logger = java.util.logging.Logger.getLogger(NovaPostController.class.getName());
		List<String> logged = new ArrayList<>();
		java.util.logging.Handler handler = new java.util.logging.Handler(){
			@Override public void publish(java.util.logging.LogRecord record){
				logged.add(new java.util.logging.SimpleFormatter().formatMessage(record));
			}
			@Override public void flush(){}
			@Override public void close(){}
		};
		logger.addHandler(handler);
		try{
			assertEquals(1, controller(http).getTracking("20400048799000").size());
		}
		finally{
			logger.removeHandler(handler);
		}

		assertEquals(1, logged.size(), logged.toString());
		assertTrue(logged.getFirst().contains("ID_20400048799000: Please enter a valid phone number"), logged.getFirst());
	}

	@Test
	void anErrorKeyedByTheWaybillKeepsItsText(){
		RecordingHttpClient http = RecordingHttpClient.always("{\"success\":false,\"data\":[],\"errors\":[{\"ID_20400048799000\":\"Document number is not correct\"}],\"errorCodes\":[20000200068]}");

		NovaPostApiException e = assertThrows(NovaPostApiException.class, () -> controller(http).getTracking("20400048799000"));

		assertEquals(List.of("ID_20400048799000: Document number is not correct"), e.getErrors());
		assertEquals(List.of("20000200068"), e.getErrorCodes());
		assertTrue(e.getMessage().contains("Document number is not correct"), e.getMessage());
	}
}
