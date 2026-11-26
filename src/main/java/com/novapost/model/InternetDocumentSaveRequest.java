package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.novapost.model.support.NpBooleanDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import com.novapost.model.support.NpDecimalSerializer;
import com.novapost.model.support.NpFlagSerializer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Request of {@code InternetDocumentGeneral/save} and {@code update}: every parameter of the eight documented variants
 * (standard, to a postomat, several cargo types, additional services, to a branch given as text, with backward delivery,
 * from a postomat, to an address given as text). Use {@link #builder()}; the recipes {@link Builder#toPostomat},
 * {@link Builder#fromPostomat}, {@link Builder#recipientBranchByText} and {@link Builder#recipientAddressByText} fill in the
 * parameters of those variants.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record InternetDocumentSaveRequest(
		@JsonProperty("Ref") String ref,
		@JsonProperty("PayerType") String payerType,
		@JsonProperty("PaymentMethod") String paymentMethod,
		@JsonProperty("DateTime") @JsonFormat(pattern = "dd.MM.yyyy") LocalDate dateTime,
		@JsonProperty("CargoType") String cargoType,
		@JsonProperty("VolumeGeneral") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal volumeGeneral,
		@JsonProperty("Weight") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal weight,
		@JsonProperty("ServiceType") String serviceType,
		@JsonProperty("SeatsAmount") @JsonFormat(shape = JsonFormat.Shape.STRING) Integer seatsAmount,
		@JsonProperty("Description") String description,
		@JsonProperty("Cost") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal cost,
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
		@JsonProperty("AfterpaymentOnGoodsCost") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal afterpaymentOnGoodsCost,
		@JsonProperty("OptionsSeat") List<OptionSeat> optionsSeat,
		@JsonProperty("SenderWarehouseIndex") String senderWarehouseIndex,
		@JsonProperty("RecipientWarehouseIndex") String recipientWarehouseIndex,
		@JsonProperty("RedBoxBarcode") String redBoxBarcode,
		@JsonProperty("NewAddress") @JsonSerialize(using = NpFlagSerializer.class) @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean newAddress,
		@JsonProperty("RecipientCityName") String recipientCityName,
		@JsonProperty("RecipientArea") String recipientArea,
		@JsonProperty("RecipientAreaRegions") String recipientAreaRegions,
		@JsonProperty("RecipientAddressName") String recipientAddressName,
		@JsonProperty("RecipientHouse") String recipientHouse,
		@JsonProperty("RecipientFlat") String recipientFlat,
		@JsonProperty("RecipientName") String recipientName,
		@JsonProperty("RecipientType") String recipientType,
		@JsonProperty("SettlementType") String settlementType,
		@JsonProperty("OwnershipForm") String ownershipForm,
		@JsonProperty("RecipientContactName") String recipientContactName,
		@JsonProperty("EDRPOU") String edrpou,
		@JsonProperty("BackwardDeliveryData") List<BackwardDeliveryData> backwardDeliveryData,
		@JsonProperty("RecipientAddressNote") String recipientAddressNote
){

	/** The shape before the postomat, text-recipient and backward delivery parameters were modelled. */
	public InternetDocumentSaveRequest(String ref, String payerType, String paymentMethod, LocalDate dateTime, String cargoType,
	                                   BigDecimal volumeGeneral, BigDecimal weight, String serviceType, Integer seatsAmount,
	                                   String description, BigDecimal cost, String citySender, String sender,
	                                   String senderAddress, String contactSender, String sendersPhone,
	                                   String cityRecipient, String recipient, String recipientAddress,
	                                   String contactRecipient, String recipientsPhone, BigDecimal afterpaymentOnGoodsCost,
	                                   List<OptionSeat> optionsSeat){
		this(ref, payerType, paymentMethod, dateTime, cargoType, volumeGeneral, weight, serviceType, seatsAmount, description,
				cost, citySender, sender, senderAddress, contactSender, sendersPhone, cityRecipient, recipient,
				recipientAddress, contactRecipient, recipientsPhone, afterpaymentOnGoodsCost, optionsSeat,
				null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
	}

	public static Builder builder(){
		return new Builder();
	}

	public Builder toBuilder(){
		return new Builder()
				.ref(ref).payerType(payerType).paymentMethod(paymentMethod).dateTime(dateTime).cargoType(cargoType)
				.volumeGeneral(volumeGeneral).weight(weight).serviceType(serviceType).seatsAmount(seatsAmount)
				.description(description).cost(cost).citySender(citySender).sender(sender).senderAddress(senderAddress)
				.contactSender(contactSender).sendersPhone(sendersPhone).cityRecipient(cityRecipient).recipient(recipient)
				.recipientAddress(recipientAddress).contactRecipient(contactRecipient).recipientsPhone(recipientsPhone)
				.afterpaymentOnGoodsCost(afterpaymentOnGoodsCost).optionsSeat(optionsSeat)
				.senderWarehouseIndex(senderWarehouseIndex).recipientWarehouseIndex(recipientWarehouseIndex)
				.redBoxBarcode(redBoxBarcode).newAddress(newAddress).recipientCityName(recipientCityName)
				.recipientArea(recipientArea).recipientAreaRegions(recipientAreaRegions)
				.recipientAddressName(recipientAddressName).recipientHouse(recipientHouse).recipientFlat(recipientFlat)
				.recipientName(recipientName).recipientType(recipientType).settlementType(settlementType)
				.ownershipForm(ownershipForm).recipientContactName(recipientContactName).edrpou(edrpou)
				.backwardDeliveryData(backwardDeliveryData).recipientAddressNote(recipientAddressNote);
	}

	/** One cargo place. {@code packRef} adds packing, {@code Ref} identifies a postomat cell. */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record OptionSeat(
			@JsonProperty("volumetricVolume") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal volumetricVolume,
			@JsonProperty("volumetricWidth") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal volumetricWidth,
			@JsonProperty("volumetricLength") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal volumetricLength,
			@JsonProperty("volumetricHeight") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal volumetricHeight,
			@JsonProperty("weight") @JsonSerialize(using = NpDecimalSerializer.class) @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal weight,
			@JsonProperty("packRef") String packRef,
			@JsonProperty("Ref") String ref
	){

		public OptionSeat(BigDecimal volumetricVolume, BigDecimal volumetricWidth, BigDecimal volumetricLength, BigDecimal volumetricHeight, BigDecimal weight){
			this(volumetricVolume, volumetricWidth, volumetricLength, volumetricHeight, weight, null, null);
		}

		public static OptionSeat ofDimensions(BigDecimal widthCm, BigDecimal lengthCm, BigDecimal heightCm, BigDecimal weightKg){
			return new OptionSeat(null, widthCm, lengthCm, heightCm, weightKg);
		}

		public static OptionSeat ofVolume(BigDecimal volumeM3, BigDecimal weightKg){
			return new OptionSeat(volumeM3, null, null, null, weightKg);
		}

		public OptionSeat withPacking(String packRef){
			return new OptionSeat(volumetricVolume, volumetricWidth, volumetricLength, volumetricHeight, weight, packRef, ref);
		}

		public OptionSeat withCell(String cellRef){
			return new OptionSeat(volumetricVolume, volumetricWidth, volumetricLength, volumetricHeight, weight, packRef, cellRef);
		}
	}

	/**
	 * Backward delivery of documents (documented example:
	 * {@code {"PayerType":"Sender","CargoType":"Documents","Services":{"Attorney":true,"WaybillNewPostWithStamp":true,"UserActions":"UserCallSender"}}}).
	 * For money use {@link InternetDocumentSaveRequest#afterpaymentOnGoodsCost()}.
	 */
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public record BackwardDeliveryData(
			@JsonProperty("PayerType") String payerType,
			@JsonProperty("CargoType") String cargoType,
			@JsonProperty("Services") Map<String, Object> services
	){

	}

	public static class Builder{
		private String ref;
		private String payerType;
		private String paymentMethod;
		private LocalDate dateTime;
		private String cargoType;
		private BigDecimal volumeGeneral;
		private BigDecimal weight;
		private String serviceType;
		private Integer seatsAmount;
		private String description;
		private BigDecimal cost;
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
		private BigDecimal afterpaymentOnGoodsCost;
		private List<OptionSeat> optionsSeat;
		private String senderWarehouseIndex;
		private String recipientWarehouseIndex;
		private String redBoxBarcode;
		private Boolean newAddress;
		private String recipientCityName;
		private String recipientArea;
		private String recipientAreaRegions;
		private String recipientAddressName;
		private String recipientHouse;
		private String recipientFlat;
		private String recipientName;
		private String recipientType;
		private String settlementType;
		private String ownershipForm;
		private String recipientContactName;
		private String edrpou;
		private List<BackwardDeliveryData> backwardDeliveryData;
		private String recipientAddressNote;

		public Builder ref(String ref){ this.ref = ref; return this; }
		public Builder payerType(String payerType){ this.payerType = payerType; return this; }
		public Builder paymentMethod(String paymentMethod){ this.paymentMethod = paymentMethod; return this; }
		public Builder dateTime(LocalDate dateTime){ this.dateTime = dateTime; return this; }
		public Builder cargoType(String cargoType){ this.cargoType = cargoType; return this; }
		public Builder volumeGeneral(BigDecimal volumeGeneral){ this.volumeGeneral = volumeGeneral; return this; }
		public Builder volumeGeneral(double volumeGeneral){ return volumeGeneral(BigDecimal.valueOf(volumeGeneral)); }
		public Builder weight(BigDecimal weight){ this.weight = weight; return this; }
		public Builder weight(double weight){ return weight(BigDecimal.valueOf(weight)); }
		public Builder serviceType(String serviceType){ this.serviceType = serviceType; return this; }
		public Builder seatsAmount(Integer seatsAmount){ this.seatsAmount = seatsAmount; return this; }
		public Builder description(String description){ this.description = description; return this; }
		public Builder cost(BigDecimal cost){ this.cost = cost; return this; }
		public Builder cost(double cost){ return cost(BigDecimal.valueOf(cost)); }
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
		public Builder afterpaymentOnGoodsCost(BigDecimal afterpaymentOnGoodsCost){ this.afterpaymentOnGoodsCost = afterpaymentOnGoodsCost; return this; }
		public Builder afterpaymentOnGoodsCost(double afterpaymentOnGoodsCost){ return afterpaymentOnGoodsCost(BigDecimal.valueOf(afterpaymentOnGoodsCost)); }
		public Builder optionsSeat(List<OptionSeat> optionsSeat){ this.optionsSeat = optionsSeat; return this; }
		public Builder senderWarehouseIndex(String senderWarehouseIndex){ this.senderWarehouseIndex = senderWarehouseIndex; return this; }
		public Builder recipientWarehouseIndex(String recipientWarehouseIndex){ this.recipientWarehouseIndex = recipientWarehouseIndex; return this; }
		public Builder redBoxBarcode(String redBoxBarcode){ this.redBoxBarcode = redBoxBarcode; return this; }
		public Builder newAddress(Boolean newAddress){ this.newAddress = newAddress; return this; }
		public Builder recipientCityName(String recipientCityName){ this.recipientCityName = recipientCityName; return this; }
		public Builder recipientArea(String recipientArea){ this.recipientArea = recipientArea; return this; }
		public Builder recipientAreaRegions(String recipientAreaRegions){ this.recipientAreaRegions = recipientAreaRegions; return this; }
		public Builder recipientAddressName(String recipientAddressName){ this.recipientAddressName = recipientAddressName; return this; }
		public Builder recipientHouse(String recipientHouse){ this.recipientHouse = recipientHouse; return this; }
		public Builder recipientFlat(String recipientFlat){ this.recipientFlat = recipientFlat; return this; }
		public Builder recipientName(String recipientName){ this.recipientName = recipientName; return this; }
		public Builder recipientType(String recipientType){ this.recipientType = recipientType; return this; }
		public Builder settlementType(String settlementType){ this.settlementType = settlementType; return this; }
		public Builder ownershipForm(String ownershipForm){ this.ownershipForm = ownershipForm; return this; }
		public Builder recipientContactName(String recipientContactName){ this.recipientContactName = recipientContactName; return this; }
		public Builder edrpou(String edrpou){ this.edrpou = edrpou; return this; }
		public Builder backwardDeliveryData(List<BackwardDeliveryData> backwardDeliveryData){ this.backwardDeliveryData = backwardDeliveryData; return this; }
		public Builder recipientAddressNote(String recipientAddressNote){ this.recipientAddressNote = recipientAddressNote; return this; }

		public Builder payerType(com.novapost.model.enums.PayerType payerType){ return payerType(payerType.value()); }
		public Builder paymentMethod(com.novapost.model.enums.PaymentMethod paymentMethod){ return paymentMethod(paymentMethod.value()); }
		public Builder serviceType(com.novapost.model.enums.ServiceType serviceType){ return serviceType(serviceType.value()); }
		public Builder cargoType(com.novapost.model.enums.CargoType cargoType){ return cargoType(cargoType.value()); }
		public Builder recipientType(com.novapost.model.enums.CounterpartyType recipientType){ return recipientType(recipientType.value()); }

		public Builder addOptionSeat(OptionSeat seat){
			if(this.optionsSeat == null){
				this.optionsSeat = new ArrayList<>();
			}
			this.optionsSeat.add(seat);
			return this;
		}

		public Builder addBackwardDelivery(BackwardDeliveryData data){
			if(this.backwardDeliveryData == null){
				this.backwardDeliveryData = new ArrayList<>();
			}
			this.backwardDeliveryData.add(data);
			return this;
		}

		/** Delivery to a postomat: {@code RecipientWarehouseIndex} is the postomat's digital address (for example {@code 11/3002}). */
		public Builder toPostomat(String recipientWarehouseIndex){
			this.recipientWarehouseIndex = recipientWarehouseIndex;
			return this;
		}

		/** Sending from a postomat: {@code SenderWarehouseIndex} is the postomat's digital address; give the cell through {@link OptionSeat#withCell}. */
		public Builder fromPostomat(String senderWarehouseIndex){
			this.senderWarehouseIndex = senderWarehouseIndex;
			return this;
		}

		/**
		 * Recipient given as text, to a branch ({@code NewAddress=1}, branch number in {@code RecipientAddressName}); the area,
		 * region, house and flat are sent empty as in the documented example.
		 *
		 * @param recipientType {@code PrivatePerson} or {@code Organization}
		 * @param settlementType for example {@code місто} / {@code м.}
		 */
		public Builder recipientBranchByText(String cityName, String branchNumber, String recipientName, String recipientType, String settlementType){
			this.newAddress = Boolean.TRUE;
			this.recipientCityName = cityName;
			this.recipientArea = "";
			this.recipientAreaRegions = "";
			this.recipientAddressName = branchNumber;
			this.recipientHouse = "";
			this.recipientFlat = "";
			this.recipientName = recipientName;
			this.recipientType = recipientType;
			this.settlementType = settlementType;
			return this;
		}

		/** Recipient given as text, to an address ({@code NewAddress=1}, street in {@code RecipientAddressName}). */
		public Builder recipientAddressByText(String cityName, String area, String areaRegions, String street, String house,
		                                      String flat, String recipientName, String recipientType, String settlementType){
			this.newAddress = Boolean.TRUE;
			this.recipientCityName = cityName;
			this.recipientArea = area;
			this.recipientAreaRegions = areaRegions;
			this.recipientAddressName = street;
			this.recipientHouse = house;
			this.recipientFlat = flat;
			this.recipientName = recipientName;
			this.recipientType = recipientType;
			this.settlementType = settlementType;
			return this;
		}

		/** For an organization recipient given as text: EDRPOU code, ownership form Ref and contact name. */
		public Builder organizationRecipient(String edrpou, String ownershipFormRef, String contactName){
			this.edrpou = edrpou;
			this.ownershipForm = ownershipFormRef;
			this.recipientContactName = contactName;
			return this;
		}

		public InternetDocumentSaveRequest build(){
			return new InternetDocumentSaveRequest(
					ref, payerType, paymentMethod, dateTime, cargoType, volumeGeneral, weight,
					serviceType, seatsAmount, description, cost, citySender, sender,
					senderAddress, contactSender, sendersPhone, cityRecipient, recipient,
					recipientAddress, contactRecipient, recipientsPhone, afterpaymentOnGoodsCost,
					optionsSeat, senderWarehouseIndex, recipientWarehouseIndex, redBoxBarcode, newAddress,
					recipientCityName, recipientArea, recipientAreaRegions, recipientAddressName, recipientHouse,
					recipientFlat, recipientName, recipientType, settlementType, ownershipForm,
					recipientContactName, edrpou, backwardDeliveryData, recipientAddressNote
			);
		}
	}
}
