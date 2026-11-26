package com.novapost;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.model.DocumentPriceResponse;
import com.novapost.model.Warehouse;
import com.novapost.model.common.PackListSpecialRequest;
import com.novapost.model.common.PickupTimeInterval;
import com.novapost.model.courier.CourierCallCreateRequest;
import com.novapost.model.document.EwTemplateListRequest;
import com.novapost.model.scansheet.ScanSheet;
import com.novapost.model.service.ReturnOrderUpdateRequest;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * The fields of the models have their natural Java types (LocalDate, LocalDateTime, LocalTime, BigDecimal, Integer, Boolean). A request still
 * puts on the wire exactly what the documentation's examples show, and an answer is read whatever spelling the API happens to use.
 */
class TypedFieldsTest{

	private static final ObjectMapper MAPPER = NovaPostClient.createDefaultMapper();
	private static final ObjectMapper STRICT = NovaPostClient.createDefaultMapper().clearProblemHandlers();

	private static JsonNode wire(Object request) throws Exception{
		return MAPPER.readTree(MAPPER.writeValueAsString(request));
	}

	private static void assertText(String expected, JsonNode node, String what){
		assertTrue(node.isTextual(), what + " must be a JSON string but was " + node);
		assertEquals(expected, node.asText(), what);
	}

	// ------------------------------------------------------------------ requests

	@Test
	void aDateIsSentAsDdMmYyyyAndANumberAsItsPlainText() throws Exception{
		JsonNode sent = wire(CourierCallCreateRequest.builder()
				.preferredDeliveryDate(LocalDate.of(2026, 11, 26)).planedWeight(new BigDecimal("5")).timeInterval("CityPickingTimeInterval3").build());

		assertText("26.11.2026", sent.path("PreferredDeliveryDate"), "PreferredDeliveryDate");
		assertText("5", sent.path("PlanedWeight"), "PlanedWeight");
		assertFalse(sent.has("ContactSenderRef"), "an unset field is not sent");
	}

	@Test
	void aDecimalKeepsItsScaleAndNeverTurnsScientific() throws Exception{
		JsonNode sent = wire(PackListSpecialRequest.builder()
				.length(new BigDecimal("1E+3")).width(new BigDecimal("0.0400")).height(new BigDecimal("120.0")).build());

		assertText("1000", sent.path("Length"), "Length");
		assertText("0.0400", sent.path("Width"), "Width");
		assertText("120.0", sent.path("Height"), "Height");
	}

	@Test
	void flagsAreSentAsTheTextTheDocumentationUses() throws Exception{
		JsonNode sent = wire(PackListSpecialRequest.builder().isNew(true).packForSale(false).build());
		assertText("1", sent.path("New"), "New");
		assertText("0", sent.path("PackForSale"), "PackForSale");

		JsonNode table = wire(EwTemplateListRequest.builder().isPickupTable(true).page(2).limit(50).build());
		assertText("true", table.path("IsPickupTable"), "IsPickupTable");
		assertText("2", table.path("Page"), "Page");
		assertText("50", table.path("Limit"), "Limit");

		assertTrue(wire(ReturnOrderUpdateRequest.builder().onlyGetPricing(true).build()).path("OnlyGetPricing").isBoolean(),
				"the one documented example that uses a real JSON boolean");
	}

	// ------------------------------------------------------------------ answers

	@Test
	void aDateTimeIsReadFromEverySpellingTheApiUses() throws Exception{
		LocalDateTime expected = LocalDateTime.of(2026, 11, 25, 13, 5, 7);
		for(String text : new String[]{"2026-11-25 13:05:07", "25.11.2026 13:05:07", "25-11-2026 13:05:07", "2026-11-25T13:05:07",
				"2026-11-25T13:05:07+02:00", "2026-11-25T13:05:07Z", "2026-11-25 13:05:07.000000"}){
			assertEquals(expected, STRICT.readValue("{\"DateTime\":\"" + text + "\"}", ScanSheet.class).dateTime(), text);
		}
		assertEquals(LocalDateTime.of(2015, 3, 20, 13, 45, 19), STRICT.readValue("{\"DateTime\":\"2015-03-20T13:45:19+00:00\"}", ScanSheet.class).dateTime(),
				"the example of the documentation: the offset is dropped, the local time kept");
		assertEquals(LocalDateTime.of(2021, 4, 2, 0, 0), STRICT.readValue("{\"WarehouseStatusDate\":\"4/2/21 0:00\"}", Warehouse.class).warehouseStatusDate(),
				"the US style of the documentation's warehouse example");
	}

	@Test
	void noDateMeansNull() throws Exception{
		for(String text : new String[]{"", " ", "null", "0000-00-00 00:00:00", "0000-00-00", "0001-01-01 00:00:00", "01.01.0001"}){
			assertNull(STRICT.readValue("{\"DateTime\":\"" + text + "\"}", ScanSheet.class).dateTime(), "[" + text + "]");
		}
		assertNull(STRICT.readValue("{\"DateTime\":null}", ScanSheet.class).dateTime());
		assertNull(STRICT.readValue("{}", ScanSheet.class).dateTime());
	}

	@Test
	void aTimeOfDayIsReadWithOrWithoutLeadingZeroAndSeconds() throws Exception{
		PickupTimeInterval interval = STRICT.readValue("{\"Number\":\"1\",\"Start\":\"8:00\",\"End\":\"17:00:00\",\"BoundaryTime\":\"\"}", PickupTimeInterval.class);

		assertEquals(LocalTime.of(8, 0), interval.start());
		assertEquals(LocalTime.of(17, 0), interval.end());
		assertNull(interval.boundaryTime());
		assertEquals("1", interval.number(), "a number that names an interval stays text");
	}

