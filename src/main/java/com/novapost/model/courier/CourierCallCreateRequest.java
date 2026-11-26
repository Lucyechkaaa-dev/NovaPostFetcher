package com.novapost.model.courier;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.novapost.model.support.NpDateDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import com.novapost.model.support.NpDecimalSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Request model for {@code CarCallGeneral/saveCourierCall}.
 * Docs: https://developers.novaposhta.ua/view/model/96141693-eaae-11ef-84e0-48df37b921da
 * <p>Generated from src/test/resources/novaposhta/api-spec.txt by tools/ModelGenerator.java;
 * change the spec and re-run (to hand-edit this file, add the keep marker described in the generator).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CourierCallCreateRequest(
		@JsonProperty("ContactSenderRef") String contactSenderRef,
		@JsonProperty("PreferredDeliveryDate") @JsonFormat(pattern = "dd.MM.yyyy") @JsonDeserialize(using = NpDateDeserializer.class) LocalDate preferredDeliveryDate,
		@JsonProperty("PlanedWeight") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal planedWeight,
		@JsonProperty("TimeInterval") String timeInterval,
		@JsonProperty("CounterpartySender") String counterpartySender,
		@JsonProperty("AddressSenderRef") String addressSenderRef
){

	public static Builder builder(){
		return new Builder();
	}

	public static final class Builder{
		private String contactSenderRef;
		private LocalDate preferredDeliveryDate;
		private BigDecimal planedWeight;
		private String timeInterval;
		private String counterpartySender;
		private String addressSenderRef;

		public Builder contactSenderRef(String contactSenderRef){ this.contactSenderRef = contactSenderRef; return this; }
		public Builder preferredDeliveryDate(LocalDate preferredDeliveryDate){ this.preferredDeliveryDate = preferredDeliveryDate; return this; }
		public Builder planedWeight(BigDecimal planedWeight){ this.planedWeight = planedWeight; return this; }
		public Builder timeInterval(String timeInterval){ this.timeInterval = timeInterval; return this; }
		public Builder counterpartySender(String counterpartySender){ this.counterpartySender = counterpartySender; return this; }
		public Builder addressSenderRef(String addressSenderRef){ this.addressSenderRef = addressSenderRef; return this; }

		public CourierCallCreateRequest build(){
			return new CourierCallCreateRequest(contactSenderRef, preferredDeliveryDate, planedWeight, timeInterval, counterpartySender, addressSenderRef);
		}
	}
}
