package com.novapost.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.controller.NovaPostController;
import com.novapost.model.CargoDetails;
import com.novapost.model.InternetDocumentResponse;
import com.novapost.model.ParticipantRouting;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WaybillLifecycleServiceTest{

	private final ObjectMapper mapper = NovaPostClient.createDefaultMapper();

	@Test
	void testCollaborativeTwoStageCreationAndUpdate(){
		List<String> capturedBodies = new ArrayList<>();

		WaybillFetcherServiceTest.MockScriptedHttpClient mockHttp = new WaybillFetcherServiceTest.MockScriptedHttpClient((req, reqBody) -> {
			capturedBodies.add(reqBody);

			if(reqBody.contains("\"calledMethod\":\"save\"")){
				return """
						{
						  "success": true,
						  "data": [
						    {
						      "Ref": "draft-uuid-111",
						      "CostOnSite": "85.00",
						      "EstimatedDeliveryDate": "15.10.2026",
						      "IntDocNumber": "20400554828463",
						      "TypeDocument": "InternetDocument"
						    }
						  ],
						  "errors": [],
						  "warnings": []
						}
						""";
			}
			else if(reqBody.contains("\"calledMethod\":\"getDocumentList\"")){
				return """
						{
						  "success": true,
						  "data": [
						    {
						      "Ref": "draft-uuid-111",
						      "IntDocNumber": "20400554828463",
						      "DateTime": "2026-10-13 14:00:00",
						      "PayerType": "Sender",
						      "PaymentMethod": "NonCash",
						      "CargoType": "Parcel",
						      "ServiceType": "WarehouseWarehouse",
						      "SeatsAmount": "1",
						      "Description": "Goods",
						      "Cost": "300",
						      "Weight": "1.0",
						      "CitySender": "city-senderRef-uuid",
						      "Sender": "senderRef-uuid",
						      "SenderAddress": "senderRef-addr-uuid",
						      "ContactSender": "contact-senderRef-uuid",
						      "SendersPhone": "+380501112233",
						      "CityRecipient": "city-recipientRef-uuid",
						      "Recipient": "recipientRef-uuid",
						      "RecipientAddress": "recipientRef-addr-uuid",
						      "ContactRecipient": "contact-recipientRef-uuid",
						      "RecipientsPhone": "+380509998877"
						    }
						  ],
						  "errors": [],
						  "warnings": []
						}
						""";
			}
			else if(reqBody.contains("\"calledMethod\":\"update\"")){
				return """
						{
						  "success": true,
						  "data": [
						    {
						      "Ref": "draft-uuid-111",
						      "CostOnSite": "150.00",
						      "EstimatedDeliveryDate": "15.10.2026",
						      "IntDocNumber": "20400554828463",
						      "TypeDocument": "InternetDocument"
						    }
						  ],
						  "errors": [],
						  "warnings": []
						}
						""";
			}
			else if(reqBody.contains("\"calledMethod\":\"delete\"")){
				return """
						{
						  "success": true,
						  "data": [
						    {
						      "Ref": "draft-uuid-111"
						    }
						  ],
						  "errors": [],
						  "warnings": []
						}
						""";
			}

			return "{\"success\":true,\"data\":[],\"errors\":[]}";
		});

		NovaPostClient client = new NovaPostClient("test-key", NovaPostClient.DEFAULT_API_URL, mockHttp, mapper);
		NovaPostController controller = new NovaPostController(client);
		WaybillLifecycleService service = new WaybillLifecycleService(controller);

		// Stage 1: Coworker A creates parcel draft with participant routing
		ParticipantRouting routing = ParticipantRouting.builder()
				.citySenderRef("city-senderRef-uuid")
				.senderRef("senderRef-uuid")
				.senderAddressRef("senderRef-addr-uuid")
				.contactSenderRef("contact-senderRef-uuid")
				.sendersPhone("+380501112233")
				.cityRecipientRef("city-recipientRef-uuid")
				.recipientRef("recipientRef-uuid")
				.recipientAddressRef("recipientRef-addr-uuid")
				.contactRecipientRef("contact-recipientRef-uuid")
				.recipientsPhone("+380509998877")
				.build();

		InternetDocumentResponse draftResponse = service.createDraft(routing);

		assertNotNull(draftResponse);
		assertEquals("20400554828463", draftResponse.intDocNumber());
		assertEquals("draft-uuid-111", draftResponse.ref());

		// Verify that initial save had placeholder cargo
		String saveBody = capturedBodies.get(0);
		assertTrue(saveBody.contains("\"calledMethod\":\"save\""));
		assertTrue(saveBody.contains("\"city-senderRef-uuid\""));
		assertTrue(saveBody.contains("\"recipientRef-uuid\""));
		assertTrue(saveBody.contains("\"Weight\":\"1.0\""));

		// Stage 2: Coworker B at the packing station measures weight and box dimensions, then updates parcel
		CargoDetails physicalCargo = CargoDetails.ofBox("Auto parts & filters", 5.5, 1800, 30, 40, 20);
		InternetDocumentResponse updateResponse = service.updateCargo("20400554828463", physicalCargo);

		assertNotNull(updateResponse);
		assertEquals("draft-uuid-111", updateResponse.ref());

		// Verify that update preserved all original routing AND added physical box dimensions
		String updateBody = capturedBodies.get(capturedBodies.size() - 1);
		assertTrue(updateBody.contains("\"calledMethod\":\"update\""));
		assertTrue(updateBody.contains("\"city-senderRef-uuid\""));
		assertTrue(updateBody.contains("\"senderRef-addr-uuid\""));
		assertTrue(updateBody.contains("\"recipientRef-addr-uuid\""));
		assertTrue(updateBody.contains("\"Weight\":\"5.5\""));
		assertTrue(updateBody.contains("\"Description\":\"Auto parts & filters\""));
		assertTrue(updateBody.contains("\"Cost\":\"1800\""));
		assertTrue(updateBody.contains("\"OptionsSeat\""));

		// Stage 3: Delete waybill by TTN
		boolean deleted = service.deleteByTtn("20400554828463");
		assertTrue(deleted);
	}
}
