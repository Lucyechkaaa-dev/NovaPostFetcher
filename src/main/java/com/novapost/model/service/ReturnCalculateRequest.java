package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpIntegerDeserializer;

/**
 * Request model for {@code AdditionalServiceGeneral/save}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReturnCalculateRequest(
		@JsonProperty("Note") String note,
		@JsonProperty("OrderType") String orderType,
		@JsonProperty("ReturnAddressRef") String returnAddressRef,
		@JsonProperty("IntDocNumber") String intDocNumber,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("Reason") String reason,
		@JsonProperty("OnlyGetPricing") @JsonFormat(shape = JsonFormat.Shape.STRING) @JsonDeserialize(using = NpIntegerDeserializer.class) Integer onlyGetPricing,
		@JsonProperty("SubtypeReason") String subtypeReason
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String note;
		private String orderType = "orderCargoReturn";
		private String returnAddressRef;
		private String intDocNumber;
		private String paymentMethod;
		private String reason;
		private Integer onlyGetPricing = 1;
		private String subtypeReason;

		public Builder note(String note){ this.note = note; return this; }
		public Builder orderType(String orderType){ this.orderType = orderType; return this; }
		public Builder returnAddressRef(String returnAddressRef){ this.returnAddressRef = returnAddressRef; return this; }
		public Builder intDocNumber(String intDocNumber){ this.intDocNumber = intDocNumber; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder reason(String reason){ this.reason = reason; return this; }
		public Builder onlyGetPricing(Integer onlyGetPricing){ this.onlyGetPricing = onlyGetPricing; return this; }
		public Builder subtypeReason(String subtypeReason){ this.subtypeReason = subtypeReason; return this; }

		public ReturnCalculateRequest build(){
			return new ReturnCalculateRequest(note, orderType, returnAddressRef, intDocNumber, paymentMethod, reason, onlyGetPricing, subtypeReason);
		}
	}
}
