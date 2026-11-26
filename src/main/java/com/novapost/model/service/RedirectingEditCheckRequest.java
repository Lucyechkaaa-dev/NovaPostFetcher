package com.novapost.model.service;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.novapost.model.support.NpDecimalDeserializer;
import com.novapost.model.support.NpDecimalSerializer;
import java.math.BigDecimal;

import java.util.List;
import java.util.Map;

/**
 * Request model for {@code AdditionalServiceGeneral/checkPossibilityForRedirecting}.
 * Docs: https://developers.novaposhta.ua/view/model/a7682c1a-8512-11ec-8ced-005056b2dbe1
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record RedirectingEditCheckRequest(
		@JsonProperty("OrderRef") String orderRef,
		@JsonProperty("AddressDescription") String addressDescription,
		@JsonProperty("RecipientName") String recipientName,
		@JsonProperty("PhoneSender") String phoneSender,
		@JsonProperty("StreetDescription") String streetDescription,
		@JsonProperty("PhoneRecipient") String phoneRecipient,
		@JsonProperty("BuildingNumber") String buildingNumber,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("DocumentWeight") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal documentWeight,
		@JsonProperty("SettlementRecipient") String settlementRecipient,
		@JsonProperty("SettlementType") String settlementType,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("CounterpartyRecipientRef") String counterpartyRecipientRef,
		@JsonProperty("WarehouseRef") String warehouseRef
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String orderRef;
		private String addressDescription;
		private String recipientName;
		private String phoneSender;
		private String streetDescription;
		private String phoneRecipient;
		private String buildingNumber;
		private String cityRecipient;
		private BigDecimal documentWeight;
		private String settlementRecipient;
		private String settlementType;
		private String payerType;
		private String paymentMethod;
		private String counterpartyRecipientRef;
		private String warehouseRef;

		public Builder orderRef(String orderRef){ this.orderRef = orderRef; return this; }
		public Builder addressDescription(String addressDescription){ this.addressDescription = addressDescription; return this; }
		public Builder recipientName(String recipientName){ this.recipientName = recipientName; return this; }
		public Builder phoneSender(String phoneSender){ this.phoneSender = phoneSender; return this; }
		public Builder streetDescription(String streetDescription){ this.streetDescription = streetDescription; return this; }
		public Builder phoneRecipient(String phoneRecipient){ this.phoneRecipient = phoneRecipient; return this; }
		public Builder buildingNumber(String buildingNumber){ this.buildingNumber = buildingNumber; return this; }
		public Builder cityRecipient(String cityRecipient){ this.cityRecipient = cityRecipient; return this; }
		public Builder documentWeight(BigDecimal documentWeight){ this.documentWeight = documentWeight; return this; }
		public Builder settlementRecipient(String settlementRecipient){ this.settlementRecipient = settlementRecipient; return this; }
		public Builder settlementType(String settlementType){ this.settlementType = settlementType; return this; }
		public Builder payerType(String payerType){ this.payerType = payerType; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder counterpartyRecipientRef(String counterpartyRecipientRef){ this.counterpartyRecipientRef = counterpartyRecipientRef; return this; }
		public Builder warehouseRef(String warehouseRef){ this.warehouseRef = warehouseRef; return this; }

		public RedirectingEditCheckRequest build(){
			return new RedirectingEditCheckRequest(orderRef, addressDescription, recipientName, phoneSender, streetDescription, phoneRecipient, buildingNumber, cityRecipient, documentWeight, settlementRecipient, settlementType, payerType, paymentMethod, counterpartyRecipientRef, warehouseRef);
		}
	}
}
