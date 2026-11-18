package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * One item from TrackingDocument/getStatusDocuments.
 * Only the commonly used fields are declared; the rest of the response is ignored.
 * Dates are kept as strings because Nova Post uses several formats
 * (for example "10-11-2026 17:12:46" and "2026-11-11 11:26:05").
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TrackingStatus(
		@JsonProperty("Number") String number,
		@JsonProperty("StatusCode") String statusCode,
		@JsonProperty("Status") String status,
		@JsonProperty("DateCreated") String dateCreated,
		@JsonProperty("RecipientDateTime") String recipientDateTime,
		@JsonProperty("ScheduledDeliveryDate") String scheduledDeliveryDate,
		@JsonProperty("ActualDeliveryDate") String actualDeliveryDate,
		@JsonProperty("DateScan") String dateScan,
		@JsonProperty("TrackingUpdateDate") String trackingUpdateDate,
		@JsonProperty("CargoType") String cargoType,
		@JsonProperty("CargoDescriptionString") String cargoDescription,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("SeatsAmount") String seatsAmount,
		@JsonProperty("DocumentWeight") Double documentWeight,
		@JsonProperty("FactualWeight") String factualWeight,
		@JsonProperty("VolumeWeight") String volumeWeight,
		@JsonProperty("DocumentCost") String documentCost,
		@JsonProperty("AnnouncedPrice") Double announcedPrice,
		@JsonProperty("AfterpaymentOnGoodsCost") Double afterpaymentOnGoodsCost,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("CounterpartySenderDescription") String counterpartySenderDescription,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("RecipientFullName") String recipientFullName,
		@JsonProperty("WarehouseRecipient") String warehouseRecipient,
		@JsonProperty("WarehouseRecipientAddress") String warehouseRecipientAddress,
		@JsonProperty("WarehouseRecipientRef") String warehouseRecipientRef,
		@JsonProperty("PossibilityCreateClaim") Boolean possibilityCreateClaim
) {
}
