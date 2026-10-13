package com.novapost;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.controller.NovaPostController;
import com.novapost.model.InternetDocumentListItem;
import com.novapost.model.InternetDocumentResponse;
import com.novapost.model.InternetDocumentSaveRequest;
import com.novapost.model.NpRequest;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration and preparation test class for fetching TTN 20400554828463
 * and preparing/executing the InternetDocument 'update' operation with cargo details.
 */
public class TtnFetchAndUpdateTest{

	private static final String API_KEY = "YOUR_API_KEY_HERE";

	/**
	 * Target TTN to retrieve and update:
	 */
	public static final String TARGET_TTN = "";

	/**
	 * If you already know the document Ref (UUID), you can specify it here,
	 * or let the test discover it automatically from getWaybills().
	 */
	public static final String DOCUMENT_REF = null;

	private NovaPostClient client;
	private NovaPostController controller;
	private final ObjectMapper mapper = NovaPostClient.createDefaultMapper();

	@BeforeEach
	void setUp(){
		String effectiveKey = getEffectiveApiKey();
		if(effectiveKey != null){
			this.client = new NovaPostClient(effectiveKey);
			this.controller = new NovaPostController(client);
		}
	}

	private static String getEffectiveApiKey(){
		if(API_KEY != null && !API_KEY.isBlank() && !API_KEY.equals("YOUR_API_KEY_HERE")){
			return API_KEY.trim();
		}
		String sysProp = System.getProperty("novapost.api.key");
		if(sysProp != null && !sysProp.isBlank()){
			return sysProp.trim();
		}
		String envKey = System.getenv("NOVAPOST_API_KEY");
		if(envKey != null && !envKey.isBlank()){
			return envKey.trim();
		}
		return null;
	}

	private static boolean isApiKeyProvided(){
		return getEffectiveApiKey() != null;
	}

	/**
	 * Test 1: Retrieve shipment details and status for TTN 20400554828463 via TrackingDocument API.
	 */
	@Test
	@DisplayName("1. Retrieve TTN 20400554828463 tracking and shipment details")
	void testRetrieveTtnTracking() throws Exception{
		Assumptions.assumeTrue(isApiKeyProvided(),
				"Skipping live TTN retrieval: Please write your API key into the API_KEY constant field.");

		System.out.println("--- 1. Fetching tracking details for TTN: " + TARGET_TTN + " ---");

		JsonNode trackingData = client.getTracking(TARGET_TTN);
		assertNotNull(trackingData, "Tracking response data should not be null");
		assertTrue(trackingData.isArray(), "Tracking data should be a JSON array");
		assertFalse(trackingData.isEmpty(), "Tracking data should contain at least one document");

		JsonNode doc = trackingData.get(0);
		String number = doc.path("Number").asText();
		String statusCode = doc.path("StatusCode").asText();
		String status = doc.path("Status").asText();
		String dateCreated = doc.path("DateCreated").asText();
		String cargoType = doc.path("CargoType").asText();
		String documentCost = doc.path("DocumentCost").asText();
		double documentWeight = doc.path("DocumentWeight").asDouble();
		String citySender = doc.path("CitySender").asText();
		String cityRecipient = doc.path("CityRecipient").asText();
		String warehouseRecipient = doc.path("WarehouseRecipient").asText();

		System.out.println("TTN:                  " + number);
		System.out.println("Status Code:          " + statusCode);
		System.out.println("Status:               " + status);
		System.out.println("Date Created:         " + dateCreated);
		System.out.println("Cargo Type:           " + cargoType);
		System.out.println("Document Cost:        " + documentCost + " UAH");
		System.out.println("Document Weight:      " + documentWeight + " kg");
		System.out.println("Route:                " + citySender + " -> " + cityRecipient);
		System.out.println("Recipient Warehouse:  " + warehouseRecipient);

		assertEquals(TARGET_TTN, number, "Returned number must match target TTN");
		assertFalse(status.isBlank(), "Status should not be blank");
	}

	/**
	 * Test 2: Locate TTN 20400554828463 in account waybill list to retrieve its document UUID Ref.
	 * Required when performing the 'update' operation (update requires the document Ref).
	 */
	@Test
	@DisplayName("2. Locate TTN in account waybills to retrieve document UUID Ref")
	void testLocateTtnInAccountWaybills(){
		Assumptions.assumeTrue(isApiKeyProvided(),
				"Skipping account waybills lookup: Please specify your API_KEY in the class constant field.");

		System.out.println("--- 2. Searching for TTN " + TARGET_TTN + " in account waybills ---");
		LocalDate from = LocalDate.now().minusDays(30);
		LocalDate to = LocalDate.now();

		Optional<InternetDocumentListItem> found = controller.findWaybillByNumber(TARGET_TTN, from, to);
		if(found.isPresent()){
			InternetDocumentListItem item = found.get();
			System.out.println("Found waybill under account!");
			System.out.println(" - Document Ref (UUID): " + item.ref());
			System.out.println(" - TTN:                 " + item.intDocNumber());
			System.out.println(" - Created Date:        " + item.dateTime());
			System.out.println(" - Sender:              " + item.senderDescription());
			System.out.println(" - Recipient:           " + item.recipientDescription());
			assertNotNull(item.ref());
		}
		else{
			System.out.println("TTN " + TARGET_TTN + " was not found in account waybills for range " + from + " to " + to + ".");
			System.out.println("Note: If created under another sender/sub-account, specify DOCUMENT_REF manually.");
		}
	}

