package com.novapost;

import com.novapost.model.InternetDocumentSaveRequest;
import com.novapost.model.TrackingStatus;
import com.novapost.model.enums.*;
import com.novapost.client.NovaPostClient;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NpEnumsTest{

	@Test
	void valuesAreTheDocumentedSpellings(){
		assertEquals(List.of("Sender", "Recipient", "ThirdPerson"), List.of(PayerType.SENDER.value(), PayerType.RECIPIENT.value(), PayerType.THIRD_PERSON.value()));
		assertEquals("NonCash", PaymentMethod.NON_CASH.value());
		assertEquals("WarehouseDoors", ServiceType.WAREHOUSE_DOORS.value());
		assertEquals("TiresWheels", CargoType.TIRES_WHEELS.value());
		assertEquals("PrivatePerson", CounterpartyType.PRIVATE_PERSON.value());
		assertEquals("orderChangeEW", OrderType.CHANGE_EW.value());
	}

	@Test
	void parsingIsCaseInsensitiveAndNeverThrows(){
		assertEquals(PayerType.THIRD_PERSON, PayerType.of(" thirdperson "));
		assertEquals(ServiceType.DOORS_DOORS, ServiceType.of("DoorsDoors"));
		assertEquals(CargoType.UNKNOWN, CargoType.of("SomethingNew"));
		assertEquals(CargoType.UNKNOWN, CargoType.of(null));
		assertEquals(PaymentMethod.UNKNOWN, PaymentMethod.of(""));
	}

	@Test
	void everyDocumentedTrackingStatusCodeIsKnown(){
		List<String> documented = List.of("1", "2", "3", "4", "41", "5", "6", "7", "8", "9", "10", "11", "12", "15", "101", "102", "103",
				"104", "105", "106", "107", "111", "112", "124");

		for(String code : documented){
			assertNotEquals(TrackingStatusCode.UNKNOWN, TrackingStatusCode.of(code), "status " + code);
			assertEquals(code, TrackingStatusCode.of(code).value());
		}
		assertEquals(documented.size() + 1, TrackingStatusCode.values().length, "24 documented codes + UNKNOWN");
		assertEquals(TrackingStatusCode.UNKNOWN, TrackingStatusCode.of("999"));
		assertTrue(TrackingStatusCode.of("9").isDelivered());
		assertTrue(TrackingStatusCode.of("11").isDelivered());
		assertFalse(TrackingStatusCode.of("7").isDelivered());
	}

	@Test
	void trackingStatusExposesTheCodeAsAnEnum() throws Exception{
		TrackingStatus status = NovaPostClient.createDefaultMapper().readValue("{\"Number\":\"1\",\"StatusCode\":\"102\"}", TrackingStatus.class);

		assertEquals(TrackingStatusCode.REFUSED_RETURN_ORDERED, status.statusCodeValue());
	}

	@Test
	void builderAcceptsEnums(){
		InternetDocumentSaveRequest request = InternetDocumentSaveRequest.builder().payerType(PayerType.RECIPIENT)
				.paymentMethod(PaymentMethod.CASH).serviceType(ServiceType.DOORS_WAREHOUSE).cargoType(CargoType.PARCEL)
				.recipientType(CounterpartyType.ORGANIZATION).build();

		assertEquals("Recipient", request.payerType());
		assertEquals("Cash", request.paymentMethod());
		assertEquals("DoorsWarehouse", request.serviceType());
		assertEquals("Parcel", request.cargoType());
		assertEquals("Organization", request.recipientType());
	}
}
