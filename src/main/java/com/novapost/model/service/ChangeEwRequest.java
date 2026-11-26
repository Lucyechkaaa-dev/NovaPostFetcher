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
public record ChangeEwRequest(
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("OrderType") String orderType,
		@JsonProperty("SenderContactName") String senderContactName,
		@JsonProperty("SenderPhone") String senderPhone,
		@JsonProperty("Recipient") String recipient,
		@JsonProperty("RecipientContactName") String recipientContactName,
		@JsonProperty("RecipientPhone") String recipientPhone,
		@JsonProperty("PayerType") String payerType
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String intDocNumber;
		private String paymentMethod;
		private String orderType = "orderChangeEW";
		private String senderContactName;
		private String senderPhone;
		private String recipient;
		private String recipientContactName;
		private String recipientPhone;
		private String payerType;

		public Builder intDocNumber(String intDocNumber){ this.intDocNumber = intDocNumber; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder orderType(String orderType){ this.orderType = orderType; return this; }
		public Builder senderContactName(String senderContactName){ this.senderContactName = senderContactName; return this; }
		public Builder senderPhone(String senderPhone){ this.senderPhone = senderPhone; return this; }
		public Builder recipient(String recipient){ this.recipient = recipient; return this; }
		public Builder recipientContactName(String recipientContactName){ this.recipientContactName = recipientContactName; return this; }
		public Builder recipientPhone(String recipientPhone){ this.recipientPhone = recipientPhone; return this; }
		public Builder payerType(String payerType){ this.payerType = payerType; return this; }

		public ChangeEwRequest build(){
			return new ChangeEwRequest(intDocNumber, paymentMethod, orderType, senderContactName, senderPhone, recipient, recipientContactName, recipientPhone, payerType);
		}
	}
}
