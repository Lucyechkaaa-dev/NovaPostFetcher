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
public record RedirectingOrderRequest(
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("Note") String note,
		@JsonProperty("OrderType") String orderType,
		@JsonProperty("Recipient") String recipient,
		@JsonProperty("RecipientContactName") String recipientContactName,
		@JsonProperty("RecipientPhone") String recipientPhone,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("Customer") String customer,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("RecipientSettlement") String recipientSettlement,
		@JsonProperty("RecipientSettlementStreet") String recipientSettlementStreet,
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("NoteAddressRecipient") String noteAddressRecipient,
		@JsonProperty("RecipientWarehouse") String recipientWarehouse
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String intDocNumber;
		private String paymentMethod;
		private String note;
		private String orderType = "orderRedirecting";
		private String recipient;
		private String recipientContactName;
		private String recipientPhone;
		private String payerType;
		private String customer;
		private String serviceType;
		private String recipientSettlement;
		private String recipientSettlementStreet;
		private String buildingNumber;
		private String noteAddressRecipient;
		private String recipientWarehouse;

		public Builder intDocNumber(String intDocNumber){ this.intDocNumber = intDocNumber; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder note(String note){ this.note = note; return this; }
		public Builder orderType(String orderType){ this.orderType = orderType; return this; }
		public Builder recipient(String recipient){ this.recipient = recipient; return this; }
		public Builder recipientContactName(String recipientContactName){ this.recipientContactName = recipientContactName; return this; }
		public Builder recipientPhone(String recipientPhone){ this.recipientPhone = recipientPhone; return this; }
		public Builder payerType(String payerType){ this.payerType = payerType; return this; }
		public Builder customer(String customer){ this.customer = customer; return this; }
		public Builder serviceType(String serviceType){ this.serviceType = serviceType; return this; }
		public Builder recipientSettlement(String recipientSettlement){ this.recipientSettlement = recipientSettlement; return this; }
		public Builder recipientSettlementStreet(String recipientSettlementStreet){ this.recipientSettlementStreet = recipientSettlementStreet; return this; }
		public Builder buildingNumber(String buildingNumber){ this.buildingNumber = buildingNumber; return this; }
		public Builder noteAddressRecipient(String noteAddressRecipient){ this.noteAddressRecipient = noteAddressRecipient; return this; }
		public Builder recipientWarehouse(String recipientWarehouse){ this.recipientWarehouse = recipientWarehouse; return this; }

		public RedirectingOrderRequest build(){
			return new RedirectingOrderRequest(intDocNumber, paymentMethod, note, orderType, recipient, recipientContactName, recipientPhone, payerType, customer, serviceType, recipientSettlement, recipientSettlementStreet, buildingNumber, noteAddressRecipient, recipientWarehouse);
		}
	}
}
