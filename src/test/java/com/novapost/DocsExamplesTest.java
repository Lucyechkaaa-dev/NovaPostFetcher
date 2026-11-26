package com.novapost;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.model.*;
import com.novapost.model.address.City;
import com.novapost.model.counterparty.CounterpartyOptions;
import com.novapost.model.document.*;
import com.novapost.model.scansheet.ScanSheetInsertResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Payloads copied from the examples on https://developers.novaposhta.ua/documentation (placeholders such as
 * {@code дд.мм.рррр} replaced by real dates): responses must deserialize into the models, requests must serialize to the
 * documented JSON.
 */
class DocsExamplesTest{

	private final ObjectMapper mapper = NovaPostClient.createDefaultMapper();

	private <T> NpResponse<T> parse(String json, Class<T> type) throws Exception{
		JavaType t = mapper.getTypeFactory().constructParametricType(NpResponse.class, type);
		return mapper.readValue(json, t);
	}

	private JsonNode json(String text) throws Exception{
		return mapper.readTree(text);
	}

	// ------------------------------------------------------------------ responses

	@Test
	void warehouseTypesKeepTheirDescription() throws Exception{
		var r = parse("{\"success\":true,\"data\":[{\"Ref\":\"6f8c7162-4b72-4b0a-88e5-906948c6a92f\",\"Description\":\"Поштомат\",\"DescriptionRu\":\"Почтомат\"}]}", WarehouseTypes.class);

		assertEquals("Поштомат", r.data().getFirst().description());
		assertEquals("Почтомат", r.data().getFirst().descriptionRu());
	}

	@Test
	void searchSettlementStreetsIsAWrapperWithAddresses() throws Exception{
		String body = """
				{"success":true,"data":[{"TotalCount":"1","Addresses":[{"SettlementRef":"e718a680-4b33-11e4-ab6d-005056801329",
				"SettlementStreetRef":"ad090b1f-6845-11e6-8304-00505688561d","SettlementStreetDescription":"Хрещатик","Present":"вул. Хрещатик",
				"StreetsType":"0f1d7fbb-4bba-11e4-ab6d-005056801329","StreetsTypeDescription":"вул.",
				"Location":{"lat":50.44806099962443,"lon":30.52225599065423},"SettlementStreetDescriptionRu":"Крещатик"}]}],
				"errors":[],"warnings":[],"info":[],"messageCodes":[],"errorCodes":[],"warningCodes":[],"infoCodes":[]}
				""";
		var r = parse(body, SettlementStreetSearchResult.class);

		assertEquals(1, r.data().size());
		assertEquals(1, r.data().getFirst().totalCount());
		SettlementStreet street = r.data().getFirst().streets().getFirst();
		assertEquals("Хрещатик", street.settlementStreetDescription());
		assertEquals("Хрещатик", street.description());
		assertEquals("ad090b1f-6845-11e6-8304-00505688561d", street.ref());
		assertEquals("вул.", street.streetsTypeDescription());
		assertEquals(new BigDecimal("50.44806099962443"), street.location().lat());
		assertEquals(new BigDecimal("30.52225599065423"), street.location().lon());
		assertEquals(List.of(), r.warningCodes());
	}

	@Test
	void searchSettlementsExample() throws Exception{
		String body = """
				{"success":true,"data":[{"TotalCount":"235","Addresses":[{"Present":"м. Київ, Київська обл.","Warehouses":6382,"MainDescription":"Київ",
				"Area":"Київська","Region":" ","SettlementTypeCode":"м.","Ref":"e718a680-4b33-11e4-ab6d-005056801329","DeliveryCity":"8d5a980d-391c-11dd-90d9-001a92567626",
				"AddressDeliveryAllowed":true,"StreetsAvailability":true,"ParentRegionTypes":"область","ParentRegionCode":"обл.","RegionTypes":" ","RegionTypesCode":" "}]}]}
				""";
		SearchSettlementItem item = parse(body, SearchSettlementItem.class).data().getFirst();

		assertEquals(235, item.totalCount());
		var address = item.addresses().getFirst();
		assertEquals("Київ", address.mainDescription());
		assertEquals(6382, address.warehouses());
		assertTrue(address.addressDeliveryAllowed());
		assertEquals("область", address.parentRegionTypes());
	}

