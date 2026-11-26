package com.novapost.model.counterparty;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpBooleanDeserializer;

import java.util.List;
import java.util.Map;

/**
 * Response model for {@code CounterpartyGeneral/getCounterpartyOptions}.
 * Docs: https://developers.novaposhta.ua/view/model/a28f4b04-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CounterpartyOptions(
		@JsonProperty("AddressDocumentDelivery") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean addressDocumentDelivery,
		@JsonProperty("AfterpaymentType") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean afterpaymentType,
		@JsonProperty("BackwardDeliverySubtypesDocuments") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean backwardDeliverySubtypesDocuments,
		@JsonProperty("BlockInternationalSenderLKK") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean blockInternationalSenderLKK,
		@JsonProperty("CalculationByFactualWeight") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean calculationByFactualWeight,
		@JsonProperty("CanAfterpaymentOnGoodsCost") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canAfterpaymentOnGoodsCost,
		@JsonProperty("CanCreditDocuments") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canCreditDocuments,
		@JsonProperty("CanEWTransporter") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canEWTransporter,
		@JsonProperty("CanForwardingService") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canForwardingService,
		@JsonProperty("CanNonCashPayment") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canNonCashPayment,
		@JsonProperty("CanPayTheThirdPerson") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canPayTheThirdPerson,
		@JsonProperty("CanSameDayDelivery") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canSameDayDelivery,
		@JsonProperty("CanSameDayDeliveryStandart") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canSameDayDeliveryStandart,
		@JsonProperty("CanSentFromPostomat") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canSentFromPostomat,
		@JsonProperty("CanSignedDocuments") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean canSignedDocuments,
		@JsonProperty("CarCall") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean carCall,
		@JsonProperty("CreditDocuments") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean creditDocuments,
		@JsonProperty("CustomerReturn") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean customerReturn,
		@JsonProperty("DayCustomerReturn") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean dayCustomerReturn,
		@JsonProperty("DebtorParams") JsonNode debtorParams,
		@JsonProperty("DeliveryByHand") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean deliveryByHand,
		@JsonProperty("DescentFromFloor") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean descentFromFloor,
		@JsonProperty("FillingWarranty") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean fillingWarranty,
		@JsonProperty("HaveMoneyWallets") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean haveMoneyWallets,
		@JsonProperty("HideDeliveryCost") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean hideDeliveryCost,
		@JsonProperty("InternationalDelivery") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean internationalDelivery,
		@JsonProperty("InternationalDeliveryServiceType") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean internationalDeliveryServiceType,
		@JsonProperty("LoyaltyProgram") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean loyaltyProgram,
		@JsonProperty("MainCounterparty") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean mainCounterparty,
		@JsonProperty("PickupService") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean pickupService,
		@JsonProperty("PrintMarkingAllowedTypes") JsonNode printMarkingAllowedTypes,
		@JsonProperty("SecurePayment") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean securePayment,
		@JsonProperty("Services") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean services,
		@JsonProperty("ShowDeliveryByHand") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean showDeliveryByHand,
		@JsonProperty("SignedDocuments") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean signedDocuments,
		@JsonProperty("TransferPricingConditions") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean transferPricingConditions
){
}