	/**
	 * Test 3: Prepare and validate the update request with updated cargo details.
	 * Tests builder construction, cargo parameters, seat options, and JSON serialization.
	 */
	@Test
	@DisplayName("3. Prepare update request with cargo details & verify serialization")
	void testPrepareUpdateRequestWithCargoDetails() throws Exception{
		System.out.println("--- 3. Preparing update request with custom cargo details ---");

		String sampleRef = (DOCUMENT_REF != null && !DOCUMENT_REF.isBlank())
				? DOCUMENT_REF
				: "00000000-0000-0000-0000-000000000000";

		InternetDocumentSaveRequest updateRequest = buildCargoUpdateRequest(sampleRef);

		assertNotNull(updateRequest);
		assertEquals(sampleRef, updateRequest.ref());
		assertEquals("Parcel", updateRequest.cargoType());
		assertEquals("5.0", updateRequest.weight());
		assertEquals("1", updateRequest.seatsAmount());
		assertNotNull(updateRequest.optionsSeat());
		assertEquals(1, updateRequest.optionsSeat().size());

		// Verify JSON serialization conforms to Nova Post API specification
		NpRequest<InternetDocumentSaveRequest> requestWrapper =
				new NpRequest<>(isApiKeyProvided() ? getEffectiveApiKey() : "sample-key", "InternetDocument", "update", updateRequest);
		String compactJson = mapper.writeValueAsString(requestWrapper);
		String prettyJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(requestWrapper);

		System.out.println("Serialized API update request JSON payload:\n" + prettyJson);

		assertTrue(compactJson.contains("\"calledMethod\":\"update\""));
		assertTrue(compactJson.contains("\"CargoType\":\"Parcel\""));
		assertTrue(compactJson.contains("\"Weight\":\"5.0\""));
		assertTrue(compactJson.contains("\"Description\":\"Автозапчастини та аксесуари\""));
		assertTrue(compactJson.contains("\"OptionsSeat\""));
		assertTrue(compactJson.contains("\"volumetricWidth\":\"30\""));
		assertTrue(compactJson.contains("\"volumetricLength\":\"40\""));
		assertTrue(compactJson.contains("\"volumetricHeight\":\"20\""));
	}

	/**
	 * Test 4: Live update execution against Nova Post API.
	 * Executes controller.updateWaybill(...) once your API_KEY is set.
	 */
	@Test
	@DisplayName("4. Execute live waybill update with cargo details")
	void testLiveUpdateWaybillWithCargoDetails(){
		Assumptions.assumeTrue(isApiKeyProvided(),
				"Skipping live update: Please write your API key into API_KEY constant field.");

		LocalDate from = LocalDate.now().minusDays(30);
		LocalDate to = LocalDate.now();

		System.out.println("Executing merged live update for TTN: " + TARGET_TTN);
		InternetDocumentResponse response = controller.updateWaybillCargoByNumber(TARGET_TTN, from, to, builder -> {
			InternetDocumentSaveRequest.OptionSeat cargoSeat = InternetDocumentSaveRequest.OptionSeat.ofDimensions(
					"30",  // width (cm)
					"40",  // length (cm)
					"20",  // height (cm)
					"5.0"  // actual weight (kg)
			);

			builder
					.cargoType("Parcel")
					.description("Автозапчастини та аксесуари")
					.weight("5.0")
					.volumeGeneral("0.024")
					.seatsAmount("1")
					.cost("1500")
					.optionsSeat(List.of(cargoSeat));
		});

		assertNotNull(response);
		System.out.println("Waybill successfully updated!");
		System.out.println(" - Document Ref:            " + response.ref());
		System.out.println(" - TTN Number:             " + response.intDocNumber());
		System.out.println(" - Cost on site:           " + response.costOnSite());
		System.out.println(" - Estimated delivery date: " + response.estimatedDeliveryDate());
	}

	/**
	 * Helper method to configure cargo details for the update operation.
	 * Edit the fields below to customize your cargo dimensions, weight, and description.
	 */
	public static InternetDocumentSaveRequest buildCargoUpdateRequest(String documentRef){
		InternetDocumentSaveRequest.OptionSeat cargoSeat = InternetDocumentSaveRequest.OptionSeat.ofDimensions(
				"30",  // width (cm)
				"40",  // length (cm)
				"20",  // height (cm)
				"5.0"  // actual weight (kg)
		);

		return InternetDocumentSaveRequest.builder()
				.ref(documentRef)
				.cargoType("Parcel")
				.description("Автозапчастини та аксесуари")
				.weight("5.0")
				.volumeGeneral("0.024")
				.seatsAmount("1")
				.cost("1500")
				.optionsSeat(List.of(cargoSeat))
				.payerType("Sender")
				.paymentMethod("NonCash")
				.dateTime(LocalDate.now())
				.serviceType("DoorsWarehouse")
				.build();
	}
}