	@Test
	void documentPriceExampleWithTariffZone() throws Exception{
		String body = "{\"success\":true,\"data\":[{\"AssessedCost\":\"300\",\"Cost\":\"45\",\"CostRedelivery\":\"100\","
				+ "\"TZoneInfo\":{\"TzoneName\":\"Тарифназона:Тарифназона4\",\"TzoneID\":\"4\"},\"CostPack\":\"4\"}]}";
		DocumentPriceResponse price = parse(body, DocumentPriceResponse.class).data().getFirst();

		assertEquals(new BigDecimal("45"), price.cost());
		assertNumber("300", price.assessedCost());
		assertNumber("100", price.costRedelivery());
		assertNumber("4", price.costPack());
		assertEquals(4, price.tzoneInfo().tzoneId());
		assertEquals("Тарифназона:Тарифназона4", price.tzoneInfo().tzoneName());
	}

	@Test
	void deliveryDateExample() throws Exception{
		String body = "{\"success\":true,\"data\":[{\"DeliveryDate\":{\"date\":\"2024-07-10 18:00:00.000000\",\"timezone_type\":3,\"timezone\":\"Europe/Kiev\"}}]}";

		assertEquals(LocalDateTime.of(2024, 7, 10, 18, 0), parse(body, DocumentDeliveryDateResponse.class).data().getFirst().getDeliveryDateTime());
	}

	@Test
	void documentListAcceptsEveryDateFormatAndKeepsTheDocumentedFields() throws Exception{
		String template = """
				{"success":true,"data":[{"Ref":"3b0c0e2a-0000-0000-0000-000000000000","DateTime":"%s","PreferredDeliveryDate":"26.11.2026",
				"RecipientDateTime":"27.11.2026","EWDateCreated":"%s","Weight":"5","SeatsAmount":"2","IntDocNumber":"20400030200931","Cost":"15000",
				"CitySender":"c1","CityRecipient":"c2","SenderAddress":"a1","RecipientAddress":"a2","CostOnSite":"45","PayerType":"Sender",
				"PaymentMethod":"Cash","AfterpaymentOnGoodsCost":"500","PackingNumber":"123456","RejectionReason":"причина","StateId":"41",
				"StateName":"Відправлення в місті Києві","ScanSheetNumber":""}]}
				""";
		for(String[] dates : new String[][]{{"25.11.2026", "25.11.2026"}, {"2026-11-25 14:30:00", "2026-11-25 14:30:00"}, {"25.11.2026 14:30:00", "25-11-2026 14:30:00"}}){
			InternetDocumentListItem item = parse(template.formatted(dates[0], dates[1]), InternetDocumentListItem.class).data().getFirst();

			assertEquals(LocalDate.of(2026, 11, 25), item.dateTime().toLocalDate(), dates[0]);
			assertEquals(new BigDecimal("15000"), item.cost());
			assertNumber("45", item.costOnSite());
			assertEquals("Cash", item.paymentMethod());
			assertNumber("500", item.afterpaymentOnGoodsCost());
			assertEquals(41, item.stateId());
			assertEquals("123456", item.packingNumber());
			assertEquals(LocalDate.of(2026, 11, 26), item.preferredDeliveryDate());
		}
	}

	@Test
	void fullWaybillViewFromTheCourierCallDocumentList() throws Exception{
		String body = """
				{"success":true,"data":[{"PayerType":"Recipient","ScanSheetRef":"","BackwardDeliveryCargoType":"Гроші","EWNumber":"null","Number":"20400009196210",
				"OriginalGeoData":[{"RecipientAddressName":" ","RecipientArea":" ","RecipientCityName":" "}],"StateId":"1","RecipientDateTime":"null",
				"IntDocNumber":"20400009196210","BackwardDeliverySum":"200","EWDateCreated":"2025-02-11 11:29:00","bCardPaymentPayed":"false",
				"CitySenderDescription":"Київ","SenderDescription":"Р.Б","SenderAddress":"7b422fc3-e1b8-11e3-8c4a-0050568002cf","Weight":"3","System":"API",
				"BackwardDeliveryData":[],"SettlmentAddressData":[]}]}
				""";
		InternetDocument d = parse(body, InternetDocument.class).data().getFirst();

		assertEquals("20400009196210", d.intDocNumber());
		assertEquals("Гроші", d.backwardDeliveryCargoType());
		assertEquals(LocalDateTime.of(2025, 2, 11, 11, 29, 0), d.ewDateCreated());
		assertEquals(Boolean.FALSE, d.bCardPaymentPayed());
		assertEquals("API", d.system());
		assertTrue(d.originalGeoData().isArray());
		assertEquals("Київ", d.citySenderDescription());
	}

