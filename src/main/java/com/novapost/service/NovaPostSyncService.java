package com.novapost.service;

import com.novapost.client.NovaPostClient;
import com.novapost.db.DatabaseManager;
import com.novapost.model.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class NovaPostSyncService{

	private static final int SETTLEMENT_PAGE_LIMIT = 150;
	private static final int WAREHOUSE_PAGE_LIMIT = 500;
	private static final int DOCUMENT_PAGE_LIMIT = 100;

	private final NovaPostClient client;
	private final DatabaseManager databaseManager;
	private final WaybillFetcherService waybillFetcherService;
	private final long pageDelayMs;

	public NovaPostSyncService(NovaPostClient client, DatabaseManager databaseManager){
		this(client, databaseManager, com.novapost.config.AppConfig.DEFAULT_PAGE_DELAY_MS);
	}

	public NovaPostSyncService(NovaPostClient client, DatabaseManager databaseManager, long pageDelayMs){
		this(client, databaseManager, new WaybillFetcherService(client, pageDelayMs), pageDelayMs);
	}

	public NovaPostSyncService(NovaPostClient client, DatabaseManager databaseManager, WaybillFetcherService waybillFetcherService){
		this(client, databaseManager, waybillFetcherService, waybillFetcherService != null ? waybillFetcherService.getThrottleDelayMs() : com.novapost.config.AppConfig.DEFAULT_PAGE_DELAY_MS);
	}

	public NovaPostSyncService(NovaPostClient client, DatabaseManager databaseManager, WaybillFetcherService waybillFetcherService, long pageDelayMs){
		this.client = client;
		this.databaseManager = databaseManager;
		this.pageDelayMs = Math.max(0, pageDelayMs);
		this.waybillFetcherService = waybillFetcherService != null ? waybillFetcherService : new WaybillFetcherService(client, pageDelayMs);
	}

	public SyncResult syncAll() throws SQLException, IOException, InterruptedException{
		return syncAll(0);
	}

	public SyncResult syncAll(int documentDaysBack) throws SQLException, IOException, InterruptedException{
		System.out.println("Starting database initialization and table setup...");
		databaseManager.initializeTables();

		System.out.println("Truncating tables (nova_post_settlements, nova_post_warehouses, nova_post_internet_documents)...");
		databaseManager.truncateTables();

		int totalSettlements = syncSettlements();
		int totalWarehouses = syncWarehouses();
		int totalDocs = 0;
		if(documentDaysBack > 0){
			totalDocs = syncInternetDocuments(documentDaysBack);
		}

		System.out.println("Sync finished successfully. Settlements: " + totalSettlements + ", Warehouses: " + totalWarehouses + ", Documents: " + totalDocs);
		return new SyncResult(totalSettlements, totalWarehouses, totalDocs);
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

	public int syncInternetDocuments(LocalDate from, LocalDate to) throws SQLException{
		System.out.println("Fetching and syncing internet documents (waybills) from Nova Post API for range " + from + " to " + to + "...");
		int[] totalInserted = new int[]{0};
		int[] batchIndex = new int[]{0};

		try {
			waybillFetcherService.fetchAllDocuments(from, to, batch -> {
				try {
					int inserted = databaseManager.insertInternetDocuments(batch);
					totalInserted[0] += inserted;
					batchIndex[0]++;
					System.out.println("Internet documents batch #" + batchIndex[0] + " inserted: " + inserted + " (total so far: " + totalInserted[0] + ")");
				} catch (SQLException e) {
					throw new RuntimeException("Database error saving internet documents batch #" + batchIndex[0] + ": " + e.getMessage(), e);
				}
			});
		} catch (RuntimeException e) {
			if (e.getCause() instanceof SQLException sqlEx) {
				throw sqlEx;
			}
			throw e;
		}

		return totalInserted[0];
	}

	public int syncInternetDocuments(int daysBack) throws SQLException{
		LocalDate to = LocalDate.now();
		LocalDate from = to.minusDays(Math.max(0, daysBack));
		return syncInternetDocuments(from, to);
	}

	public record SyncResult(int settlementsCount, int warehousesCount, int internetDocumentsCount){
		public SyncResult(int settlementsCount, int warehousesCount){
			this(settlementsCount, warehousesCount, 0);
		}
	}
}
