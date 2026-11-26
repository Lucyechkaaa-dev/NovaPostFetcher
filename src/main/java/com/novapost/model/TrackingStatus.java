package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpBooleanDeserializer;

import java.time.LocalDateTime;
import java.util.Map;
import com.novapost.model.support.NpDateTimeDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import java.math.BigDecimal;
import com.novapost.model.support.NpIntegerDeserializer;

/**
 * Response model for {@code TrackingDocumentGeneral/getStatusDocuments}.
 * Docs: https://developers.novaposhta.ua/view/model/a99d2f28-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record TrackingStatus(
		@JsonProperty("PossibilityCreateReturn") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityCreateReturn,
		@JsonProperty("PossibilityCreateRefusal") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityCreateRefusal,
		@JsonProperty("PossibilityChangeEW") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityChangeEW,
		@JsonProperty("PossibilityCreateRedirecting") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityCreateRedirecting,
		@JsonProperty("Number") String number,
		@JsonProperty("Redelivery") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean redelivery,
		@JsonProperty("RedeliverySum") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal redeliverySum,
		@JsonProperty("RedeliveryNum") String redeliveryNum,
		@JsonProperty("RedeliveryPayer") String redeliveryPayer,
		@JsonProperty("OwnerDocumentType") String ownerDocumentType,
		@JsonProperty("LastCreatedOnTheBasisDocumentType") String lastCreatedOnTheBasisDocumentType,
		@JsonProperty("LastCreatedOnTheBasisPayerType") String lastCreatedOnTheBasisPayerType,
		@JsonProperty("LastCreatedOnTheBasisDateTime") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime lastCreatedOnTheBasisDateTime,
		@JsonProperty("LastTransactionStatusGM") String lastTransactionStatusGM,
		@JsonProperty("LastTransactionDateTimeGM") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime lastTransactionDateTimeGM,
		@JsonProperty("LastAmountTransferGM") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal lastAmountTransferGM,
		@JsonProperty("DateCreated") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateCreated,
		@JsonProperty("DocumentWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal documentWeight,
		@JsonProperty("FactualWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal factualWeight,
		@JsonProperty("VolumeWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal volumeWeight,
		@JsonProperty("CheckWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal checkWeight,
		@JsonProperty("CheckWeightMethod") String checkWeightMethod,
		@JsonProperty("DocumentCost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal documentCost,
		@JsonProperty("CalculatedWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal calculatedWeight,
		@JsonProperty("SumBeforeCheckWeight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal sumBeforeCheckWeight,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("RecipientFullName") String recipientFullName,
		@JsonProperty("RecipientDateTime") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime recipientDateTime,
		@JsonProperty("ScheduledDeliveryDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime scheduledDeliveryDate,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("CargoDescriptionString") String cargoDescriptionString,
		@JsonProperty("CargoType") String cargoType,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("WarehouseRecipient") String warehouseRecipient,
		@JsonProperty("CounterpartyType") String counterpartyType,
		@JsonProperty("AfterpaymentOnGoodsCost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal afterpaymentOnGoodsCost,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("UndeliveryReasonsSubtypeDescription") String undeliveryReasonsSubtypeDescription,
		@JsonProperty("WarehouseRecipientNumber") String warehouseRecipientNumber,
		@JsonProperty("LastCreatedOnTheBasisNumber") String lastCreatedOnTheBasisNumber,
		@JsonProperty("PhoneRecipient") String phoneRecipient,
		@JsonProperty("RecipientFullNameEW") String recipientFullNameEW,
		@JsonProperty("WarehouseRecipientInternetAddressRef") String warehouseRecipientInternetAddressRef,
		@JsonProperty("MarketplacePartnerToken") String marketplacePartnerToken,
		@JsonProperty("ClientBarcode") String clientBarcode,
		@JsonProperty("RecipientAddress") String recipientAddress,
		@JsonProperty("CounterpartyRecipientDescription") String counterpartyRecipientDescription,
		@JsonProperty("CounterpartySenderType") String counterpartySenderType,
		@JsonProperty("DateScan") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateScan,
		@JsonProperty("PaymentStatus") String paymentStatus,
		@JsonProperty("PaymentStatusDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime paymentStatusDate,
		@JsonProperty("AmountToPay") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal amountToPay,
		@JsonProperty("AmountPaid") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal amountPaid,
		@JsonProperty("Status") String status,
		@JsonProperty("StatusCode") String statusCode,
		@JsonProperty("RefEW") String refEW,
		@JsonProperty("BackwardDeliverySubTypesActions") String backwardDeliverySubTypesActions,
		@JsonProperty("BackwardDeliverySubTypesServices") String backwardDeliverySubTypesServices,
		@JsonProperty("UndeliveryReasons") String undeliveryReasons,
		@JsonProperty("DatePayedKeeping") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime datePayedKeeping,
		@JsonProperty("InternationalDeliveryType") String internationalDeliveryType,
		@JsonProperty("SeatsAmount") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer seatsAmount,
		@JsonProperty("CardMaskedNumber") String cardMaskedNumber,
		@JsonProperty("ExpressWaybillPaymentStatus") String expressWaybillPaymentStatus,
		@JsonProperty("ExpressWaybillAmountToPay") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal expressWaybillAmountToPay,
		@JsonProperty("PhoneSender") String phoneSender,
		@JsonProperty("WarehouseSender") String warehouseSender,
		@JsonProperty("TrackingUpdateDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime trackingUpdateDate,
		@JsonProperty("DateReturnCargo") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateReturnCargo,
		@JsonProperty("DateMoving") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateMoving,
		@JsonProperty("DateFirstDayStorage") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateFirstDayStorage,
		@JsonProperty("RefCityRecipient") String refCityRecipient,
		@JsonProperty("RefCitySender") String refCitySender,
		@JsonProperty("RefSettlementRecipient") String refSettlementRecipient,
		@JsonProperty("RefSettlementSender") String refSettlementSender,
		@JsonProperty("SenderAddress") String senderAddress,
		@JsonProperty("SenderFullNameEW") String senderFullNameEW,
		@JsonProperty("AnnouncedPrice") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal announcedPrice,
		@JsonProperty("AdditionalInformationEW") String additionalInformationEW,
		@JsonProperty("ActualDeliveryDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime actualDeliveryDate,
		@JsonProperty("PostomatV3CellReservationNumber") String postomatV3CellReservationNumber,
		@JsonProperty("OwnerDocumentNumber") String ownerDocumentNumber,
		@JsonProperty("LastAmountReceivedCommissionGM") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal lastAmountReceivedCommissionGM,
		@JsonProperty("DeliveryTimeframe") String deliveryTimeframe,
		@JsonProperty("CreatedOnTheBasis") String createdOnTheBasis,
		@JsonProperty("UndeliveryReasonsDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime undeliveryReasonsDate,
		@JsonProperty("RecipientWarehouseTypeRef") String recipientWarehouseTypeRef,
		@JsonProperty("WarehouseRecipientRef") String warehouseRecipientRef,
		@JsonProperty("CategoryOfWarehouse") String categoryOfWarehouse,
		@JsonProperty("WarehouseRecipientAddress") String warehouseRecipientAddress,
		@JsonProperty("WarehouseSenderInternetAddressRef") String warehouseSenderInternetAddressRef,
		@JsonProperty("WarehouseSenderAddress") String warehouseSenderAddress,
		@JsonProperty("AviaDelivery") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean aviaDelivery,
		@JsonProperty("BarcodeRedBox") String barcodeRedBox,
		@JsonProperty("CargoReturnRefusal") String cargoReturnRefusal,
		@JsonProperty("DaysStorageCargo") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer daysStorageCargo,
		@JsonProperty("Packaging") JsonNode packaging,
		@JsonProperty("PartialReturnGoods") JsonNode partialReturnGoods,
		@JsonProperty("SecurePayment") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean securePayment,
		@JsonProperty("PossibilityChangeCash2Card") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityChangeCash2Card,
		@JsonProperty("PossibilityChangeDeliveryIntervals") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityChangeDeliveryIntervals,
		@JsonProperty("PossibilityTermExtensio") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityTermExtensio,
		@JsonProperty("Edit") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean edit,
		@JsonProperty("PossibilityEUReturn") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityEUReturn,
		@JsonProperty("PossibilityLightReturn") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityLightReturn,
		@JsonProperty("PossibilityTermExtension") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityTermExtension,
		@JsonProperty("PossibilityTrusteeRecipient") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityTrusteeRecipient,
		@JsonProperty("StorageAmount") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal storageAmount,
		@JsonProperty("StoragePrice") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal storagePrice,
		@JsonProperty("FreeShipping") String freeShipping,
		@JsonProperty("LoyaltyCardRecipient") String loyaltyCardRecipient,
		@JsonProperty("CounterpartySenderDescription") String counterpartySenderDescription,
		@JsonProperty("PossibilityCreateClaim") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean possibilityCreateClaim,
		@JsonAnySetter Map<String, JsonNode> extra
){

	// @keep: hand-edited after generation (convenience date accessor, unknown-field capture); the generator will not overwrite this file.

	/** {@code extra} holds every field of the answer that this model does not declare (it is never null). */
	public TrackingStatus{
		extra = extra == null ? Map.of() : Map.copyOf(extra);
	}

	@JsonAnyGetter
	public Map<String, JsonNode> extra(){
		return extra;
	}

	/** {@code StatusCode} as a documented constant; {@code UNKNOWN} for a code the documentation does not list. */
	public com.novapost.model.enums.TrackingStatusCode statusCodeValue(){
		return com.novapost.model.enums.TrackingStatusCode.of(statusCode);
	}
}