	@Test
	void counterpartyOptionsReadBooleansWhicheverWayTheyAreEncoded() throws Exception{
		String body = """
				{"success":true,"data":[{"CanSentFromPostomat":true,"CarCall":"1","CanNonCashPayment":0,"SecurePayment":"true","Services":"","DebtorParams":[],
				"PrintMarkingAllowedTypes":{"zebra":true}}]}
				""";
		CounterpartyOptions o = parse(body, CounterpartyOptions.class).data().getFirst();

		assertTrue(o.canSentFromPostomat());
		assertTrue(o.carCall());
		assertFalse(o.canNonCashPayment());
		assertTrue(o.securePayment());
		assertNull(o.services());
		assertTrue(o.printMarkingAllowedTypes().path("zebra").asBoolean());
	}

	@Test
	void citiesAcceptConglomeratesAsAnArray() throws Exception{
		String body = "{\"success\":true,\"data\":[{\"Description\":\"Київ\",\"Ref\":\"8d5a980d-391c-11dd-90d9-001a92567626\",\"IsBranch\":\"0\",\"Conglomerates\":[\"x\"],\"CityID\":\"2\"}]}";
		City city = parse(body, City.class).data().getFirst();

		assertEquals("Київ", city.description());
		assertTrue(city.conglomerates().isArray());
		assertEquals(2, city.cityID());
	}

	@Test
	void scanSheetInsertResultKeepsItsNestedLists() throws Exception{
		String body = "{\"success\":true,\"data\":[{\"Description\":\"d\",\"Ref\":\"r\",\"Number\":\"105-00000595\",\"Date\":\"25.11.2026\",\"Errors\":[],\"Warnings\":[],\"Data\":{},\"Success\":[{\"Number\":\"2040\"}]}]}";
		ScanSheetInsertResult r = parse(body, ScanSheetInsertResult.class).data().getFirst();

		assertEquals("105-00000595", r.number());
		assertTrue(r.success().isArray());
	}

	@Test
	void envelopeCarriesAllCodeLists() throws Exception{
		String body = "{\"success\":false,\"data\":[],\"errors\":[\"API key is invalid\"],\"warnings\":[\"w\"],\"info\":{\"totalCount\":3},"
				+ "\"messageCodes\":[],\"errorCodes\":[\"20000200068\"],\"warningCodes\":[\"1\"],\"infoCodes\":[\"2\"]}";
		NpResponse<JsonNode> r = parse(body, JsonNode.class);

		assertTrue(r.hasErrors());
		assertTrue(r.hasWarnings());
		assertEquals(List.of("20000200068"), r.errorCodes());
		assertEquals(List.of("1"), r.warningCodes());
		assertEquals(List.of("2"), r.infoCodes());
	}

	@Test
	void aSingleObjectInsteadOfAnArrayIsAccepted() throws Exception{
		NpResponse<WarehouseTypes> r = parse("{\"success\":true,\"data\":{\"Ref\":\"x\",\"Description\":\"d\"}}", WarehouseTypes.class);

		assertEquals(1, r.data().size());
	}

	// ------------------------------------------------------------------ requests

	private void assertJson(String expected, Object request) throws Exception{
		assertEquals(json(expected), json(mapper.writeValueAsString(request)), "serialized: " + mapper.writeValueAsString(request));
	}

	private static final String PHONE = "380660000000";

	private InternetDocumentSaveRequest.Builder base(){
		return InternetDocumentSaveRequest.builder().payerType("Sender").paymentMethod("Cash").dateTime(LocalDate.of(2026, 11, 25))
				.description("Додатковий опис відправлення").citySender("cs").sender("s").senderAddress("sa").contactSender("cps")
				.sendersPhone(PHONE).recipientsPhone(PHONE);
	}

	private InternetDocumentSaveRequest.Builder byRefs(InternetDocumentSaveRequest.Builder b){
		return b.cityRecipient("cr").recipient("r").recipientAddress("ra").contactRecipient("cpr");
	}

