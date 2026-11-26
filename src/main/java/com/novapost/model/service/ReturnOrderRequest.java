package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for {@code AdditionalServiceGeneral/save}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReturnOrderRequest(
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("NoteAddressRecipient") String noteAddressRecipient,
		@JsonProperty("RecipientSettlement") String recipientSettlement,
		@JsonProperty("RecipientWarehouse") String recipientWarehouse,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("Reason") String reason,
		@JsonProperty("SubtypeReason") String subtypeReason,
		@JsonProperty("Note") String note,
		@JsonProperty("OrderType") String orderType,
		@JsonProperty("ReturnAddressRef") String returnAddressRef,
		@JsonProperty("RecipientSettlementStreet") String recipientSettlementStreet
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String buildingNumber;
		private String noteAddressRecipient;
		private String recipientSettlement;
		private String recipientWarehouse;
		private String intDocNumber;
		private String paymentMethod;
		private String reason;
		private String subtypeReason;
		private String note;
		private String orderType = "orderCargoReturn";
		private String returnAddressRef;
		private String recipientSettlementStreet;

		public Builder buildingNumber(String buildingNumber){ this.buildingNumber = buildingNumber; return this; }
		public Builder noteAddressRecipient(String noteAddressRecipient){ this.noteAddressRecipient = noteAddressRecipient; return this; }
		public Builder recipientSettlement(String recipientSettlement){ this.recipientSettlement = recipientSettlement; return this; }
		public Builder recipientWarehouse(String recipientWarehouse){ this.recipientWarehouse = recipientWarehouse; return this; }
		public Builder intDocNumber(String intDocNumber){ this.intDocNumber = intDocNumber; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder reason(String reason){ this.reason = reason; return this; }
		public Builder subtypeReason(String subtypeReason){ this.subtypeReason = subtypeReason; return this; }
		public Builder note(String note){ this.note = note; return this; }
		public Builder orderType(String orderType){ this.orderType = orderType; return this; }
		public Builder returnAddressRef(String returnAddressRef){ this.returnAddressRef = returnAddressRef; return this; }
		public Builder recipientSettlementStreet(String recipientSettlementStreet){ this.recipientSettlementStreet = recipientSettlementStreet; return this; }

		public ReturnOrderRequest build(){
			return new ReturnOrderRequest(buildingNumber, noteAddressRecipient, recipientSettlement, recipientWarehouse, intDocNumber, paymentMethod, reason, subtypeReason, note, orderType, returnAddressRef, recipientSettlementStreet);
		}
	}
}
