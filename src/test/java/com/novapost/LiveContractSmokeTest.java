package com.novapost;

import java.math.BigDecimal;

import com.fasterxml.jackson.databind.JsonNode;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpModelStyle;
import com.novapost.model.DocumentDeliveryDateRequest;
import com.novapost.model.DocumentPriceRequest;
import com.novapost.model.InternetDocumentListFilter;
import com.novapost.testsupport.ReadOnlyClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Read-only checks against the live API; they run only when {@code NOVAPOST_API_KEY} is set:
 * <pre>
 *   $env:NOVAPOST_API_KEY = "..."; mvn test -Dtest=LiveContractSmokeTest
 * </pre>
 * Nova Poshta has no sandbox for API 2.0 (the only endpoint is production), so these calls go to the account that owns the key. Use a
 * key created for the purpose (business cabinet - Settings - Security - Create key; several keys per account are allowed and a key can be
 * deleted at any time) and delete it afterwards. The client is a {@link ReadOnlyClient}: a call whose name does not start with
 * {@code get}, {@code search} or {@code check} is refused before it is built, so a test can neither create, change nor delete anything
 * (pinned by {@code ReadOnlyClientTest}).
 *
 * <p>The tests settle the points that the documentation leaves open (B1-B5):
 * <ul>
 *   <li>B1 - are the {@code *General} model names and the legacy ones both accepted? (run for both {@link NpModelStyle}s)</li>
 *   <li>B2 - does {@code getDocumentDeliveryDate} take {@code dd.MM.yyyy}, {@code yyyy-MM-dd HH:mm:ss}, or both?</li>
 *   <li>B3 - which date format does {@code getDocumentList} return?</li>
 *   <li>B5 - does {@code getDocumentList} accept date-only and date-time bounds?</li>
 * </ul>
 * Once both styles pass, switch the default of {@code NovaPostClient} to {@link NpModelStyle#DOCUMENTED}.
 */
@EnabledIfEnvironmentVariable(named = "NOVAPOST_API_KEY", matches = ".+")
class LiveContractSmokeTest{

	private static String key;

	@BeforeAll
	static void readKey(){
		key = System.getenv("NOVAPOST_API_KEY").trim();
	}

	private static NovaPostClient client(NpModelStyle style){
		return ReadOnlyClient.of(key, style);
	}

	private static void assertOk(JsonNode root, String what){
		List<String> errors = new ArrayList<>();
		root.path("errors").forEach(e -> errors.add(e.asText()));
		assertTrue(root.path("success").asBoolean(false) && errors.isEmpty(), what + " failed: " + errors + " " + root.path("errorCodes"));
	}

	@ParameterizedTest
	@EnumSource(NpModelStyle.class)
	void directoriesAnswerUnderBothModelStyles(NpModelStyle style) throws Exception{
		NovaPostClient c = client(style);

		assertOk(c.common().getCargoTypes().rawRoot(), "getCargoTypes");
		assertOk(c.common().getServiceTypes().rawRoot(), "getServiceTypes");
		assertOk(c.common().getMessageCodeText().rawRoot(), "getMessageCodeText");
		assertOk(c.address().getAreas().rawRoot(), "getAreas");
		assertOk(c.address().getWarehouseTypes().rawRoot(), "getWarehouseTypes");
		assertOk(c.address().getCities(com.novapost.model.address.CityFilter.builder().page(1).limit(1).build()).rawRoot(), "getCities");
		assertOk(c.address().getSettlements(com.novapost.model.SettlementFilter.byString("Київ", 1, 1)).rawRoot(), "getSettlements");
		assertOk(c.address().searchSettlements(com.novapost.model.SearchSettlementFilter.of("Київ", 1, 1)).rawRoot(), "searchSettlements");
		assertOk(c.counterparty().getCounterparties(com.novapost.model.counterparty.CounterpartyListRequest.builder()
				.counterpartyProperty("Sender").page(1).build()).rawRoot(), "getCounterparties");
		assertOk(c.scanSheet().getScanSheetList().rawRoot(), "getScanSheetList");
	}

	@ParameterizedTest
	@EnumSource(NpModelStyle.class)
	void waybillMethodsAnswerUnderBothModelStyles(NpModelStyle style) throws Exception{
		NovaPostClient c = client(style);
		LocalDateTime to = LocalDateTime.now();
		LocalDateTime from = to.minusDays(3);

		assertOk(c.internetDocument().getDocumentList(InternetDocumentListFilter.byDateRange(from, to, 1, 100)).rawRoot(), "getDocumentList paged");
		assertOk(c.internetDocument().getDocumentList(InternetDocumentListFilter.list(from, to)).rawRoot(), "getDocumentList capped list");
		assertOk(c.internetDocument().getDocumentList(InternetDocumentListFilter.byDay(to.toLocalDate(), 1, 100)).rawRoot(), "getDocumentList with time bounds (B5)");
		assertOk(c.getTrackingRoot("20400048799000", ""), "tracking of an unknown number");
	}

	@ParameterizedTest
	@EnumSource(NpModelStyle.class)
	void deliveryDateAndPriceTakeTheDocumentedParameters(NpModelStyle style) throws Exception{
		NovaPostClient c = client(style);
		// Kyiv -> Lviv city Refs from the public directory
		String kyiv = "8d5a980d-391c-11dd-90d9-001a92567626";
		String lviv = "db5c88f5-391c-11dd-90d9-001a92567626";

		var dateOnly = c.internetDocument().getDocumentDeliveryDate(new DocumentDeliveryDateRequest(LocalDateTime.now().toLocalDate(), "WarehouseWarehouse", kyiv, lviv)).rawRoot();
		var dateTime = c.internetDocument().getDocumentDeliveryDate(DocumentDeliveryDateRequest.ofDateTime(LocalDateTime.now(), "WarehouseWarehouse", kyiv, lviv)).rawRoot();
		System.out.println("[" + style + "] delivery date, dd.MM.yyyy -> success=" + dateOnly.path("success").asBoolean() + " " + dateOnly.path("errors"));
		System.out.println("[" + style + "] delivery date, yyyy-MM-dd HH:mm:ss -> success=" + dateTime.path("success").asBoolean() + " " + dateTime.path("errors"));
		assertTrue(dateOnly.path("success").asBoolean() || dateTime.path("success").asBoolean(), "neither date format was accepted");

		assertOk(c.internetDocument().getDocumentPrice(new DocumentPriceRequest(kyiv, lviv, BigDecimal.ONE, "WarehouseWarehouse", new BigDecimal("500"), "Cargo", 1)).rawRoot(), "getDocumentPrice");
	}

	@ParameterizedTest
	@EnumSource(NpModelStyle.class)
	void parcelIsAKnownCargoType(NpModelStyle style) throws Exception{
		// calculatePrice() and CargoDetails default to "Parcel"; the documentation lists only Cargo/Documents/TiresWheels/Pallet for the price
		var types = client(style).common().getCargoTypes().list();
		System.out.println("[" + style + "] cargo types: " + types);
		assertTrue(types.stream().anyMatch(t -> "Parcel".equals(t.ref())), "Parcel is not in getCargoTypes: " + types);
	}
}
