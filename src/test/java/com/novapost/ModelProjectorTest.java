package com.novapost;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.novapost.model.Settlement;
import com.novapost.model.Warehouse;
import com.novapost.projection.ModelProjector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ModelProjectorTest {

	private ModelProjector projector;

	@BeforeEach
	void setUp() {
		projector = ModelProjector.getInstance();
	}

	@Test
	void testProjectToMapWithNullValuesInTargetFields() {
		Warehouse warehouse = new Warehouse(
				"12345", "Kyiv Branch 1", "Kiev Branch 1", "Khreshchatyk 1", "Khreshchatyk 1",
				"0800500609", "PostOffice", "wh-ref-111", "1", "city-ref-999",
				"Kyiv", "Kiev", "settlement-ref-777", "Kyiv",
				"Kyivska", "Kyivskyi", "m.", "30.5234", "50.4501",
				"1", "1", "1", "1", "1", "5", "1100", "30", "Working", "2026-01-01",
				"Branch", "Kyiv", "1", "01001", "01001", Map.of(), Map.of(), Map.of()
		);

		Map<String, Object> targetFields = new HashMap<>();
		targetFields.put("ref", null);
		targetFields.put("description", null);
		targetFields.put("Number", null);
		targetFields.put("nonExistentField", null);

		Map<String, Object> projected = projector.projectToMap(warehouse, targetFields);

		assertEquals(4, projected.size());
		assertEquals("wh-ref-111", projected.get("ref"));
		assertEquals("Kyiv Branch 1", projected.get("description"));
		assertEquals("1", projected.get("Number"));
		assertNull(projected.get("nonExistentField"));
	}

	@Test
	void testProjectToMapCaseInsensitive() {
		Settlement settlement = new Settlement(
				"settle-ref-1", "City", "50.4501", "30.5234", "Kyiv",
				"Kiev", "City", "City Ru", "region-1", "Kyivska",
				"Kyivska Ru", "area-1", "Kyivska", "Kyivska Ru",
				"01001", "01002", "8000000000", "1"
		);

		Map<String, Object> targetFields = new HashMap<>();
		targetFields.put("REF", null);
		targetFields.put("Description", null);
		targetFields.put("settlementTypeDescription", null);

		Map<String, Object> projected = projector.projectToMap(settlement, targetFields);

		assertEquals("settle-ref-1", projected.get("REF"));
		assertEquals("Kyiv", projected.get("Description"));
		assertEquals("City", projected.get("settlementTypeDescription"));
	}

	@Test
	void testProjectToMapNullHandling() {
		assertTrue(projector.projectToMap(null, Map.of("ref", "")).isEmpty());
		assertTrue(projector.projectToMap(new Object(), null).isEmpty());
		assertTrue(projector.projectAllToMap(null, Map.of("ref", "")).isEmpty());
		assertTrue(projector.projectAllToMap(List.of(), Map.of("ref", "")).isEmpty());
	}

	@Test
	void testProjectToPojoWithSettersAndTypeConversion() {
		Warehouse warehouse = new Warehouse(
				"987", "Branch 5", "Branch 5 Ru", "Main St 10", "Main St 10 Ru",
				"0800500609", "PostOffice", "wh-ref-222", "42", "city-ref-999",
				"Lviv", "Lvov", "settlement-ref-888", "Lviv",
				"Lvivska", "Lvivskyi", "m.", "24.0311", "49.8429",
				"1", "1", "1", "1", "1", "2", "30", "30", "Working", "2026-01-01",
				"Branch", "Lviv", "1", "79000", "79000", Map.of(), Map.of(), Map.of()
		);

		WarehousePojoDto dto = projector.projectToClass(warehouse, WarehousePojoDto.class);

		assertNotNull(dto);
		assertEquals("wh-ref-222", dto.getRef());
		assertEquals("Branch 5", dto.getDescription());
		assertEquals(42, dto.getNumber());
		assertEquals(987L, dto.getSiteKey());
		assertNull(dto.getExtraUnsetField());
	}

	@Test
	void testProjectToRecordWithAutomaticTypeConversion() {
		Warehouse warehouse = new Warehouse(
				"100", "Dnipro Branch 3", "Dnipro Branch 3 Ru", "Central Ave", "Central Ave Ru",
				"0800500609", "PostOffice", "wh-ref-333", "15", "city-ref-555",
				"Dnipro", "Dnepr", "settlement-ref-333", "Dnipro",
				"Dnipropetrovska", "Dniprovskyi", "m.", "35.0462", "48.4647",
				"1", "1", "1", "1", "1", "4", "1000", "30", "Working", "2026-01-01",
				"Branch", "Dnipro", "1", "49000", "49000", Map.of(), Map.of(), Map.of()
		);

		WarehouseRecordDto dto = projector.projectToClass(warehouse, WarehouseRecordDto.class);

		assertNotNull(dto);
		assertEquals("wh-ref-333", dto.ref());
		assertEquals("Dnipro Branch 3", dto.description());
		assertEquals(15, dto.number());
		assertEquals(100L, dto.siteKey());
		assertNull(dto.missingField());
		assertEquals(0.0, dto.optionalWeightLimit());
	}

	@Test
	void testProjectAllToClassBatch() {
		Settlement s1 = new Settlement(
				"ref-1", "City", "46.4825", "30.7233", "Odesa",
				"Odessa", "City", "City Ru", "region-2", "Odeska",
				"Odeska Ru", "area-2", "Odeska", "Odeska Ru",
				"65000", "65001", "5110100000", "1"
		);
		Settlement s2 = new Settlement(
				"ref-2", "City", "49.9935", "36.2304", "Kharkiv",
				"Kharkov", "City", "City Ru", "region-3", "Kharkivska",
				"Kharkivska Ru", "area-3", "Kharkivska", "Kharkivska Ru",
				"61000", "61001", "6310100000", "1"
		);

		List<SettlementRecordDto> results = projector.projectAllToClass(List.of(s1, s2), SettlementRecordDto.class);

		assertEquals(2, results.size());
		assertEquals("ref-1", results.get(0).ref());
		assertEquals("Odesa", results.get(0).description());
		assertEquals("ref-2", results.get(1).ref());
		assertEquals("Kharkiv", results.get(1).description());
	}

	@Test
	void testProjectAllToMapBatch() {
		Settlement s1 = new Settlement(
				"ref-1", "City", "46.4825", "30.7233", "Odesa",
				"Odessa", "City", "City Ru", "region-2", "Odeska",
				"Odeska Ru", "area-2", "Odeska", "Odeska Ru",
				"65000", "65001", "5110100000", "1"
		);

		Map<String, Object> template = new HashMap<>();
		template.put("ref", null);
		template.put("description", null);

		List<Map<String, Object>> results = projector.projectAllToMap(List.of(s1), template);

		assertEquals(1, results.size());
		assertEquals("ref-1", results.getFirst().get("ref"));
		assertEquals("Odesa", results.getFirst().get("description"));
	}

	@Test
	void testProjectDirectlyFromJsonStringWithUnmappedNovaPostFields() {
		String rawJson = """
				{
				  "Ref": "doc-999",
				  "IntDocNumber": "20450000000999",
				  "Cost": "285.50",
				  "SeatsAmount": "2",
				  "RecipientAddressDescription": "Poltava, Peace Ave 5",
				  "UnmappedFieldAlpha": "AlphaVal",
				  "ExtraInternalFlag": true,
				  "IgnoredBulkField1": "skip",
				  "IgnoredBulkField2": "skip"
				}
				""";

		Map<String, Object> targetFields = new HashMap<>();
		targetFields.put("IntDocNumber", null);
		targetFields.put("SeatsAmount", null);
		targetFields.put("RecipientAddressDescription", null);
		targetFields.put("UnmappedFieldAlpha", null);

		Map<String, Object> projectedMap = projector.projectJsonToMap(rawJson, targetFields);

		assertEquals(4, projectedMap.size());
		assertEquals("20450000000999", projectedMap.get("IntDocNumber"));
		assertEquals("2", projectedMap.get("SeatsAmount"));
		assertEquals("Poltava, Peace Ave 5", projectedMap.get("RecipientAddressDescription"));
		assertEquals("AlphaVal", projectedMap.get("UnmappedFieldAlpha"));
		assertFalse(projectedMap.containsKey("IgnoredBulkField1"));
		assertFalse(projectedMap.containsKey("IgnoredBulkField2"));
	}

	@Test
	void testProjectDirectlyFromJsonToCustomRecordWithUnmappedFields() {
		String rawJson = """
				{
				  "Ref": "doc-888",
				  "IntDocNumber": "20450000000888",
				  "Cost": "340.00",
				  "RecipientAddressDescription": "Chernihiv, Shevchenka 12",
				  "SeatsAmount": "3",
				  "ExtraInternalFlag": true,
				  "UnusedPayload1": "val1",
				  "UnusedPayload2": "val2"
				}
				""";

		UnmappedWaybillDto dto = projector.projectJsonToClass(rawJson, UnmappedWaybillDto.class);

		assertNotNull(dto);
		assertEquals("doc-888", dto.ref());
		assertEquals("20450000000888", dto.intDocNumber());
		assertEquals(340.00, dto.cost(), 0.001);
		assertEquals("Chernihiv, Shevchenka 12", dto.recipientAddressDescription());
		assertEquals(3, dto.seatsAmount());
		assertTrue(dto.extraInternalFlag());
	}

	@Test
	void testProjectJsonArrayBatchDirectly() {
		String jsonArray = """
				[
				  {
				    "Ref": "wh-1",
				    "Description": "Warehouse 1",
				    "CustomWarehouseTag": "TAG-A"
				  },
				  {
				    "Ref": "wh-2",
				    "Description": "Warehouse 2",
				    "CustomWarehouseTag": "TAG-B"
				  }
				]
				""";

		Map<String, Object> fields = new HashMap<>();
		fields.put("Ref", null);
		fields.put("CustomWarehouseTag", null);

		List<Map<String, Object>> mapResults = projector.projectJsonArrayToMap(jsonArray, fields);
		assertEquals(2, mapResults.size());
		assertEquals("wh-1", mapResults.get(0).get("Ref"));
		assertEquals("TAG-A", mapResults.get(0).get("CustomWarehouseTag"));
		assertEquals("wh-2", mapResults.get(1).get("Ref"));
		assertEquals("TAG-B", mapResults.get(1).get("CustomWarehouseTag"));

		List<CustomTagDto> dtoList = projector.projectJsonArrayToClass(jsonArray, CustomTagDto.class);
		assertEquals(2, dtoList.size());
		assertEquals("wh-1", dtoList.get(0).ref());
		assertEquals("TAG-A", dtoList.get(0).customWarehouseTag());
		assertEquals("wh-2", dtoList.get(1).ref());
		assertEquals("TAG-B", dtoList.get(1).customWarehouseTag());
	}

	public static class WarehousePojoDto {
		private String ref;
		private String description;
		private Integer number;
		private Long siteKey;
		private String extraUnsetField;

		public String getRef() { return ref; }
		public void setRef(String ref) { this.ref = ref; }

		public String getDescription() { return description; }
		public void setDescription(String description) { this.description = description; }

		public Integer getNumber() { return number; }
		public void setNumber(Integer number) { this.number = number; }

		public Long getSiteKey() { return siteKey; }
		public void setSiteKey(Long siteKey) { this.siteKey = siteKey; }

		public String getExtraUnsetField() { return extraUnsetField; }
		public void setExtraUnsetField(String extraUnsetField) { this.extraUnsetField = extraUnsetField; }
	}

	public record WarehouseRecordDto(
			@JsonProperty("Ref") String ref,
			@JsonProperty("Description") String description,
			@JsonProperty("Number") int number,
			@JsonProperty("SiteKey") long siteKey,
			String missingField,
			double optionalWeightLimit
	) {}

	public record SettlementRecordDto(
			String ref,
			String description
	) {}

	public record UnmappedWaybillDto(
			String ref,
			String intDocNumber,
			double cost,
			String recipientAddressDescription,
			int seatsAmount,
			boolean extraInternalFlag
	) {}

	public record CustomTagDto(
			String ref,
			String customWarehouseTag
	) {}
}