	@Test
	void decimalsWholeNumbersAndFlagsAreReadFromNumbersAndFromText() throws Exception{
		Warehouse w = STRICT.readValue("""
				{"Latitude":"50.450418000000000","Longitude":30.523541,"MaxDeclaredCost":"1 200,50","TotalMaxWeightAllowed":0,"PlaceMaxWeightAllowed":"",
				 "SelfServiceWorkplacesCount":"3","POSTerminal":"1","PostFinance":0,"BicycleParking":"false","PaymentAccess":true,"DenyToSelect":""}""", Warehouse.class);

		assertEquals(new BigDecimal("50.450418000000000"), w.latitude(), "the scale of the text is kept");
		assertEquals(new BigDecimal("30.523541"), w.longitude());
		assertEquals(new BigDecimal("1200.50"), w.maxDeclaredCost());
		assertEquals(0, BigDecimal.ZERO.compareTo(w.totalMaxWeightAllowed()));
		assertNull(w.placeMaxWeightAllowed());
		assertEquals(3, w.selfServiceWorkplacesCount());
		assertTrue(w.posTerminal());
		assertFalse(w.postFinance());
		assertFalse(w.bicycleParking());
		assertTrue(w.paymentAccess());
		assertNull(w.denyToSelect());
	}

	@Test
	void aWholeNumberIsReadFromTextWithSpacesAndFromAZeroFraction() throws Exception{
		for(String text : new String[]{"3", " 3 ", "3.0", "3.00"}){
			assertEquals(3, STRICT.readValue("{\"SelfServiceWorkplacesCount\":\"" + text.strip().replace(" ", "") + "\"}", Warehouse.class).selfServiceWorkplacesCount(), text);
		}
		assertEquals(3, STRICT.readValue("{\"SelfServiceWorkplacesCount\":3}", Warehouse.class).selfServiceWorkplacesCount());
		assertEquals(3, STRICT.readValue("{\"SelfServiceWorkplacesCount\":3.0}", Warehouse.class).selfServiceWorkplacesCount());
		assertNull(STRICT.readValue("{\"SelfServiceWorkplacesCount\":\"\"}", Warehouse.class).selfServiceWorkplacesCount());
		for(String notWhole : new String[]{"\"3.5\"", "3.5", "\"many\"", "99999999999"}){
			String json = "{\"SelfServiceWorkplacesCount\":" + notWhole + "}";
			assertThrows(JsonMappingException.class, () -> STRICT.readValue(json, Warehouse.class), json);
			assertNull(MAPPER.readValue(json, Warehouse.class).selfServiceWorkplacesCount(), json);
		}
	}

	@Test
	void aMoneyAmountIsNotADouble() throws Exception{
		DocumentPriceResponse price = STRICT.readValue("{\"Cost\":45,\"AssessedCost\":\"300.00\",\"CostRedelivery\":0.1,\"CostPack\":\"\"}", DocumentPriceResponse.class);

		assertEquals(new BigDecimal("45"), price.cost());
		assertEquals(new BigDecimal("300.00"), price.assessedCost());
		assertEquals(new BigDecimal("0.1"), price.costRedelivery(), "0.1 is exact, not 0.1000000000000000055");
		assertNull(price.costPack());
	}

	// ------------------------------------------------------------------ the value that cannot be read

	private static final String ODD = """
			{"WarehouseStatusDate":"yesterday","Latitude":"north","SelfServiceWorkplacesCount":{"a":1},"POSTerminal":"maybe","Description":"kept"}""";

	@Test
	void theClientMapperReadsAValueItCannotUseAsNullAndKeepsTheRest() throws Exception{
		Warehouse w = MAPPER.readValue(ODD, Warehouse.class);

		assertNull(w.warehouseStatusDate());
		assertNull(w.latitude());
		assertNull(w.selfServiceWorkplacesCount());
		assertNull(w.posTerminal());
		assertEquals("kept", w.description(), "one odd value never costs the rest of the record");
	}

	@Test
	void anEmptyArrayOrObjectWhereAScalarBelongsReadsAsNull() throws Exception{
		// PHP backends send [] or {} for "nothing"
		for(String empty : new String[]{"[]", "{}", "[1,2]", "{\"a\":{\"b\":[]}}"}){
			Warehouse w = MAPPER.readValue("{\"SelfServiceWorkplacesCount\":" + empty + ",\"WarehouseStatusDate\":" + empty + ",\"Latitude\":" + empty
					+ ",\"POSTerminal\":" + empty + ",\"Description\":\"kept\"}", Warehouse.class);
			assertNull(w.selfServiceWorkplacesCount(), empty);
			assertNull(w.warehouseStatusDate(), empty);
			assertNull(w.latitude(), empty);
			assertNull(w.posTerminal(), empty);
			assertEquals("kept", w.description(), "the field after the odd one is still read: " + empty);
		}
	}

	@Test
	void aStrictMapperRefusesTheSameValues() {
		for(String field : new String[]{"{\"WarehouseStatusDate\":\"yesterday\"}", "{\"Latitude\":\"north\"}", "{\"POSTerminal\":\"maybe\"}"}){
			assertThrows(JsonMappingException.class, () -> STRICT.readValue(field, Warehouse.class), field);
		}
	}
}
