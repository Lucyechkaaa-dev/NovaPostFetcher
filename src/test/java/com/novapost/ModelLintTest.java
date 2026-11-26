package com.novapost;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.RecordComponent;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/** Catches typos in {@code @JsonProperty} names (such as {@code "Description "} with a trailing space) in every model record. */
class ModelLintTest{

	private static List<Class<?>> modelClasses() throws IOException, URISyntaxException{
		Path root = Path.of(ModelLintTest.class.getResource("/").toURI()).resolveSibling("classes");
		Path modelDir = root.resolve("com/novapost/model");
		assertTrue(Files.isDirectory(modelDir), "compiled model classes not found at " + modelDir);
		List<Class<?>> classes = new ArrayList<>();
		try(Stream<Path> files = Files.walk(modelDir)){
			for(Path p : files.filter(f -> f.toString().endsWith(".class")).toList()){
				String name = root.relativize(p).toString().replace('\\', '/').replace('/', '.');
				name = name.substring(0, name.length() - ".class".length());
				try{
					classes.add(Class.forName(name));
				}
				catch(ClassNotFoundException | LinkageError e){
					fail("Cannot load " + name + ": " + e);
				}
			}
		}
		return classes;
	}

	@Test
	void jsonPropertyNamesAreCleanAndUnique() throws Exception{
		List<String> problems = new ArrayList<>();
		int records = 0, properties = 0;
		for(Class<?> c : modelClasses()){
			if(!c.isRecord()){
				continue;
			}
			records++;
			Set<String> seen = new HashSet<>();
			for(RecordComponent rc : c.getRecordComponents()){
				JsonProperty p = c.getDeclaredField(rc.getName()).getAnnotation(JsonProperty.class);
				if(p == null){
					continue;
				}
				properties++;
				String n = p.value();
				if(n.isEmpty() || !n.equals(n.strip()) || n.chars().anyMatch(Character::isWhitespace)){
					problems.add(c.getName() + "." + rc.getName() + " has a malformed JSON name [" + n + "]");
				}
				if(!seen.add(n)){
					problems.add(c.getName() + " declares " + n + " twice");
				}
			}
		}
		System.out.println("Model lint: " + records + " records, " + properties + " JSON properties");
		assertTrue(problems.isEmpty(), String.join("\n", problems));
		assertTrue(records > 100, "expected the generated models to be found, got " + records);
		assertTrue(properties > 900, "expected the JSON properties to be found, got " + properties);
	}

	/** Properties that are on/off flags in every model that has them (Nova Poshta writes them as 1/0, true/false, "true"...). */
	private static final Set<String> FLAGS = Set.of("AviaDelivery", "BicycleParking", "ChangedDataEW", "DeletionMark", "DeliveryByHand", "DeliveryPharma",
			"DenyToSelect", "ElevatorRecipient", "ExpressPallet", "FillingWarranty", "InternationalShipping", "IsBranch", "IsTakeAttorney", "LightReturn",
			"OnlyReceivingParcel", "PaymentAccess", "POSTerminal", "PostFinance", "Posted", "PreventEntryNewStreetsUser", "Printed", "Redelivery",
			"SameDayDelivery", "SaturdayDelivery", "ScanSheetPrinted", "SecurePayment", "Vip", "WarehouseForAgent", "WarehouseIllusha",
			"bCardPaymentCancelled", "bCardPaymentPayed", "AgeIdentification", "Delivery1", "Delivery2", "Delivery3", "Delivery4", "Delivery5",
			"Delivery6", "Delivery7", "IsPickupTable", "NewAddress", "PackForSale");

	/** Whole numbers (counts, pages, codes that are numbers). */
	private static final Set<String> WHOLE_NUMBERS = Set.of("Page", "Limit", "SeatsAmount", "PackCount", "Count", "ForwardingCount", "NumberOfFloorsLifting",
			"SelfServiceWorkplacesCount", "DaysStorageCargo", "CityID", "StateId", "StatePayId", "TzoneID");

	/** Money, weight, size and coordinates: one decimal type for all of them. */
	private static final Set<String> DECIMALS = Set.of("AfterpaymentOnGoodsCost", "AfterpaymentSum", "AmountPaid", "AmountToPay", "AnnouncedPrice",
			"BackwardDeliveryMoney", "BackwardDeliverySum", "Cost", "CostOnSite", "DeliveryCost", "DocumentCost", "ExpressWaybillAmountToPay",
			"LastAmountReceivedCommissionGM", "LastAmountTransferGM", "MaxDeclaredCost", "RedeliverySum", "StorageAmount", "StoragePrice",
			"SumBeforeCheckWeight", "CalculatedWeight", "CheckWeight", "DocumentWeight", "FactualWeight", "Height", "Length", "PlaceMaxWeightAllowed",
			"PlanedWeight", "TotalMaxWeightAllowed", "VolumeGeneral", "VolumeWeight", "VolumetricWeight", "Weight", "Width", "weight",
			"volumetricHeight", "volumetricLength", "volumetricWidth", "volumetricVolume", "Latitude", "Longitude", "AssessedCost", "CostPack",
			"CostRedelivery", "lat", "lon");

	@Test
	void fieldsWithAnObviousMeaningAreNeverStrings() throws Exception{
		List<String> problems = new ArrayList<>();
		for(Class<?> c : modelClasses()){
			if(!c.isRecord()){
				continue;
			}
			for(RecordComponent rc : c.getRecordComponents()){
				JsonProperty p = c.getDeclaredField(rc.getName()).getAnnotation(JsonProperty.class);
				if(p == null || rc.getType() != String.class){
					continue;
				}
				String n = p.value();
				boolean flag = FLAGS.contains(n);
				boolean whole = WHOLE_NUMBERS.contains(n);
				boolean decimal = DECIMALS.contains(n);
				// 'Warehouse' is a flag only in a settlement; a TTN, a phone or a code stays text on purpose
				if(flag || whole || decimal){
					problems.add(c.getSimpleName() + "." + n + " is a String but means a " + (flag ? "flag (Boolean)" : whole ? "whole number (Integer)" : "decimal (BigDecimal)"));
				}
			}
		}
		assertTrue(problems.isEmpty(), "a typed field went back to String (change its token in api-spec.txt and regenerate):\n" + String.join("\n", problems));
	}
}
