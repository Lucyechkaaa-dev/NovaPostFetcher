package com.novapost.client.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.model.TrackingRequest;
import com.novapost.model.TrackingStatus;

/**
 * Facade for the "Tracking" section of the Nova Post API documentation
 * (https://developers.novaposhta.ua/view/model/a99d2f28-8512-11ec-8ced-005056b2dbe1).
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java.
 */
public final class TrackingApi{

	private final NovaPostClient client;

	public TrackingApi(NovaPostClient client){
		this.client = client;
	}

	/**
	 * {@code TrackingDocumentGeneral/getStatusDocuments} (docs pages a9ae7bc9).
	 * <p>Authorized access (an API key is sent) returns the complete data, including the correct data of the sender and the recipient.
	 * <p>Unauthorized access (no API key) returns limited data: the sender and recipient data is replaced with the data that belongs to the phone number in the request.
	 * <p>One request may track up to 100 shipments (NovaPostClient splits longer lists). A phone number in Phone (recipient or sender) returns extended information.
	 * <p>Dates in the answer come in several formats (dd-MM-yyyy HH:mm:ss, dd.MM.yyyy HH:mm:ss, yyyy-MM-dd HH:mm:ss); each is read into a LocalDateTime (a plain date becomes midnight; an empty value, 0000-00-00 and 0001-01-01 become null). The live API also returns Edit, PossibilityEUReturn, PossibilityLightReturn, PossibilityTermExtension and PossibilityTrusteeRecipient, which the documentation does not list (the documented name is spelled PossibilityTermExtensio).
	 */
	public NpCall<TrackingStatus> getStatusDocuments(TrackingRequest request){
		return client.call(NpModel.TRACKING_DOCUMENT, "getStatusDocuments", request, TrackingStatus.class);
	}
}
