package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request model for {@code AdditionalServiceGeneral/update}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RedirectingUpdateRequest(
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("NoteAddressRecipient") String noteAddressRecipient,
		@JsonProperty("Recipient") String recipient,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("Note") String note,
		@JsonProperty("Customer") String customer,
		@JsonProperty("Ref") String ref,
		@JsonProperty("RecipientContactName") String recipientContactName,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("RecipientWarehouse") String recipientWarehouse,
		@JsonProperty("RecipientPhone") String recipientPhone,
		@JsonProperty("SettlementRecipient") String settlementRecipient,
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("RecipientSettlementStreet") String recipientSettlementStreet,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("OrderType") String orderType
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String paymentMethod;
		private String noteAddressRecipient;
		private String recipient;
		private String cityRecipient;
		private String note;
		private String customer;
		private String ref;
		private String recipientContactName;
		private String intDocNumber;
		private String recipientWarehouse;
		private String recipientPhone;
		private String settlementRecipient;
		private String buildingNumber;
		private String recipientSettlementStreet;
		private String serviceType;
		private String payerType;
		private String orderType = "orderRedirecting";

		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder noteAddressRecipient(String noteAddressRecipient){ this.noteAddressRecipient = noteAddressRecipient; return this; }
		public Builder recipient(String recipient){ this.recipient = recipient; return this; }
		public Builder cityRecipient(String cityRecipient){ this.cityRecipient = cityRecipient; return this; }
		public Builder note(String note){ this.note = note; return this; }
		public Builder customer(String customer){ this.customer = customer; return this; }
		public Builder ref(String ref){ this.ref = ref; return this; }
		public Builder recipientContactName(String recipientContactName){ this.recipientContactName = recipientContactName; return this; }
		public Builder intDocNumber(String intDocNumber){ this.intDocNumber = intDocNumber; return this; }
		public Builder recipientWarehouse(String recipientWarehouse){ this.recipientWarehouse = recipientWarehouse; return this; }
		public Builder recipientPhone(String recipientPhone){ this.recipientPhone = recipientPhone; return this; }
		public Builder settlementRecipient(String settlementRecipient){ this.settlementRecipient = settlementRecipient; return this; }
		public Builder buildingNumber(String buildingNumber){ this.buildingNumber = buildingNumber; return this; }
		public Builder recipientSettlementStreet(String recipientSettlementStreet){ this.recipientSettlementStreet = recipientSettlementStreet; return this; }
		public Builder serviceType(String serviceType){ this.serviceType = serviceType; return this; }
		public Builder payerType(String payerType){ this.payerType = payerType; return this; }
		public Builder orderType(String orderType){ this.orderType = orderType; return this; }

		public RedirectingUpdateRequest build(){
			return new RedirectingUpdateRequest(paymentMethod, noteAddressRecipient, recipient, cityRecipient, note, customer, ref, recipientContactName, intDocNumber, recipientWarehouse, recipientPhone, settlementRecipient, buildingNumber, recipientSettlementStreet, serviceType, payerType, orderType);
		}
	}
}
