package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.novapost.model.support.NpBooleanDeserializer;
import com.novapost.model.support.NpDateTimeDeserializer;
import com.novapost.model.support.NpDecimalDeserializer;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.novapost.model.support.NpIntegerDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Warehouse(
		@JsonProperty("SiteKey") String siteKey,
		@JsonProperty("Description") String description,
		@JsonProperty("DescriptionRu") String descriptionRu,
		@JsonProperty("ShortAddress") String shortAddress,
		@JsonProperty("ShortAddressRu") String shortAddressRu,
		@JsonProperty("Phone") String phone,
		@JsonProperty("TypeOfWarehouse") String typeOfWarehouse,
		@JsonProperty("Ref") String ref,
		@JsonProperty("Number") String number,
		@JsonProperty("CityRef") String cityRef,
		@JsonProperty("CityDescription") String cityDescription,
		@JsonProperty("CityDescriptionRu") String cityDescriptionRu,
		@JsonProperty("SettlementRef") String settlementRef,
		@JsonProperty("SettlementDescription") String settlementDescription,
		@JsonProperty("SettlementAreaDescription") String settlementAreaDescription,
		@JsonProperty("SettlementRegionsDescription") String settlementRegionsDescription,
		@JsonProperty("SettlementTypeDescription") String settlementTypeDescription,
		@JsonProperty("Longitude") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal longitude,
		@JsonProperty("Latitude") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal latitude,
		@JsonProperty("PostFinance") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean postFinance,
		@JsonProperty("BicycleParking") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean bicycleParking,
		@JsonProperty("PaymentAccess") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean paymentAccess,
		@JsonProperty("POSTerminal") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean posTerminal,
		@JsonProperty("InternationalShipping") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean internationalShipping,
		@JsonProperty("SelfServiceWorkplacesCount") @JsonDeserialize(using = NpIntegerDeserializer.class) Integer selfServiceWorkplacesCount,
		@JsonProperty("TotalMaxWeightAllowed") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal totalMaxWeightAllowed,
		@JsonProperty("PlaceMaxWeightAllowed") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal placeMaxWeightAllowed,
		@JsonProperty("WarehouseStatus") String warehouseStatus,
		@JsonProperty("WarehouseStatusDate") @JsonDeserialize(using = NpDateTimeDeserializer.class) LocalDateTime warehouseStatusDate,
		@JsonProperty("CategoryOfWarehouse") String categoryOfWarehouse,
		@JsonProperty("RegionCity") String regionCity,
		@JsonProperty("Direct") String direct,
		@JsonProperty("DistrictCode") String districtCode,
		@JsonProperty("WarehouseIndex") String warehouseIndex,
		@JsonProperty("Schedule") Map<String, String> schedule,
		@JsonProperty("SendingLimitationsOnDimensions") Map<String, Object> sendingLimitationsOnDimensions,
		@JsonProperty("ReceivingLimitationsOnDimensions") Map<String, Object> receivingLimitationsOnDimensions,
		@JsonProperty("SettlementTypeDescriptionRu") String settlementTypeDescriptionRu,
		@JsonProperty("Reception") Map<String, Object> reception,
		@JsonProperty("Delivery") Map<String, Object> delivery,
		@JsonProperty("WarehouseForAgent") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean warehouseForAgent,
		@JsonProperty("MaxDeclaredCost") @JsonDeserialize(using = NpDecimalDeserializer.class) BigDecimal maxDeclaredCost,
		@JsonProperty("DenyToSelect") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean denyToSelect,
		@JsonProperty("PostMachineType") String postMachineType,
		@JsonProperty("PostalCodeUA") String postalCodeUA,
		@JsonProperty("OnlyReceivingParcel") @JsonDeserialize(using = NpBooleanDeserializer.class) Boolean onlyReceivingParcel
){

	/** The shape before the fields from the current documentation were added. */
	public Warehouse(String siteKey, String description, String descriptionRu, String shortAddress, String shortAddressRu,
	                 String phone, String typeOfWarehouse, String ref, String number, String cityRef, String cityDescription,
	                 String cityDescriptionRu, String settlementRef, String settlementDescription,
	                 String settlementAreaDescription, String settlementRegionsDescription, String settlementTypeDescription,
	                 BigDecimal longitude, BigDecimal latitude, Boolean postFinance, Boolean bicycleParking, Boolean paymentAccess,
	                 Boolean posTerminal, Boolean internationalShipping, Integer selfServiceWorkplacesCount,
	                 BigDecimal totalMaxWeightAllowed, BigDecimal placeMaxWeightAllowed, String warehouseStatus,
	                 LocalDateTime warehouseStatusDate, String categoryOfWarehouse, String regionCity, String direct,
	                 String districtCode, String warehouseIndex, Map<String, String> schedule,
	                 Map<String, Object> sendingLimitationsOnDimensions, Map<String, Object> receivingLimitationsOnDimensions){
		this(siteKey, description, descriptionRu, shortAddress, shortAddressRu, phone, typeOfWarehouse, ref, number, cityRef,
				cityDescription, cityDescriptionRu, settlementRef, settlementDescription, settlementAreaDescription,
				settlementRegionsDescription, settlementTypeDescription, longitude, latitude, postFinance, bicycleParking,
				paymentAccess, posTerminal, internationalShipping, selfServiceWorkplacesCount, totalMaxWeightAllowed,
				placeMaxWeightAllowed, warehouseStatus, warehouseStatusDate, categoryOfWarehouse, regionCity, direct,
				districtCode, warehouseIndex, schedule, sendingLimitationsOnDimensions, receivingLimitationsOnDimensions,
				null, null, null, null, null, null, null, null, null);
	}
}
