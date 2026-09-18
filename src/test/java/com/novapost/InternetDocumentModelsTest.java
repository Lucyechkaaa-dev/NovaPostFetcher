package com.novapost;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class InternetDocumentModelsTest {

    private final ObjectMapper mapper = NovaPostClient.createDefaultMapper();

    @Test
    void testInternetDocumentSaveRequestSerialization() throws Exception {
        InternetDocumentSaveRequest saveReq = new InternetDocumentSaveRequest(
            null, "Sender", "Cash", LocalDate.of(2026, 4, 20), "Parcel", null, "1.5",
            "WarehouseWarehouse", "1", "Electronics", "500",
            "city-sender-ref", "sender-ref", "sender-addr-ref", "contact-sender-ref", "+380501112233",
            "city-recipient-ref", "recipient-ref", "recipient-addr-ref", "contact-recipient-ref", "+380509998877",
            null, null
        );

        NpRequest<InternetDocumentSaveRequest> request = new NpRequest<>("test-api-key", "InternetDocument", "save", saveReq);
        String json = mapper.writeValueAsString(request);

        assertTrue(json.contains("\"modelName\":\"InternetDocument\""));
        assertTrue(json.contains("\"calledMethod\":\"save\""));
        assertTrue(json.contains("\"PayerType\":\"Sender\""));
        assertTrue(json.contains("\"PaymentMethod\":\"Cash\""));
        assertTrue(json.contains("\"DateTime\":\"20.04.2026\""));
        assertTrue(json.contains("\"Weight\":\"1.5\""));
    }

    @Test
    void testInternetDocumentListFilterSerialization() throws Exception {
        InternetDocumentListFilter filter = InternetDocumentListFilter.byDateRange(
            LocalDate.of(2026, 4, 1),
            LocalDate.of(2026, 4, 30),
            1, 50
        );

        String json = mapper.writeValueAsString(filter);
        assertTrue(json.contains("\"DateTimeFrom\":\"01.04.2026\""));
        assertTrue(json.contains("\"DateTimeTo\":\"30.04.2026\""));
    }

    @Test
    void testInternetDocumentResponseDeserialization() throws Exception {
        String json = """
        {
            "success": true,
            "data": [
                {
                    "Ref": "1b08ce61-e0e9-11ea-80d8-0025b537cf48",
                    "CostOnSite": "55",
                    "EstimatedDeliveryDate": "22.04.2026",
                    "IntDocNumber": "20450201234567",
                    "TypeDocument": "InternetDocument"
                }
            ],
            "errors": [],
            "warnings": [],
            "info": []
        }
        """;

        JavaType type = mapper.getTypeFactory().constructParametricType(NpResponse.class, InternetDocumentResponse.class);
        NpResponse<InternetDocumentResponse> response = mapper.readValue(json, type);

        assertTrue(response.success());
        assertEquals(1, response.data().size());
        assertEquals("20450201234567", response.data().getFirst().intDocNumber());
        assertEquals("55", response.data().getFirst().costOnSite());
        assertEquals(LocalDate.of(2026, 4, 22), response.data().getFirst().estimatedDeliveryDate());
    }

    @Test
    void testInternetDocumentListItemDeserialization() throws Exception {
        String json = """
        {
            "success": true,
            "data": [
                {
                    "Ref": "ref-1",
                    "IntDocNumber": "20450201234567",
                    "DateTime": "2026-04-20 14:30:00",
                    "Cost": "500",
                    "EstimatedDeliveryDate": "2026-04-22 18:00:00"
                }
            ],
            "errors": [],
            "warnings": []
        }
        """;

        JavaType type = mapper.getTypeFactory().constructParametricType(NpResponse.class, InternetDocumentListItem.class);
        NpResponse<InternetDocumentListItem> response = mapper.readValue(json, type);

        assertTrue(response.success());
        assertEquals(1, response.data().size());
        assertEquals(LocalDateTime.of(2026, 4, 20, 14, 30, 0), response.data().getFirst().dateTime());
        assertEquals(LocalDateTime.of(2026, 4, 22, 18, 0, 0), response.data().getFirst().estimatedDeliveryDate());
    }

    @Test
    void testDocumentPriceResponseDeserialization() throws Exception {
        String json = """
        {
            "success": true,
            "data": [
                {
                    "Cost": 75.0,
                    "AssessedCost": 15.0,
                    "CostRedelivery": 0.0,
                    "CostPack": 0.0
                }
            ],
            "errors": [],
            "warnings": []
        }
        """;

        JavaType type = mapper.getTypeFactory().constructParametricType(NpResponse.class, DocumentPriceResponse.class);
        NpResponse<DocumentPriceResponse> response = mapper.readValue(json, type);

        assertTrue(response.success());
        assertEquals(1, response.data().size());
        assertEquals(75.0, response.data().getFirst().cost());
    }

    @Test
    void testDocumentDeliveryDateResponseDeserialization() throws Exception {
        String json = """
        {
            "success": true,
            "data": [
                {
                    "DeliveryDate": {
                        "date": "2026-04-22 00:00:00.000000",
                        "timezone_type": 3,
                        "timezone": "Europe/Kiev"
                    }
                }
            ],
            "errors": [],
            "warnings": []
        }
        """;

        JavaType type = mapper.getTypeFactory().constructParametricType(NpResponse.class, DocumentDeliveryDateResponse.class);
        NpResponse<DocumentDeliveryDateResponse> response = mapper.readValue(json, type);

        assertTrue(response.success());
        assertEquals("2026-04-22 00:00:00.000000", response.data().getFirst().getFormattedDate());
        assertEquals(LocalDateTime.of(2026, 4, 22, 0, 0, 0), response.data().getFirst().getDeliveryDateTime());
    }

    @Test
    void testInternetDocumentDeleteRequestSerialization() throws Exception {
        InternetDocumentDeleteRequest deleteReq = InternetDocumentDeleteRequest.of("doc-ref-123");
        NpRequest<InternetDocumentDeleteRequest> request = new NpRequest<>("test-api-key", "InternetDocument", "delete", deleteReq);
        String json = mapper.writeValueAsString(request);

        assertTrue(json.contains("\"calledMethod\":\"delete\""));
        assertTrue(json.contains("\"DocumentRefs\":[\"doc-ref-123\"]"));
    }
}
