package com.novapost;

import com.fasterxml.jackson.databind.JsonNode;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.client.NpModelStyle;
import com.novapost.config.AppConfig;
import com.novapost.controller.NovaPostApiException;
import com.novapost.model.InternetDocumentListFilter;
import com.novapost.model.common.RefDescription;
import com.novapost.model.support.NpDates;
import com.novapost.testsupport.RecordingHttpClient;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class NpSupportTest{

	private static NovaPostClient client(RecordingHttpClient http, NpModelStyle style){
		return new NovaPostClient("k", NovaPostClient.DEFAULT_API_URL, http, NovaPostClient.createDefaultMapper(), style);
	}

	@Test
	void dateFormatsSeenInTheDocumentationAndTheApi(){
		LocalDateTime noon = LocalDateTime.of(2026, 11, 25, 12, 5, 7);
		assertEquals(Optional.of(noon), NpDates.parseDateTime("25.11.2026 12:05:07"));
		assertEquals(Optional.of(noon), NpDates.parseDateTime("2026-11-25 12:05:07"));
		assertEquals(Optional.of(noon), NpDates.parseDateTime("25-11-2026 12:05:07"));
		assertEquals(Optional.of(noon), NpDates.parseDateTime("2026-11-25T12:05:07"));
		assertEquals(Optional.of(noon.withSecond(0).withNano(0)), NpDates.parseDateTime("2026-11-25 12:05"));
		assertEquals(Optional.of(LocalDateTime.of(2026, 11, 25, 0, 0)), NpDates.parseDateTime("25.11.2026"));
		assertEquals(Optional.of(LocalDateTime.of(2024, 7, 10, 18, 0)), NpDates.parseDateTime("2024-07-10 18:00:00.000000"));
		assertEquals(Optional.of(LocalDate.of(2026, 11, 25)), NpDates.parseDate("25.11.2026 12:05:07"));
		assertEquals(Optional.of(LocalDate.of(2026, 11, 25)), NpDates.parseDate("2026-11-25"));
	}

	@Test
	void valuesThatMeanNoDate(){
		for(String blank : new String[]{null, "", "  ", "null", "NULL", "0000-00-00 00:00:00", "0000-00-00"}){
			assertEquals(Optional.empty(), NpDates.parseDateTime(blank), "[" + blank + "]");
		}
		assertEquals(Optional.empty(), NpDates.parseDateTime("дд.мм.рррр"), "a documentation placeholder is not a date");
	}

	@Test
	void documentedStyleRenamesLegacyModels(){
		assertEquals("InternetDocumentGeneral", NpModel.documentedName("InternetDocument"));
		assertEquals("AddressGeneral", NpModel.documentedName("Address"));
		assertEquals("TrackingDocumentGeneral", NpModel.documentedName("TrackingDocument"));
		assertEquals("AddressGeneral", NpModel.documentedName("AddressGeneral"), "already documented names stay");
		assertEquals("CarCallGeneral", NpModel.documentedName("CarCallGeneral"));
		assertEquals("Whatever", NpModel.documentedName("Whatever"), "unknown names are not touched");
		assertEquals(NpModelStyle.LEGACY, NpModelStyle.parse(null));
		assertEquals(NpModelStyle.DOCUMENTED, NpModelStyle.parse("Documented"));
		assertThrows(IllegalArgumentException.class, () -> NpModelStyle.parse("nonsense"));
	}

	@Test
	void everyOldCodePathFollowsTheModelStyle() throws Exception{
		for(NpModelStyle style : NpModelStyle.values()){
			RecordingHttpClient http = RecordingHttpClient.ok();
			NovaPostClient client = client(http, style);
			client.getInternetDocumentList(InternetDocumentListFilter.list(LocalDateTime.now(), LocalDateTime.now()));
			client.getStreet("c", 1, 10);
			client.getWarehouseTypes();
			client.getTrackingNode("20400048799000");

			String expectedDoc = style == NpModelStyle.DOCUMENTED ? "InternetDocumentGeneral" : "InternetDocument";
			String expectedAddress = style == NpModelStyle.DOCUMENTED ? "AddressGeneral" : "Address";
			String expectedTracking = style == NpModelStyle.DOCUMENTED ? "TrackingDocumentGeneral" : "TrackingDocument";
			assertEquals(expectedDoc, http.request(0).path("modelName").asText(), style.toString());
			assertEquals(expectedAddress, http.request(1).path("modelName").asText(), style.toString());
			assertEquals("AddressGeneral", http.request(2).path("modelName").asText(), "getWarehouseTypes always used AddressGeneral");
			assertEquals(expectedTracking, http.request(3).path("modelName").asText(), style.toString());
		}
	}

	@Test
	void withModelStyleSwitchesWithoutRebuildingTheTransport() throws Exception{
		RecordingHttpClient http = RecordingHttpClient.ok();
		NovaPostClient legacy = client(http, NpModelStyle.LEGACY);
		NovaPostClient documented = legacy.withModelStyle(NpModelStyle.DOCUMENTED);

		legacy.common().getCargoTypes().response();
		documented.common().getCargoTypes().response();

		assertEquals("Common", http.request(0).path("modelName").asText());
		assertEquals("CommonGeneral", http.request(1).path("modelName").asText());
		assertEquals(NpModelStyle.DOCUMENTED, documented.getModelStyle());
	}

	@Test
	void npCallTerminalOperations(){
		String data = "[{\"Ref\":\"r1\",\"Description\":\"Cargo\"},{\"Ref\":\"r2\",\"Description\":\"Documents\"}]";
		NovaPostClient client = client(RecordingHttpClient.always(RecordingHttpClient.envelope(data)), NpModelStyle.LEGACY);

		NpCall<RefDescription> call = client.common().getCargoTypes();
		assertEquals("Common", call.modelName());
		assertEquals(2, call.list().size());
		assertEquals("Cargo", call.first().orElseThrow().description());
		assertEquals(2, call.data().size());
		assertEquals(List.of(Map.of("Ref", "r1"), Map.of("Ref", "r2")), call.project(Map.of("Ref", "")));
		record Pair(String ref){

		}
		assertEquals("r2", call.as(Pair.class).get(1).ref());
	}

	@Test
	void npCallFailsOnApiErrorsWithTheirCodes(){
		String body = "{\"success\":false,\"data\":[],\"errors\":[\"Wrong key\"],\"errorCodes\":[\"20000200068\"]}";
		NpCall<RefDescription> call = client(RecordingHttpClient.always(body), NpModelStyle.LEGACY).common().getCargoTypes();

		NovaPostApiException e = assertThrows(NovaPostApiException.class, call::list);
		assertEquals(List.of("20000200068"), e.getErrorCodes());
		assertThrows(NovaPostApiException.class, call::data);
	}

	@Test
	void npCallIsLazy(){
		RecordingHttpClient http = RecordingHttpClient.ok();
		client(http, NpModelStyle.LEGACY).common().getCargoTypes();

		assertEquals(0, http.calls(), "nothing is sent before a terminal operation");
	}

	@Test
	void appConfigReadsTheModelStyle(){
		assertEquals(NpModelStyle.LEGACY, AppConfig.fromArgs(new String[]{"--api-key=k"}).modelStyle());
		assertEquals(NpModelStyle.DOCUMENTED, AppConfig.fromArgs(new String[]{"--api-key=k", "--model-style=documented"}).modelStyle());
	}

	@Test
	void booleanFlagsFromTheApiAreCaseAndEncodingTolerant() throws Exception{
		var mapper = NovaPostClient.createDefaultMapper();
		record Flags(@com.fasterxml.jackson.annotation.JsonProperty("A") @com.fasterxml.jackson.databind.annotation.JsonDeserialize(using = com.novapost.model.support.NpBooleanDeserializer.class) Boolean a){

		}
		assertEquals(true, mapper.readValue("{\"A\":\"TRUE\"}", Flags.class).a());
		assertEquals(false, mapper.readValue("{\"A\":\"0\"}", Flags.class).a());
		assertNull(mapper.readValue("{\"A\":\"\"}", Flags.class).a());
		assertNull(mapper.readValue("{\"A\":null}", Flags.class).a());

		// a value that is no boolean: the client mapper reads null (and logs it once), a strict mapper refuses
		assertNull(mapper.readValue("{\"A\":\"maybe\"}", Flags.class).a());
		assertNull(mapper.readValue("{\"A\":{\"nested\":1}}", Flags.class).a(), "an object where a flag belongs is skipped");
		assertThrows(com.fasterxml.jackson.databind.JsonMappingException.class,
				() -> NovaPostClient.createDefaultMapper().clearProblemHandlers().readValue("{\"A\":\"maybe\"}", Flags.class));
	}

	@Test
	void documentedExampleRequestsReachTheWire() throws Exception{
		RecordingHttpClient http = RecordingHttpClient.ok();
		NovaPostClient client = client(http, NpModelStyle.DOCUMENTED);

		client.internetDocument().delete(com.novapost.model.InternetDocumentDeleteRequest.of("ref-1")).response();

		JsonNode sent = http.request(0);
		assertEquals("InternetDocumentGeneral", sent.path("modelName").asText());
		assertEquals("delete", sent.path("calledMethod").asText());
		assertEquals("ref-1", sent.path("methodProperties").path("DocumentRefs").asText());
	}
}
