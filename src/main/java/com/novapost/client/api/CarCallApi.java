package com.novapost.client.api;

import com.novapost.client.NovaPostClient;
import com.novapost.client.NpCall;
import com.novapost.client.NpModel;
import com.novapost.model.common.RefRequest;
import com.novapost.model.courier.*;

/**
 * Facade for the "CarCall" section of the Nova Post API documentation
 * (https://developers.novaposhta.ua/view/model/96141693-eaae-11ef-84e0-48df37b921da).
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java.
 */
public final class CarCallApi{

	private final NovaPostClient client;

	public CarCallApi(NovaPostClient client){
		this.client = client;
	}

	/**
	 * {@code CarCallGeneral/getOrdersListCourierCall} (docs pages 6ed8c0ea,97e2097b).
	 */
	public NpCall<CourierCall> getOrders(CourierCallFilter request){
		return client.call(NpModel.CAR_CALL, "getOrdersListCourierCall", request, CourierCall.class);
	}

	/**
	 * {@code CarCallGeneral/insertDocuments} (docs pages 730e80bc).
	 */
	public NpCall<CourierCallDocumentsResult> insertDocuments(CourierCallDocumentsRequest request){
		return client.call(NpModel.CAR_CALL, "insertDocuments", request, CourierCallDocumentsResult.class);
	}

	/**
	 * {@code CarCallGeneral/getAvailableOrdersList} (docs pages d5efafc8).
	 */
	public NpCall<AvailableCourierCall> getAvailableOrders(AvailableCourierCallsRequest request){
		return client.call(NpModel.CAR_CALL, "getAvailableOrdersList", request, AvailableCourierCall.class);
	}

	/**
	 * {@code CarCallGeneral/deleteCourierCall} (docs pages edfb5c7d).
	 */
	public NpCall<CourierCallCancelResult> cancel(RefRequest request){
		return client.call(NpModel.CAR_CALL, "deleteCourierCall", request, CourierCallCancelResult.class);
	}

	/**
	 * {@code CarCallGeneral/updateCourierCall} (docs pages ee1ff6d3).
	 */
	public NpCall<CourierCallBarcode> update(CourierCallUpdateRequest request){
		return client.call(NpModel.CAR_CALL, "updateCourierCall", request, CourierCallBarcode.class);
	}

	/**
	 * {@code CarCallGeneral/removeDocuments} (docs pages ee23f404).
	 */
	public NpCall<CourierCallRemoveResult> removeDocuments(CourierCallRemoveDocumentsRequest request){
		return client.call(NpModel.CAR_CALL, "removeDocuments", request, CourierCallRemoveResult.class);
	}

	/**
	 * {@code CarCallGeneral/saveCourierCall} (docs pages ee2a2001).
	 */
	public NpCall<CourierCallBarcode> create(CourierCallCreateRequest request){
		return client.call(NpModel.CAR_CALL, "saveCourierCall", request, CourierCallBarcode.class);
	}
}