	private static final String COMMON = "\"PayerType\":\"Sender\",\"PaymentMethod\":\"Cash\",\"DateTime\":\"25.11.2026\",\"Description\":\"Додатковий опис відправлення\","
			+ "\"CitySender\":\"cs\",\"Sender\":\"s\",\"SenderAddress\":\"sa\",\"ContactSender\":\"cps\",\"SendersPhone\":\"380660000000\",\"RecipientsPhone\":\"380660000000\"";
	private static final String REFS = "\"CityRecipient\":\"cr\",\"Recipient\":\"r\",\"RecipientAddress\":\"ra\",\"ContactRecipient\":\"cpr\"";
	private static final String SEAT = "{\"volumetricVolume\":\"1\",\"volumetricWidth\":\"30\",\"volumetricLength\":\"30\",\"volumetricHeight\":\"30\",\"weight\":\"20\"}";

	private static InternetDocumentSaveRequest.OptionSeat seat(){
		return new InternetDocumentSaveRequest.OptionSeat(new BigDecimal("1"), new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("20"));
	}

	/** Decimals compare by value: the scale the API sends (45 or 45.00) is not the point of these tests. */
	private static void assertNumber(String expected, BigDecimal actual){
		assertNotNull(actual, expected);
		assertEquals(0, new BigDecimal(expected).compareTo(actual), expected + " but was " + actual);
	}

	@Test
	void saveVariant1Standard() throws Exception{
		assertJson("{" + COMMON + "," + REFS + ",\"SenderWarehouseIndex\":\"101/102\",\"RecipientWarehouseIndex\":\"101/102\",\"CargoType\":\"Cargo\","
						+ "\"VolumeGeneral\":\"0.45\",\"Weight\":\"0.5\",\"ServiceType\":\"DoorsWarehouse\",\"SeatsAmount\":\"2\",\"Cost\":\"15000\"}",
				byRefs(base()).senderWarehouseIndex("101/102").recipientWarehouseIndex("101/102").cargoType("Cargo").volumeGeneral(new BigDecimal("0.45"))
						.weight(new BigDecimal("0.5")).serviceType("DoorsWarehouse").seatsAmount(2).cost(new BigDecimal("15000")).build());
	}

	@Test
	void saveVariant2ToPostomat() throws Exception{
		assertJson("{" + COMMON + "," + REFS + ",\"SenderWarehouseIndex\":\"101/102\",\"RecipientWarehouseIndex\":\"11/3002\",\"CargoType\":\"Cargo\","
						+ "\"Weight\":\"0.5\",\"ServiceType\":\"DoorsWarehouse\",\"SeatsAmount\":\"2\",\"Cost\":\"6000\",\"OptionsSeat\":[" + SEAT + "]}",
				byRefs(base()).senderWarehouseIndex("101/102").toPostomat("11/3002").cargoType("Cargo").weight(new BigDecimal("0.5"))
						.serviceType("DoorsWarehouse").seatsAmount(2).cost(new BigDecimal("6000")).addOptionSeat(seat()).build());
	}

	@Test
	void saveVariant3SeveralCargoTypesPallet() throws Exception{
		String seat10 = "{\"volumetricVolume\":\"10\",\"volumetricWidth\":\"30\",\"volumetricLength\":\"30\",\"volumetricHeight\":\"30\",\"weight\":\"20\"}";
		InternetDocumentSaveRequest.OptionSeat s = new InternetDocumentSaveRequest.OptionSeat(new BigDecimal("10"), new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("20"));
		assertJson("{" + COMMON + "," + REFS + ",\"CargoType\":\"Pallet\",\"Weight\":\"280\",\"ServiceType\":\"DoorsDoors\",\"SeatsAmount\":\"2\","
						+ "\"Cost\":\"15000\",\"OptionsSeat\":[" + seat10 + "," + seat10 + "]}",
				byRefs(base()).cargoType("Pallet").weight(new BigDecimal("280")).serviceType("DoorsDoors").seatsAmount(2).cost(new BigDecimal("15000"))
						.addOptionSeat(s).addOptionSeat(s).build());
	}

