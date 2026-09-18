package com.novapost.service;

import com.novapost.client.NovaPostClient;
import com.novapost.db.DatabaseManager;
import com.novapost.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class NovaPostSyncService{

	private static final int SETTLEMENT_PAGE_LIMIT = 150;
	private static final int WAREHOUSE_PAGE_LIMIT = 500;

	private final NovaPostClient client;
	private final DatabaseManager databaseManager;
	private final long pageDelayMs;

	public NovaPostSyncService(NovaPostClient client, DatabaseManager databaseManager){
		this(client, databaseManager, com.novapost.config.AppConfig.DEFAULT_PAGE_DELAY_MS);
	}

	public NovaPostSyncService(NovaPostClient client, DatabaseManager databaseManager, long pageDelayMs){
		this.client = client;
		this.databaseManager = databaseManager;
		this.pageDelayMs = Math.max(0, pageDelayMs);
	}

	public SyncResult syncAll() throws SQLException, IOException, InterruptedException{
		System.out.println("Starting database initialization and table setup...");
		databaseManager.initializeTables();

		System.out.println("Truncating tables (nova_post_settlements, nova_post_warehouses)...");
		databaseManager.truncateTables();

		int totalSettlements = syncSettlements();
		int totalWarehouses = syncWarehouses();

		System.out.println("Sync finished successfully. Settlements: " + totalSettlements + ", Warehouses: " + totalWarehouses);
		return new SyncResult(totalSettlements, totalWarehouses);
	}

	public int syncSettlements() throws IOException, InterruptedException, SQLException{
		System.out.println("Fetching settlements from Nova Post API...");
		int page = 1;
		int totalInserted = 0;

		while(true){
			SettlementFilter filter = new SettlementFilter(null, null, null, page, SETTLEMENT_PAGE_LIMIT, null);
			NpResponse<Settlement> response = client.getSettlements(filter);

			if(response.hasErrors()){
				throw new IOException("API error fetching settlements at page " + page + ": " + response.errors());
			}

			List<Settlement> list = response.data();
			if(list == null || list.isEmpty()){
				break;
			}

			int inserted = databaseManager.insertSettlements(list);
			totalInserted += inserted;
			System.out.println("Settlements page " + page + " inserted: " + inserted + " (total so far: " + totalInserted + ")");

			if(list.size() < SETTLEMENT_PAGE_LIMIT){
				break;
			}
			page++;
			if(pageDelayMs > 0){
				Thread.sleep(pageDelayMs);
			}
		}

		return totalInserted;
	}

	public int syncWarehouses() throws IOException, InterruptedException, SQLException{
		System.out.println("Fetching warehouses from Nova Post API...");
		int page = 1;
		int totalInserted = 0;

		while(true){
			WarehouseFilter filter = new WarehouseFilter(null, null, null, null, null, null, page, WAREHOUSE_PAGE_LIMIT, null);
			NpResponse<Warehouse> response = client.getWarehouses(filter);

			if(response.hasErrors()){
				throw new IOException("API error fetching warehouses at page " + page + ": " + response.errors());
			}

			List<Warehouse> list = response.data();
			if(list == null || list.isEmpty()){
				break;
			}

			int inserted = databaseManager.insertWarehouses(list);
			totalInserted += inserted;
			System.out.println("Warehouses page " + page + " inserted: " + inserted + " (total so far: " + totalInserted + ")");

			if(list.size() < WAREHOUSE_PAGE_LIMIT){
				break;
			}
			page++;
			if(pageDelayMs > 0){
				Thread.sleep(pageDelayMs);
			}
		}

		return totalInserted;
	}

	public record SyncResult(int settlementsCount, int warehousesCount){

	}
}
