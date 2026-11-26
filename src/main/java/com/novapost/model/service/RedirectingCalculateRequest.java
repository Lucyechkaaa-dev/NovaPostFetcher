package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code AdditionalServiceGeneral/save}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RedirectingCalculateRequest(
		@JsonProperty("RecipientSettlement") String recipientSettlement,
		@JsonProperty("Recipient") String recipient,
		@JsonProperty("RecipientSettlementStreet") String recipientSettlementStreet,
		@JsonProperty("RecipientWarehouse") String recipientWarehouse,
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("NoteAddressRecipient") String noteAddressRecipient,
		@JsonProperty("Customer") String customer,
		@JsonProperty("RecipientContactName") String recipientContactName,
		@JsonProperty("RecipientPhone") String recipientPhone,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("Note") String note,
		@JsonProperty("OrderType") String orderType,
		@JsonProperty("ServiceType") String serviceType
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String recipientSettlement;
		private String recipient;
		private String recipientSettlementStreet;
		private String recipientWarehouse;
		private String buildingNumber;
		private String noteAddressRecipient;
		private String customer;
		private String recipientContactName;
		private String recipientPhone;
		private String intDocNumber;
		private String paymentMethod;
		private String payerType;
		private String note;
		private String orderType = "orderRedirecting";
		private String serviceType;

		public Builder recipientSettlement(String recipientSettlement){ this.recipientSettlement = recipientSettlement; return this; }
		public Builder recipient(String recipient){ this.recipient = recipient; return this; }
		public Builder recipientSettlementStreet(String recipientSettlementStreet){ this.recipientSettlementStreet = recipientSettlementStreet; return this; }
		public Builder recipientWarehouse(String recipientWarehouse){ this.recipientWarehouse = recipientWarehouse; return this; }
		public Builder buildingNumber(String buildingNumber){ this.buildingNumber = buildingNumber; return this; }
		public Builder noteAddressRecipient(String noteAddressRecipient){ this.noteAddressRecipient = noteAddressRecipient; return this; }
		public Builder customer(String customer){ this.customer = customer; return this; }
		public Builder recipientContactName(String recipientContactName){ this.recipientContactName = recipientContactName; return this; }
		public Builder recipientPhone(String recipientPhone){ this.recipientPhone = recipientPhone; return this; }
		public Builder intDocNumber(String intDocNumber){ this.intDocNumber = intDocNumber; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder payerType(String payerType){ this.payerType = payerType; return this; }
		public Builder note(String note){ this.note = note; return this; }
		public Builder orderType(String orderType){ this.orderType = orderType; return this; }
		public Builder serviceType(String serviceType){ this.serviceType = serviceType; return this; }

		public RedirectingCalculateRequest build(){
			return new RedirectingCalculateRequest(recipientSettlement, recipient, recipientSettlementStreet, recipientWarehouse, buildingNumber, noteAddressRecipient, customer, recipientContactName, recipientPhone, intDocNumber, paymentMethod, payerType, note, orderType, serviceType);
		}
	}
}
