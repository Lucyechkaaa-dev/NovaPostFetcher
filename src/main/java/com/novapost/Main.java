package com.novapost;

import com.novapost.client.NovaPostClient;
import com.novapost.config.AppConfig;
import com.novapost.db.DatabaseConfig;
import com.novapost.db.DatabaseManager;
import com.novapost.service.NovaPostSyncService;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        AppConfig config = AppConfig.fromArgs(args);

        if (config.helpRequested()) {
            AppConfig.printUsage();
            return;
        }

        List<String> missingArgs = new ArrayList<>();
        if (config.apiKey() == null || config.apiKey().isBlank()) {
            missingArgs.add("--api-key (or -k)");
        }
        if (config.dbUrl() == null || config.dbUrl().isBlank()) {
            missingArgs.add("--db-url");
        }
        if (config.dbUser() == null || config.dbUser().isBlank()) {
            missingArgs.add("--db-user (or -u)");
        }
        if (config.dbPassword() == null) {
            missingArgs.add("--db-password (or -p)");
        }

        if (!missingArgs.isEmpty()) {
            System.err.println("Error: Missing required program arguments: " + String.join(", ", missingArgs));
            System.err.println();
            AppConfig.printUsage();
            System.exit(1);
        }

        try {
            System.out.println("Starting Nova Post Sync with configuration:");
            System.out.println(" - DB URL: " + config.dbUrl());
            System.out.println(" - DB User: " + config.dbUser());
            System.out.println(" - API URL: " + config.apiUrl());
            System.out.println(" - Page delay: " + config.pageDelayMs() + "ms");

            NovaPostClient client = new NovaPostClient(config.apiKey(), config.apiUrl(),
                    java.net.http.HttpClient.newBuilder().connectTimeout(java.time.Duration.ofSeconds(15)).build(),
                    NovaPostClient.createDefaultMapper());

            DatabaseConfig dbConfig = new DatabaseConfig(config.dbUrl(), config.dbUser(), config.dbPassword());
            DatabaseManager databaseManager = new DatabaseManager(dbConfig);
            NovaPostSyncService syncService = new NovaPostSyncService(client, databaseManager, config.pageDelayMs());

            NovaPostSyncService.SyncResult result = syncService.syncAll();
            System.out.println("Sync complete. Inserted " + result.settlementsCount() + " settlements and " + result.warehousesCount() + " warehouses.");
        } catch (Exception e) {
            System.err.println("Sync failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
