package com.novapost;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.model.*;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class NovaPostModelsTest {

    private final ObjectMapper mapper = NovaPostClient.createDefaultMapper();

    @Test
    void testNpRequestSerialization() throws Exception {
        SettlementFilter filter = SettlementFilter.byString("Kyiv", 1, 10);
        NpRequest<SettlementFilter> request = new NpRequest<>("test-api-key", "AddressGeneral", "getSettlements", filter);

        String json = mapper.writeValueAsString(request);
        assertTrue(json.contains("\"apiKey\":\"test-api-key\""));
        assertTrue(json.contains("\"modelName\":\"AddressGeneral\""));
        assertTrue(json.contains("\"calledMethod\":\"getSettlements\""));
        assertTrue(json.contains("\"FindByString\":\"Kyiv\""));
    }

    @Test
    void testWarehouseResponseDeserialization() throws Exception {
        String json = """
        {
            "success": true,
            "data": [
                {
                    "SiteKey": "1234",
                    "Description": "Branch 1",
                    "ShortAddress": "Khreshchatyk 1",
                    "Number": "1",
                    "CityRef": "8d5a980d-391c-11dd-90d9-001a92567626",
                    "CityDescription": "Kyiv",
                    "Ref": "169227f2-e1c2-11e3-8c4a-0050568002cf"
                }
            ],
            "errors": [],
            "warnings": [],
            "info": []
        }
        """;

        JavaType type = mapper.getTypeFactory().constructParametricType(NpResponse.class, Warehouse.class);
        NpResponse<Warehouse> response = mapper.readValue(json, type);

        assertTrue(response.success());
        assertFalse(response.hasErrors());
        assertEquals(1, response.data().size());

        Warehouse w = response.data().get(0);
        assertEquals("1", w.number());
        assertEquals("Kyiv", w.cityDescription());
        assertEquals("169227f2-e1c2-11e3-8c4a-0050568002cf", w.ref());
    }

    @Test
    void testSettlementResponseDeserialization() throws Exception {
        String json = """
        {
            "success": true,
            "data": [
                {
                    "Ref": "e718a680-4b33-11e4-ab6d-005056801329",
                    "SettlementType": "місто",
                    "Description": "Київ",
                    "RegionsDescription": "Київська",
                    "AreaDescription": ""
                }
            ],
            "errors": [],
            "warnings": []
        }
        """;

        JavaType type = mapper.getTypeFactory().constructParametricType(NpResponse.class, Settlement.class);
        NpResponse<Settlement> response = mapper.readValue(json, type);

        assertTrue(response.success());
        assertEquals(1, response.data().size());
        assertEquals("Київ", response.data().get(0).description());
    }
}
