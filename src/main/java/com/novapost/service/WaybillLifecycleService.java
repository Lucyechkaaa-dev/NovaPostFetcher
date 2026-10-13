package com.novapost.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.novapost.controller.NovaPostApiException;
import com.novapost.controller.NovaPostController;
import com.novapost.model.CargoDetails;
import com.novapost.model.InternetDocumentListItem;
import com.novapost.model.InternetDocumentResponse;
import com.novapost.model.InternetDocumentSaveRequest;
import com.novapost.model.ParticipantRouting;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * High-level ergonomic lifecycle manager for Nova Post InternetDocuments (Waybills).
 * Supports the two-stage collaborative workflow:
 * 1. Initial creation with participant sender/recipient details and temporary placeholder cargo.
 * 2. Subsequent updates of cargo weight, dimensions, and declared cost by warehouse coworkers.
 */
public class WaybillLifecycleService {

	private final NovaPostController controller;

	public WaybillLifecycleService(NovaPostController controller) {
		this.controller = Objects.requireNonNull(controller, "NovaPostController must not be null");
	}

	public static WaybillLifecycleService create(String apiKey) {
		return new WaybillLifecycleService(NovaPostController.create(apiKey));
	}

	public static WaybillLifecycleService create(NovaPostController controller) {
		return new WaybillLifecycleService(controller);
	}

	public NovaPostController getController() {
		return controller;
	}

	/**
	 * Creates a waybill draft with participant sender/recipient routing and placeholder cargo values.
	 * Allows a sales or support agent to register the shipment before physical weight and seats are packed.
	 */
	public InternetDocumentResponse createDraft(ParticipantRouting routing) {
		return createWaybill(routing, CargoDetails.ofPlaceholder());
	}

	/**
	 * Creates a full waybill with both routing details and specified cargo parameters in a single step.
	 */
	public InternetDocumentResponse createWaybill(ParticipantRouting routing, CargoDetails cargo) {
		Objects.requireNonNull(routing, "ParticipantRouting must not be null");
		CargoDetails effectiveCargo = cargo != null ? cargo : CargoDetails.ofPlaceholder();

		InternetDocumentSaveRequest request = InternetDocumentSaveRequest.builder()
				.payerType(routing.payerType() != null ? routing.payerType() : "Sender")
				.paymentMethod(routing.paymentMethod() != null ? routing.paymentMethod() : "NonCash")
				.dateTime(routing.dateTime() != null ? routing.dateTime() : LocalDate.now())
				.serviceType(routing.serviceType() != null ? routing.serviceType() : "WarehouseWarehouse")
				// Routing
				.citySender(routing.citySender())
				.sender(routing.sender())
				.senderAddress(routing.senderAddress())
				.contactSender(routing.contactSender())
				.sendersPhone(routing.sendersPhone())
				.cityRecipient(routing.cityRecipient())
				.recipient(routing.recipient())
				.recipientAddress(routing.recipientAddress())
				.contactRecipient(routing.contactRecipient())
				.recipientsPhone(routing.recipientsPhone())
				.afterpaymentOnGoodsCost(routing.afterpaymentOnGoodsCost())
				// Cargo
				.cargoType(effectiveCargo.cargoType())
				.description(effectiveCargo.description())
				.weight(effectiveCargo.weight())
				.cost(effectiveCargo.cost())
				.seatsAmount(effectiveCargo.seatsAmount())
				.volumeGeneral(effectiveCargo.volumeGeneral())
				.optionsSeat(effectiveCargo.optionsSeat())
				.build();

		return controller.createWaybill(request);
	}

	/**
	 * Updates the physical cargo specs of an existing waybill by TTN.
	 * Automatically fetches the existing document JSON, preserves all sender/recipient routing,
	 * applies the new cargo details, and commits the update to Nova Post.
	 */
	public InternetDocumentResponse updateCargo(String ttn, LocalDate searchFrom, LocalDate searchTo, CargoDetails newCargo) {
		Objects.requireNonNull(ttn, "TTN must not be null");
		Objects.requireNonNull(newCargo, "CargoDetails must not be null");

		return controller.updateWaybillCargoByNumber(ttn, searchFrom, searchTo, builder -> applyCargo(builder, newCargo));
	}

