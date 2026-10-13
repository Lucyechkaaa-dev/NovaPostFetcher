package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/**
 * Ergonomic descriptor for sender and recipient routing information.
 * Allows creating a waybill draft before cargo dimensions and weight are known.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ParticipantRouting(
		String citySender,
		String sender,
		String senderAddress,
		String contactSender,
		String sendersPhone,
		String cityRecipient,
		String recipient,
		String recipientAddress,
		String contactRecipient,
		String recipientsPhone,
		String payerType,
		String paymentMethod,
		String serviceType,
		LocalDate dateTime,
		String afterpaymentOnGoodsCost
){

	public static Builder builder(){
		return new Builder();
	}

	public static class Builder{
		private String citySender;
		private String sender;
		private String senderAddress;
		private String contactSender;
		private String sendersPhone;
		private String cityRecipient;
		private String recipient;
		private String recipientAddress;
		private String contactRecipient;
		private String recipientsPhone;
		private String payerType = "Sender";
		private String paymentMethod = "NonCash";
		private String serviceType = "WarehouseWarehouse";
		private LocalDate dateTime = LocalDate.now();
		private String afterpaymentOnGoodsCost;

		public Builder citySender(String citySender){ this.citySender = citySender; return this; }
		public Builder sender(String sender){ this.sender = sender; return this; }
		public Builder senderAddress(String senderAddress){ this.senderAddress = senderAddress; return this; }
		public Builder contactSender(String contactSender){ this.contactSender = contactSender; return this; }
		public Builder sendersPhone(String sendersPhone){ this.sendersPhone = sendersPhone; return this; }
		public Builder cityRecipient(String cityRecipient){ this.cityRecipient = cityRecipient; return this; }
		public Builder recipient(String recipient){ this.recipient = recipient; return this; }
		public Builder recipientAddress(String recipientAddress){ this.recipientAddress = recipientAddress; return this; }
		public Builder contactRecipient(String contactRecipient){ this.contactRecipient = contactRecipient; return this; }
		public Builder recipientsPhone(String recipientsPhone){ this.recipientsPhone = recipientsPhone; return this; }
		public Builder payerType(String payerType){ this.payerType = payerType; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder serviceType(String serviceType){ this.serviceType = serviceType; return this; }
		public Builder dateTime(LocalDate dateTime){ this.dateTime = dateTime; return this; }
		public Builder afterpaymentOnGoodsCost(String cost){ this.afterpaymentOnGoodsCost = cost; return this; }

		public ParticipantRouting build(){
			return new ParticipantRouting(
					citySender, sender, senderAddress, contactSender, sendersPhone,
					cityRecipient, recipient, recipientAddress, contactRecipient, recipientsPhone,
					payerType, paymentMethod, serviceType, dateTime, afterpaymentOnGoodsCost
			);
		}
	}
}