	@Test
	void saveVariant4AdditionalServicesPacking() throws Exception{
		String packed = "{\"volumetricVolume\":\"1\",\"volumetricWidth\":\"30\",\"volumetricLength\":\"30\",\"volumetricHeight\":\"30\",\"weight\":\"2\",\"packRef\":\"1499fa4a-d26e-11e1-95e4-0026b97ed48a\"}";
		assertJson("{" + COMMON + "," + REFS + ",\"CargoType\":\"Cargo\",\"VolumeGeneral\":\"0.45\",\"Weight\":\"0.5\",\"ServiceType\":\"DoorsWarehouse\","
						+ "\"SeatsAmount\":\"2\",\"Cost\":\"15000\",\"RedBoxBarcode\":\"\",\"OptionsSeat\":[" + packed + "]}",
				byRefs(base()).cargoType("Cargo").volumeGeneral(new BigDecimal("0.45")).weight(new BigDecimal("0.5")).serviceType("DoorsWarehouse").seatsAmount(2).cost(new BigDecimal("15000"))
						.redBoxBarcode("").addOptionSeat(new InternetDocumentSaveRequest.OptionSeat(new BigDecimal("1"), new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("30"), new BigDecimal("2")).withPacking("1499fa4a-d26e-11e1-95e4-0026b97ed48a")).build());
	}

	@Test
	void saveVariant5ToABranchGivenAsText() throws Exception{
		assertJson("{" + COMMON + ",\"CargoType\":\"Cargo\",\"VolumeGeneral\":\"0.45\",\"Weight\":\"0.5\",\"ServiceType\":\"DoorsWarehouse\",\"SeatsAmount\":\"2\","
						+ "\"Cost\":\"15000\",\"NewAddress\":\"1\",\"RecipientCityName\":\"Київ\",\"RecipientArea\":\"\",\"RecipientAreaRegions\":\"\","
						+ "\"RecipientAddressName\":\"1\",\"RecipientHouse\":\"\",\"RecipientFlat\":\"\",\"RecipientName\":\"Іванов Іван Іванович\","
						+ "\"RecipientType\":\"PrivatePerson\",\"SettlementType\":\"місто\",\"OwnershipForm\":\"of\",\"RecipientContactName\":\"Іванов Іван Іванович\",\"EDRPOU\":\"12345678\"}",
				base().cargoType("Cargo").volumeGeneral(new BigDecimal("0.45")).weight(new BigDecimal("0.5")).serviceType("DoorsWarehouse").seatsAmount(2).cost(new BigDecimal("15000"))
						.recipientBranchByText("Київ", "1", "Іванов Іван Іванович", "PrivatePerson", "місто")
						.organizationRecipient("12345678", "of", "Іванов Іван Іванович").build());
	}

	@Test
	void saveVariant6BackwardDelivery() throws Exception{
		assertJson("{" + COMMON + "," + REFS + ",\"CargoType\":\"Cargo\",\"Weight\":\"0.5\",\"ServiceType\":\"DoorsWarehouse\",\"SeatsAmount\":\"2\","
						+ "\"Cost\":\"15000\",\"NewAddress\":\"1\",\"OptionsSeat\":[" + SEAT + "],\"BackwardDeliveryData\":[{\"PayerType\":\"Sender\","
						+ "\"CargoType\":\"Documents\",\"Services\":{\"Attorney\":true,\"WaybillNewPostWithStamp\":true,\"UserActions\":\"UserCallSender\"}}]}",
				byRefs(base()).cargoType("Cargo").weight(new BigDecimal("0.5")).serviceType("DoorsWarehouse").seatsAmount(2).cost(new BigDecimal("15000")).newAddress(true)
						.addOptionSeat(seat()).addBackwardDelivery(new InternetDocumentSaveRequest.BackwardDeliveryData("Sender", "Documents",
								Map.of("Attorney", true, "WaybillNewPostWithStamp", true, "UserActions", "UserCallSender"))).build());
	}

	@Test
	void saveVariant7FromAPostomat() throws Exception{
		String cell = "{\"volumetricVolume\":\"1\",\"volumetricWidth\":\"30\",\"volumetricLength\":\"30\",\"volumetricHeight\":\"30\",\"weight\":\"20\",\"Ref\":\"cd00107e-2e73-41eb-9056-85a9931c6223\"}";
		assertJson("{" + COMMON + "," + REFS + ",\"ServiceType\":\"WarehouseDoors\",\"Weight\":\"0.5\",\"RecipientWarehouseIndex\":\"11/3002\","
						+ "\"CargoType\":\"Parcel\",\"SenderWarehouseIndex\":\"11/3002\",\"Cost\":\"6000\",\"SeatsAmount\":\"1\",\"OptionsSeat\":[" + cell + "]}",
				byRefs(base()).serviceType("WarehouseDoors").weight(new BigDecimal("0.5")).toPostomat("11/3002").fromPostomat("11/3002").cargoType("Parcel")
						.cost(new BigDecimal("6000")).seatsAmount(1).addOptionSeat(seat().withCell("cd00107e-2e73-41eb-9056-85a9931c6223")).build());
	}

