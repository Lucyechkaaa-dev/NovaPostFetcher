package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;

/**
 * Ergonomic descriptor for senderRef and recipientRef routing information.
 * Allows creating a waybill draft before cargo dimensions and weight are known.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ParticipantRouting(
		String citySenderRef,
		String senderRef,
		String senderAddressRef,
		String contactSenderRef,
		String sendersPhone,
		String cityRecipientRef,
		String recipientRef,
		String recipientAddressRef,
		String contactRecipientRef,
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
		private String citySenderRef;
		private String senderRef;
		private String senderAddressRef;
		private String contactSenderRef;
		private String sendersPhone;
		private String cityRecipientRef;
		private String recipientRef;
		private String recipientAddressRef;
		private String contactRecipientRef;
		private String recipientsPhone;
		private String payerType = "Sender";
		private String paymentMethod = "NonCash";
		private String serviceType = "WarehouseWarehouse";
		private LocalDate date = LocalDate.now();
		private String afterpaymentOnGoodsCost;

		public Builder citySenderRef(String citySenderRef){ this.citySenderRef = citySenderRef; return this; }
		public Builder senderRef(String senderRef){ this.senderRef = senderRef; return this; }
		public Builder senderAddressRef(String senderAddressRef){ this.senderAddressRef = senderAddressRef; return this; }
		public Builder contactSenderRef(String contactSenderRef){ this.contactSenderRef = contactSenderRef; return this; }
		public Builder sendersPhone(String sendersPhone){ this.sendersPhone = sendersPhone; return this; }
		public Builder cityRecipientRef(String cityRecipientRef){ this.cityRecipientRef = cityRecipientRef; return this; }
		public Builder recipientRef(String recipientRef){ this.recipientRef = recipientRef; return this; }
		public Builder recipientAddressRef(String recipientAddressRef){ this.recipientAddressRef = recipientAddressRef; return this; }
		public Builder contactRecipientRef(String contactRecipientRef){ this.contactRecipientRef = contactRecipientRef; return this; }
		public Builder recipientsPhone(String recipientsPhone){ this.recipientsPhone = recipientsPhone; return this; }
		public Builder payerType(String payerType){ this.payerType = payerType; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder serviceType(String serviceType){ this.serviceType = serviceType; return this; }
		public Builder dateTime(LocalDate dateTime){ this.date = dateTime; return this; }
		public Builder afterpaymentOnGoodsCost(String cost){ this.afterpaymentOnGoodsCost = cost; return this; }

		public ParticipantRouting build(){
			return new ParticipantRouting(
					citySenderRef, senderRef, senderAddressRef, contactSenderRef, sendersPhone,
					cityRecipientRef, recipientRef, recipientAddressRef, contactRecipientRef, recipientsPhone,
					payerType, paymentMethod, serviceType, date, afterpaymentOnGoodsCost
			);
		}
	}
}
