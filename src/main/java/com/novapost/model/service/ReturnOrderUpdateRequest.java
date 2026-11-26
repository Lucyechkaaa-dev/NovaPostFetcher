package com.novapost.model.service;

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
 * Request model for {@code AdditionalServiceGeneral/update}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReturnOrderUpdateRequest(
		@JsonProperty("Ref") String ref,
		@JsonProperty("OnlyGetPricing") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean onlyGetPricing,
		@JsonProperty("RecipientSettlement") String recipientSettlement,
		@JsonProperty("RecipientWarehouse") String recipientWarehouse,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("RecipientSettlementStreet") String recipientSettlementStreet,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("OrderType") String orderType,
		@JsonProperty("NoteAddressRecipient") String noteAddressRecipient,
		@JsonProperty("Reason") String reason,
		@JsonProperty("SubtypeReason") String subtypeReason
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String ref;
		private Boolean onlyGetPricing;
		private String recipientSettlement;
		private String recipientWarehouse;
		private String intDocNumber;
		private String recipientSettlementStreet;
		private String paymentMethod;
		private String buildingNumber;
		private String orderType = "orderCargoReturn";
		private String noteAddressRecipient;
		private String reason;
		private String subtypeReason;

		public Builder ref(String ref){ this.ref = ref; return this; }
		public Builder onlyGetPricing(Boolean onlyGetPricing){ this.onlyGetPricing = onlyGetPricing; return this; }
		public Builder recipientSettlement(String recipientSettlement){ this.recipientSettlement = recipientSettlement; return this; }
		public Builder recipientWarehouse(String recipientWarehouse){ this.recipientWarehouse = recipientWarehouse; return this; }
		public Builder intDocNumber(String intDocNumber){ this.intDocNumber = intDocNumber; return this; }
		public Builder recipientSettlementStreet(String recipientSettlementStreet){ this.recipientSettlementStreet = recipientSettlementStreet; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder buildingNumber(String buildingNumber){ this.buildingNumber = buildingNumber; return this; }
		public Builder orderType(String orderType){ this.orderType = orderType; return this; }
		public Builder noteAddressRecipient(String noteAddressRecipient){ this.noteAddressRecipient = noteAddressRecipient; return this; }
		public Builder reason(String reason){ this.reason = reason; return this; }
		public Builder subtypeReason(String subtypeReason){ this.subtypeReason = subtypeReason; return this; }

		public ReturnOrderUpdateRequest build(){
			return new ReturnOrderUpdateRequest(ref, onlyGetPricing, recipientSettlement, recipientWarehouse, intDocNumber, recipientSettlementStreet, paymentMethod, buildingNumber, orderType, noteAddressRecipient, reason, subtypeReason);
		}
	}
}