	@Test
	void saveVariant8ToAnAddressGivenAsText() throws Exception{
		assertJson("{" + COMMON + ",\"RecipientAddressNote\":\"Вільний текст\",\"CargoType\":\"Cargo\",\"VolumeGeneral\":\"0.45\",\"Weight\":\"0.5\","
						+ "\"ServiceType\":\"WarehouseDoors\",\"SeatsAmount\":\"2\",\"Cost\":\"15000\",\"NewAddress\":\"1\",\"RecipientCityName\":\"Київ\","
						+ "\"RecipientArea\":\"Київська\",\"RecipientAreaRegions\":\"Броварський\",\"RecipientAddressName\":\"Київська\",\"RecipientHouse\":\"1\","
						+ "\"RecipientFlat\":\"25\",\"RecipientName\":\"Іванов Іван Іванович\",\"RecipientType\":\"PrivatePerson\",\"SettlementType\":\"Київська\","
						+ "\"OwnershipForm\":\"of\",\"RecipientContactName\":\"Іванов Іван Іванович\",\"EDRPOU\":\"12345678\"}",
				base().recipientAddressNote("Вільний текст").cargoType("Cargo").volumeGeneral(new BigDecimal("0.45")).weight(new BigDecimal("0.5")).serviceType("WarehouseDoors")
						.seatsAmount(2).cost(new BigDecimal("15000"))
						.recipientAddressByText("Київ", "Київська", "Броварський", "Київська", "1", "25", "Іванов Іван Іванович", "PrivatePerson", "Київська")
						.organizationRecipient("12345678", "of", "Іванов Іван Іванович").build());
	}

	@Test
	void updateAndToBuilderRoundTrip() throws Exception{
		InternetDocumentSaveRequest request = byRefs(base()).ref("doc-ref").cargoType("Cargo").weight(new BigDecimal("0.5")).serviceType("DoorsWarehouse").seatsAmount(2)
				.cost(new BigDecimal("15000")).recipientBranchByText("Київ", "1", "n", "PrivatePerson", "місто").addOptionSeat(seat()).build();

		assertEquals(request, request.toBuilder().build());
		assertEquals("doc-ref", json(mapper.writeValueAsString(request)).path("Ref").asText());
	}

	@Test
	void documentPriceExampleWithEveryOptionalParameter() throws Exception{
		DocumentPriceRequest request = new DocumentPriceRequest("c1", "c2", new BigDecimal("0.1"), "WarehouseWarehouse", new BigDecimal("300"), "Cargo", 2,
				RedeliveryCalculate.builder().cargoType("Money").amount(new BigDecimal("100")).build(), 1, "p", new BigDecimal("100"),
				List.of(CargoDetailItem.builder().cargoDescription("d").amount(2).build()), "d");

		assertJson("{\"CitySender\":\"c1\",\"CityRecipient\":\"c2\",\"Weight\":\"0.1\",\"ServiceType\":\"WarehouseWarehouse\",\"Cost\":\"300\","
				+ "\"CargoType\":\"Cargo\",\"SeatsAmount\":\"2\",\"RedeliveryCalculate\":{\"CargoType\":\"Money\",\"Amount\":\"100\"},\"PackCount\":\"1\","
				+ "\"PackRef\":\"p\",\"Amount\":\"100\",\"CargoDetails\":[{\"CargoDescription\":\"d\",\"Amount\":\"2\"}],\"CargoDescription\":\"d\"}", request);
	}

	@Test
	void basicPriceRequestStaysCompact() throws Exception{
		assertJson("{\"CitySender\":\"c1\",\"CityRecipient\":\"c2\",\"Weight\":\"2.0\",\"ServiceType\":\"WarehouseWarehouse\",\"Cost\":\"1000\",\"CargoType\":\"Parcel\",\"SeatsAmount\":\"1\"}",
				new DocumentPriceRequest("c1", "c2", new BigDecimal("2.0"), "WarehouseWarehouse", new BigDecimal("1000"), "Parcel", 1));
	}

