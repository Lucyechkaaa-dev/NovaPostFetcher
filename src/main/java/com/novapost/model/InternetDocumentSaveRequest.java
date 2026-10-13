package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record InternetDocumentSaveRequest(
		@JsonProperty("Ref") String ref,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("DateTime") @JsonFormat(pattern = "dd.MM.yyyy") LocalDate dateTime,
		@JsonProperty("CargoType") String cargoType,
		@JsonProperty("VolumeGeneral") String volumeGeneral,
		@JsonProperty("Weight") String weight,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("SeatsAmount") String seatsAmount,
		@JsonProperty("Description") String description,
		@JsonProperty("Cost") String cost,
		@JsonProperty("CitySender") String citySender,
		@JsonProperty("Sender") String sender,
		@JsonProperty("SenderAddress") String senderAddress,
		@JsonProperty("ContactSender") String contactSender,
		@JsonProperty("SendersPhone") String sendersPhone,
		@JsonProperty("CityRecipient") String cityRecipient,
		@JsonProperty("Recipient") String recipient,
		@JsonProperty("RecipientAddress") String recipientAddress,
		@JsonProperty("ContactRecipient") String contactRecipient,
		@JsonProperty("RecipientsPhone") String recipientsPhone,
		@JsonProperty("AfterpaymentOnGoodsCost") String afterpaymentOnGoodsCost,
		@JsonProperty("OptionsSeat") List<OptionSeat> optionsSeat
){

	public static Builder builder(){
		return new Builder();
	}

	public Builder toBuilder(){
		return new Builder()
				.ref(ref)
				.payerType(payerType)
				.paymentMethod(paymentMethod)
				.dateTime(dateTime)
				.cargoType(cargoType)
				.volumeGeneral(volumeGeneral)
				.weight(weight)
				.serviceType(serviceType)
				.seatsAmount(seatsAmount)
				.description(description)
				.cost(cost)
				.citySender(citySender)
				.sender(sender)
				.senderAddress(senderAddress)
				.contactSender(contactSender)
				.sendersPhone(sendersPhone)
				.cityRecipient(cityRecipient)
				.recipient(recipient)
				.recipientAddress(recipientAddress)
				.contactRecipient(contactRecipient)
				.recipientsPhone(recipientsPhone)
				.afterpaymentOnGoodsCost(afterpaymentOnGoodsCost)
				.optionsSeat(optionsSeat);
	}

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record OptionSeat(
			@JsonProperty("volumetricVolume") String volumetricVolume,
			@JsonProperty("volumetricWidth") String volumetricWidth,
			@JsonProperty("volumetricLength") String volumetricLength,
			@JsonProperty("volumetricHeight") String volumetricHeight,
			@JsonProperty("weight") String weight
	){

		public static OptionSeat ofDimensions(String widthCm, String lengthCm, String heightCm, String weightKg){
			return new OptionSeat(null, widthCm, lengthCm, heightCm, weightKg);
		}

		public static OptionSeat ofVolume(String volumeM3, String weightKg){
			return new OptionSeat(volumeM3, null, null, null, weightKg);
		}
	}

	public static class Builder{
		private String ref;
		private String payerType;
		private String paymentMethod;
		private LocalDate dateTime;
		private String cargoType;
		private String volumeGeneral;
		private String weight;
		private String serviceType;
		private String seatsAmount;
		private String description;
		private String cost;
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
		private String afterpaymentOnGoodsCost;
		private List<OptionSeat> optionsSeat;

		public Builder ref(String ref){ this.ref = ref; return this; }
		public Builder payerType(String payerType){ this.payerType = payerType; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder dateTime(LocalDate dateTime){ this.dateTime = dateTime; return this; }
		public Builder cargoType(String cargoType){ this.cargoType = cargoType; return this; }
		public Builder volumeGeneral(String volumeGeneral){ this.volumeGeneral = volumeGeneral; return this; }
		public Builder weight(String weight){ this.weight = weight; return this; }
		public Builder serviceType(String serviceType){ this.serviceType = serviceType; return this; }
		public Builder seatsAmount(String seatsAmount){ this.seatsAmount = seatsAmount; return this; }
		public Builder description(String description){ this.description = description; return this; }
		public Builder cost(String cost){ this.cost = cost; return this; }
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
		public Builder afterpaymentOnGoodsCost(String afterpaymentOnGoodsCost){ this.afterpaymentOnGoodsCost = afterpaymentOnGoodsCost; return this; }
		public Builder optionsSeat(List<OptionSeat> optionsSeat){ this.optionsSeat = optionsSeat; return this; }

		public Builder addOptionSeat(OptionSeat seat){
			if(this.optionsSeat == null){
				this.optionsSeat = new java.util.ArrayList<>();
			}
			this.optionsSeat.add(seat);
			return this;
		}

		public InternetDocumentSaveRequest build(){
			return new InternetDocumentSaveRequest(
					ref, payerType, paymentMethod, dateTime, cargoType, volumeGeneral, weight,
					serviceType, seatsAmount, description, cost, citySender, sender,
					senderAddress, contactSender, sendersPhone, cityRecipient, recipient,
					recipientAddress, contactRecipient, recipientsPhone, afterpaymentOnGoodsCost,
					optionsSeat
			);
		}
	}
}
