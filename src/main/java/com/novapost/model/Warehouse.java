package com.novapost.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;

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
    @JsonProperty("Longitude") String longitude,
    @JsonProperty("Latitude") String latitude,
    @JsonProperty("PostFinance") String postFinance,
    @JsonProperty("BicycleParking") String bicycleParking,
    @JsonProperty("PaymentAccess") String paymentAccess,
    @JsonProperty("POSTerminal") String posTerminal,
    @JsonProperty("InternationalShipping") String internationalShipping,
    @JsonProperty("SelfServiceWorkplacesCount") String selfServiceWorkplacesCount,
    @JsonProperty("TotalMaxWeightAllowed") String totalMaxWeightAllowed,
    @JsonProperty("PlaceMaxWeightAllowed") String placeMaxWeightAllowed,
    @JsonProperty("WarehouseStatus") String warehouseStatus,
    @JsonProperty("WarehouseStatusDate") String warehouseStatusDate,
    @JsonProperty("CategoryOfWarehouse") String categoryOfWarehouse,
    @JsonProperty("RegionCity") String regionCity,
    @JsonProperty("Direct") String direct,
    @JsonProperty("DistrictCode") String districtCode,
    @JsonProperty("WarehouseIndex") String warehouseIndex,
    @JsonProperty("Schedule") Map<String, String> schedule,
    @JsonProperty("SendingLimitationsOnDimensions") Map<String, Object> sendingLimitationsOnDimensions,
    @JsonProperty("ReceivingLimitationsOnDimensions") Map<String, Object> receivingLimitationsOnDimensions
) {}
