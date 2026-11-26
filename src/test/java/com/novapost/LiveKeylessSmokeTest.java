package com.novapost;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpModelStyle;
import com.novapost.model.TrackingStatus;
import com.novapost.testsupport.ReadOnlyClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

/**
 * A live check that needs no API key at all, for people who do not want to use any key of their own:
 * <pre>
 *   $env:NOVAPOST_LIVE_KEYLESS = "1"; mvn test -Dtest=LiveKeylessSmokeTest
 * </pre>
 * Nova Poshta has no sandbox for API 2.0, but the portal says (news of 24.05.2024) that {@code TrackingDocument/getStatusDocuments} also
 * answers without a key; the sender and recipient data are then replaced by the data of the phone number in the request. One request is
 * sent, for the number of the documentation's own example, and only its status and the field names are printed. It exercises the transport (TLS, the JSON envelope, the model-name style) and the shape of
 * {@link TrackingStatus}, and prints the fields the live answer carries beyond the ones the model declares.
 */
@EnabledIfEnvironmentVariable(named = "NOVAPOST_LIVE_KEYLESS", matches = "1|true")
class LiveKeylessSmokeTest{

	/** The number from the documentation's own tracking example; the API rejects a made-up one such as 00000000000000 as "not correct". */
	private static final String NO_SUCH_WAYBILL = "20400048799000";

	@Test
	void trackingAnswersWithoutAnApiKey() throws Exception{
		NovaPostClient client = ReadOnlyClient.of("", NpModelStyle.LEGACY);

		JsonNode root = client.getTrackingRoot(NO_SUCH_WAYBILL, "");

		System.out.println("[keyless] success=" + root.path("success") + " errors=" + root.path("errors") + " warnings=" + root.path("warnings")
				+ " errorCodes=" + root.path("errorCodes") + " data.size=" + root.path("data").size());
		assertTrue(root.path("success").asBoolean(false), "the answer is not a success: " + root);
		assertEquals(1, root.path("data").size(), "one number asked, one status expected");

		JsonNode first = root.path("data").get(0);
		System.out.println("[keyless] Number=" + first.path("Number") + " StatusCode=" + first.path("StatusCode") + " Status=" + first.path("Status")
				+ " fields in the answer=" + first.size());

		// the typed model reads the very answer received above (still one request in total), with a STRICT mapper: a value that cannot be
		// read as its field's type (a date, a decimal, a flag...) fails here instead of becoming null
		ObjectMapper strict = client.getObjectMapper().copy().clearProblemHandlers();
		TrackingStatus status = strict.treeToValue(first, TrackingStatus.class);
		assertEquals(NO_SUCH_WAYBILL, status.number());
		System.out.println("[keyless] typed: DateScan=" + status.dateScan() + " DateCreated=" + status.dateCreated() + " SeatsAmount=" + status.seatsAmount()
				+ " DocumentWeight=" + status.documentWeight() + " AnnouncedPrice=" + status.announcedPrice() + " Redelivery=" + status.redelivery()
				+ " SecurePayment=" + status.securePayment());
		System.out.println("[keyless] fields the live answer carries and TrackingStatus does not declare: " + new TreeSet<>(status.extra().keySet()));
		new TreeSet<>(status.extra().keySet()).forEach(name -> System.out.println("[keyless]   undeclared " + name + " = " + status.extra().get(name)));
	}
}