	@Test
	void deliveryDateInBothFormats() throws Exception{
		assertJson("{\"DateTime\":\"2024-05-31 15:00:00\",\"ServiceType\":\"WarehouseWarehouse\",\"CitySender\":\"c1\",\"CityRecipient\":\"c2\"}",
				DocumentDeliveryDateRequest.ofDateTime(LocalDateTime.of(2024, 5, 31, 15, 0), "WarehouseWarehouse", "c1", "c2"));
		assertJson("{\"DateTime\":\"31.05.2024\",\"ServiceType\":\"WarehouseWarehouse\",\"CitySender\":\"c1\",\"CityRecipient\":\"c2\"}",
				new DocumentDeliveryDateRequest(LocalDate.of(2024, 5, 31), "WarehouseWarehouse", "c1", "c2"));
	}

	@Test
	void documentListFilterModes() throws Exception{
		LocalDateTime from = LocalDateTime.of(2026, 11, 1, 0, 0), to = LocalDateTime.of(2026, 11, 25, 0, 0);

		JsonNode paged = json(mapper.writeValueAsString(InternetDocumentListFilter.byNumber("20400009196210", from, to, 2, 100)));
		assertEquals("01.11.2026", paged.path("DateTimeFrom").asText());
		assertEquals("25.11.2026", paged.path("DateTimeTo").asText());
		assertEquals(0, paged.path("GetFullList").asInt(-1), "paged mode");
		assertEquals(2, paged.path("Page").asInt());
		assertEquals("20400009196210", paged.path("IntDocNumber").asText());

		JsonNode capped = json(mapper.writeValueAsString(InternetDocumentListFilter.list(from, to)));
		assertEquals(1, capped.path("GetFullList").asInt(-1), "capped list mode");
		assertTrue(capped.path("Page").isMissingNode(), "the one-shot list needs no page");

		JsonNode money = json(mapper.writeValueAsString(InternetDocumentListFilter.list(from, to).withRedeliveryMoney()));
		assertEquals(1, money.path("RedeliveryMoney").asInt());
	}

	@Test
	void trackingRequestExample() throws Exception{
		TrackingRequest request = TrackingRequest.builder().documents(List.of(
				TrackingDocumentRef.builder().documentNumber("20400048799000").phone("380600000000").build(),
				TrackingDocumentRef.builder().documentNumber("20400048799001").phone("380600000000").build())).build();

		assertJson("{\"Documents\":[{\"DocumentNumber\":\"20400048799000\",\"Phone\":\"380600000000\"},{\"DocumentNumber\":\"20400048799001\",\"Phone\":\"380600000000\"}]}", request);
	}

	@Test
	void newServicesBuildTheDocumentedRequests() throws Exception{
		assertJson("{\"FirstName\":\"Іван\",\"MiddleName\":\"Іванович\",\"LastName\":\"Іванов\",\"Phone\":\"380501112233\",\"CounterpartyType\":\"PrivatePerson\",\"CounterpartyProperty\":\"Recipient\"}",
				com.novapost.model.counterparty.CounterpartySaveRequest.builder().firstName("Іван").middleName("Іванович").lastName("Іванов")
						.phone("380501112233").counterpartyType("PrivatePerson").counterpartyProperty("Recipient").build());
		assertJson("{\"ContactSenderRef\":\"c\",\"PreferredDeliveryDate\":\"26.11.2026\",\"PlanedWeight\":\"5\",\"TimeInterval\":\"i\",\"CounterpartySender\":\"s\",\"AddressSenderRef\":\"a\"}",
				com.novapost.model.courier.CourierCallCreateRequest.builder().contactSenderRef("c").preferredDeliveryDate(java.time.LocalDate.parse("2026-11-26")).planedWeight(new BigDecimal("5"))
						.timeInterval("i").counterpartySender("s").addressSenderRef("a").build());
		// the constant OrderType of each additional-service scenario is preset
		assertEquals("orderRedirecting", com.novapost.model.service.RedirectingOrderRequest.builder().intDocNumber("1").build().orderType());
		assertEquals("orderCargoReturn", com.novapost.model.service.ReturnOrderRequest.builder().intDocNumber("1").build().orderType());
		assertEquals("orderChangeEW", com.novapost.model.service.ChangeEwRequest.builder().intDocNumber("1").build().orderType());
		assertJson("{\"DocumentRefs\":[\"a\",\"b\"],\"Ref\":\"r\"}",
				com.novapost.model.scansheet.ScanSheetInsertRequest.builder().documentRefs(List.of("a", "b")).ref("r").build());
	}
}