	/**
	 * Convenience overload searching up to 30 days in the past.
	 */
	public InternetDocumentResponse updateCargo(String ttn, CargoDetails newCargo) {
		LocalDate to = LocalDate.now();
		LocalDate from = to.minusDays(30);
		return updateCargo(ttn, from, to, newCargo);
	}

	/**
	 * Updates cargo details by document UUID Ref directly.
	 * Fetches the document from account waybills, merges cargo, and updates.
	 */
	public InternetDocumentResponse updateCargoByRef(String documentRef, LocalDate searchFrom, LocalDate searchTo, CargoDetails newCargo) {
		Objects.requireNonNull(documentRef, "documentRef must not be null");
		Objects.requireNonNull(newCargo, "CargoDetails must not be null");

		JsonNode dataNode = controller.getWaybills(searchFrom, searchTo, 1, 100, JsonNode.class).stream()
				.filter(node -> documentRef.equalsIgnoreCase(node.path("Ref").asText()))
				.findFirst()
				.orElseThrow(() -> new NovaPostApiException("Document with Ref " + documentRef + " not found in range " + searchFrom + " to " + searchTo));

		String ttn = dataNode.path("IntDocNumber").asText();
		return updateCargo(ttn, searchFrom, searchTo, newCargo);
	}

	/**
	 * Fluent partial update using a customizer callback.
	 */
	public InternetDocumentResponse update(String ttn, LocalDate searchFrom, LocalDate searchTo, Consumer<InternetDocumentSaveRequest.Builder> customizer) {
		return controller.updateWaybillCargoByNumber(ttn, searchFrom, searchTo, customizer);
	}

	/**
	 * Convenience update overload using standard 30-day window.
	 */
	public InternetDocumentResponse update(String ttn, Consumer<InternetDocumentSaveRequest.Builder> customizer) {
		LocalDate to = LocalDate.now();
		LocalDate from = to.minusDays(30);
		return update(ttn, from, to, customizer);
	}

	/**
	 * Deletes an existing waybill by its document UUID Ref.
	 */
	public boolean deleteByRef(String documentRef) {
		return controller.deleteWaybill(documentRef);
	}

	/**
	 * Deletes an existing waybill by its TTN number by resolving its Ref first.
	 */
	public boolean deleteByTtn(String ttn, LocalDate searchFrom, LocalDate searchTo) {
		Optional<InternetDocumentListItem> item = controller.findWaybillByNumber(ttn, searchFrom, searchTo);
		if (item.isEmpty() || item.get().ref() == null) {
			throw new NovaPostApiException("Cannot delete waybill: TTN " + ttn + " not found in date range " + searchFrom + " to " + searchTo);
		}
		return deleteByRef(item.get().ref());
	}

	/**
	 * Convenience delete overload searching within the past 30 days.
	 */
	public boolean deleteByTtn(String ttn) {
		LocalDate to = LocalDate.now();
		LocalDate from = to.minusDays(30);
		return deleteByTtn(ttn, from, to);
	}

	private static void applyCargo(InternetDocumentSaveRequest.Builder builder, CargoDetails cargo) {
		if (cargo.cargoType() != null) builder.cargoType(cargo.cargoType());
		if (cargo.description() != null) builder.description(cargo.description());
		if (cargo.weight() != null) builder.weight(cargo.weight());
		if (cargo.cost() != null) builder.cost(cargo.cost());
		if (cargo.seatsAmount() != null) builder.seatsAmount(cargo.seatsAmount());
		if (cargo.volumeGeneral() != null) builder.volumeGeneral(cargo.volumeGeneral());
		if (cargo.optionsSeat() != null) builder.optionsSeat(cargo.optionsSeat());
	}
}
