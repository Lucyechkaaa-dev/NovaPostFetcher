package com.novapost.model.document;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpBooleanDeserializer;
import com.novapost.model.support.NpDateTimeDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import com.novapost.model.support.NpIntegerDeserializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Map;

/**
 * Response model for {@code InternetDocumentGeneral/getEWTemplateList}.
 * Docs: https://developers.novaposhta.ua/view/model/a90d323c-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record InternetDocument(
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("ScanSheetRef") String scanSheetRef,
		@JsonProperty("BackwardDeliveryCargoType") String backwardDeliveryCargoType,
		@JsonProperty("EWNumber") String ewNumber,
		@JsonProperty("Number") String number,
		@JsonProperty("RecipientCityRef") String recipientCityRef,
		@JsonProperty("OriginalGeoData") JsonNode originalGeoData,
		@JsonProperty("RecipientContactPhone") String recipientContactPhone,
		@JsonProperty("ElevatorRecipient") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean elevatorRecipient,
		@JsonProperty("StateId") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer stateId,
		@JsonProperty("DeliveryPharma") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean deliveryPharma,
		@JsonProperty("TimeIntervalString") String timeIntervalString,
		@JsonProperty("Note") String note,
		@JsonProperty("RecipientDateTime") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime recipientDateTime,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("RecipientPost") String recipientPost,
		@JsonProperty("ChangedDataEW") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean changedDataEW,
		@JsonProperty("ScanSheetNumber") String scanSheetNumber,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("SaturdayDelivery") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean saturdayDelivery,
		@JsonProperty("BackwardDeliverySum") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal backwardDeliverySum,
		@JsonProperty("RecipientStreetRef") String recipientStreetRef,
		@JsonProperty("SecurePayment") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean securePayment,
		@JsonProperty("ThirdPerson") String thirdPerson,
		@JsonProperty("EWDateCreated") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime ewDateCreated,
		@JsonProperty("CitySenderDescription") String citySenderDescription,
		@JsonProperty("bCardPaymentPayed") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean bCardPaymentPayed,
		@JsonProperty("SenderDescription") String senderDescription,
		@JsonProperty("ScanSheetPrinted") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean scanSheetPrinted,
		@JsonProperty("RecipientContactPerson") String recipientContactPerson,
		@JsonProperty("RecipientWarehouseRef") String recipientWarehouseRef,
		@JsonProperty("ExpressWaybill") String expressWaybill,
		@JsonProperty("SenderAddress") String senderAddress,
		@JsonProperty("ScheduledDeliveryDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime scheduledDeliveryDate,
		@JsonProperty("RedBoxBarcode") String redBoxBarcode,
		@JsonProperty("Forwarding") String forwarding,
		@JsonProperty("RecipientsPhone") String recipientsPhone,
		@JsonProperty("AfterpaymentOnGoodsCost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal afterpaymentOnGoodsCost,
		@JsonProperty("RecipientDescription") String recipientDescription,
		@JsonProperty("Cost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal cost,
		@JsonProperty("CityRecipientDescription") String cityRecipientDescription,
		@JsonProperty("StateName") String stateName,
		@JsonProperty("bCardPaymentCancelled") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean bCardPaymentCancelled,
		@JsonProperty("CarCall") String carCall,
		@JsonProperty("RecipientAddress") String recipientAddress,
		@JsonProperty("DeliveryFrom") String deliveryFrom,
		@JsonProperty("InfoRegClientBarcodes") String infoRegClientBarcodes,
		@JsonProperty("WarehouseIllusha") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean warehouseIllusha,
		@JsonProperty("AgeIdentification") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean ageIdentification,
		@JsonProperty("IsTakeAttorney") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean isTakeAttorney,
		@JsonProperty("Promocode") String promocode,
		@JsonProperty("CargoType") String cargoType,
		@JsonProperty("LoyaltyCard") String loyaltyCard,
		@JsonProperty("MarketplacePartnerDescription") String marketplacePartnerDescription,
		@JsonProperty("NumberOfFloorsLifting") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer numberOfFloorsLifting,
		@JsonProperty("DeliveryDateFrom") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime deliveryDateFrom,
		@JsonProperty("EstimatedDeliveryDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime estimatedDeliveryDate,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("DeliveryByHand") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean deliveryByHand,
		@JsonProperty("DeliveryTo") String deliveryTo,
		@JsonProperty("Sender") String sender,
		@JsonProperty("StatePayId") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer statePayId,
		@JsonProperty("PackingNumber") String packingNumber,
		@JsonProperty("RecipientAddressDescription") String recipientAddressDescription,
		@JsonProperty("DateTime") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateTime,
		@JsonProperty("Ref") String ref,
		@JsonProperty("RegionCode") String regionCode,
		@JsonProperty("SettlementSender") String settlementSender,
		@JsonProperty("ForwardingCount") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer forwardingCount,
		@JsonProperty("BackwardDeliveryMoney") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal backwardDeliveryMoney,
		@JsonProperty("ContactSender") String contactSender,
		@JsonProperty("Vip") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean vip,
		@JsonProperty("LightReturn") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean lightReturn,
		@JsonProperty("Description") String description,
		@JsonProperty("MarketplacePartnerToken") String marketplacePartnerToken,
		@JsonProperty("SameDayDelivery") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean sameDayDelivery,
		@JsonProperty("StatePayName") String statePayName,
		@JsonProperty("PreferredDeliveryDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime preferredDeliveryDate,
		@JsonProperty("ExpressPallet") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean expressPallet,
		@JsonProperty("CardPaymentRef") String cardPaymentRef,
		@JsonProperty("StatementOfAcceptanceTransferCargoID") String statementOfAcceptanceTransferCargoID,
		@JsonProperty("CargoReturnRefusal") String cargoReturnRefusal,
		@JsonProperty("RecipientCounterpartyType") String recipientCounterpartyType,
		@JsonProperty("SenderContactPerson") String senderContactPerson,
		@JsonProperty("RejectionReason") String rejectionReason,
		@JsonProperty("DateLastUpdatedStatus") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateLastUpdatedStatus,
		@JsonProperty("OwnershipForm") String ownershipForm,
		@JsonProperty("LastModificationDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime lastModificationDate,
		@JsonProperty("Recipient") String recipient,
		@JsonProperty("BackwardDeliveryData") JsonNode backwardDeliveryData,
		@JsonProperty("SettlementRecipient") String settlementRecipient,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("TimeInterval") String timeInterval,
		@JsonProperty("MoneyTransferPayerType") String moneyTransferPayerType,
		@JsonProperty("WarehouseSender") String warehouseSender,
		@JsonProperty("Weight") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal weight,
		@JsonProperty("AviaDelivery") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean aviaDelivery,
		@JsonProperty("DateLastPrint") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime dateLastPrint,
		@JsonProperty("ReceiptDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime receiptDate,
		@JsonProperty("OnlineCreditStatus") String onlineCreditStatus,
		@JsonProperty("DeletionMark") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean deletionMark,
		@JsonProperty("SettlmentAddressData") JsonNode settlmentAddressData,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("TimeIntervalRef") String timeIntervalRef,
		@JsonProperty("WarehouseRecipient") String warehouseRecipient,
		@JsonProperty("ContactRecipient") String contactRecipient,
		@JsonProperty("AdditionalInformation") String additionalInformation,
		@JsonProperty("EDRPOU") String edrpou,
		@JsonProperty("FillingWarranty") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean fillingWarranty,
		@JsonProperty("AfterpaymentSum") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal afterpaymentSum,
		@JsonProperty("Posted") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean posted,
		@JsonProperty("SenderAddressDescription") String senderAddressDescription,
		@JsonProperty("Redelivery") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean redelivery,
		@JsonProperty("SeatsAmount") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer seatsAmount,
		@JsonProperty("CreateTime") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime createTime,
		@JsonProperty("CostOnSite") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal costOnSite,
		@JsonProperty("LightReturnNumber") String lightReturnNumber,
		@JsonProperty("Printed") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean printed,
		@JsonProperty("Route") String route,
		@JsonProperty("CardMaskedNumber") String cardMaskedNumber,
		@JsonProperty("System") String system,
		@JsonProperty("SendersPhone") String sendersPhone,
		@JsonProperty("RecipientFullName") String recipientFullName,
		@JsonProperty("SaturdayDeliveryString") String saturdayDeliveryString,
		@JsonAnySetter Map<String, JsonNode> extra
){

	/** {@code extra} holds every field of the answer that this model does not declare (it is never null); the API returns more than the documentation lists. */
	public InternetDocument{
		extra = extra == null ? Map.of() : Map.copyOf(extra);
	}

	@JsonAnyGetter
	public Map<String, JsonNode> extra(){
		return extra;
	}
}
