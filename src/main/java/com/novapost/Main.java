package com.novapost;

import com.novapost.client.NovaPostClient;
import com.novapost.config.AppConfig;
import com.novapost.controller.NovaPostController;
import com.novapost.db.DatabaseConfig;
import com.novapost.db.DatabaseManager;
import com.novapost.service.NovaPostSyncService;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.novapost.config.AppConfig.getConfig;

public class Main{

	public static void main(String[] args) throws SQLException, IOException, InterruptedException{
		Pack pack = getPack(args);
		// Demonstrating dynamic field projections directly from JSON:
		demonstrateMapProjection(pack.controller());
		demonstrateClassProjection(pack.controller());
	}

	/**
	 * Demonstrates Map-based projection directly from raw JSON:
	 * Only the requested keys are retrieved, saving CPU and memory.
	 */
	public static void demonstrateMapProjection(NovaPostController controller){
		System.out.println("\n--- Demonstration: Map Projection Directly from JSON ---");
		Map<String, Object> fields = new HashMap<>();
		fields.put("Cost", null);
		fields.put("IntDocNumber", null);
		fields.put("RecipientAddressDescription", null);

		List<Map<String, Object>> results = controller.getWaybills(
				LocalDate.now().minusDays(30),
				LocalDate.now(),
				1,
				5,
				fields
		);

		System.out.println("Retrieved " + results.size() + " waybills (Map projection):");
		for(Map<String, Object> waybill : results){
			System.out.println("  " + waybill);
		}
	}

	/**
	 * Custom target record declaring only the desired fields.
	 * Any field in Nova Post's JSON is automatically parsed and type-converted.
	 */
	public record WaybillCostSummary(
			String intDocNumber,
			double cost,
			String recipientAddressDescription
	){}

	/**
	 * Demonstrates Class/Record-based projection directly from raw JSON:
	 * Only the target record is instantiated directly from JSON payload.
	 */
	public static void demonstrateClassProjection(NovaPostController controller){
		System.out.println("\n--- Demonstration: Class Projection Directly from JSON ---");
		List<WaybillCostSummary> results = controller.getWaybills(
				LocalDate.now().minusDays(30),
				LocalDate.now(),
				1,
				5,
				WaybillCostSummary.class
		);

		System.out.println("Retrieved " + results.size() + " waybills (Class projection):");
		for(WaybillCostSummary waybill : results){
			System.out.println("  TTN: " + waybill.intDocNumber() + ", Cost: " + waybill.cost()
					+ ", Address: " + waybill.recipientAddressDescription());
		}
	}

	private static Pack getPack(String[] args){
		AppConfig cfg = init(args);
		String apiKey = cfg.apiKey();
		String url = cfg.dbUrl();
		String user = cfg.dbUser();
		String password = cfg.dbPassword();

		NovaPostClient client = new NovaPostClient(apiKey);
		DatabaseConfig databaseConfig = new DatabaseConfig(url, user, password);
		DatabaseManager dbManager = new DatabaseManager(databaseConfig);
		NovaPostController controller = new NovaPostController(client);
		NovaPostSyncService syncService = new NovaPostSyncService(client, dbManager);
		return new Pack(client, dbManager, controller, syncService);
	}

	private record Pack(NovaPostClient client, DatabaseManager dbManager, NovaPostController controller,
	                    NovaPostSyncService syncService){

	}

	private static AppConfig init(String[] args){
		AppConfig config = getConfig(args);
		if(config == null) throw new IllegalStateException("No config found!");

		System.out.println("Starting Nova Post Sync with configuration:");
		System.out.println(" - DB URL: " + config.dbUrl());
		System.out.println(" - DB User: " + config.dbUser());
		System.out.println(" - API URL: " + config.apiUrl());
		System.out.println(" - Page delay: " + config.pageDelayMs() + "ms");
		return config;
	}
}
