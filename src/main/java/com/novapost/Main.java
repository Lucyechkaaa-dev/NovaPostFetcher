package com.novapost;

import com.novapost.client.NovaPostClient;
import com.novapost.config.AppConfig;
import com.novapost.controller.NovaPostController;
import com.novapost.db.DatabaseConfig;
import com.novapost.db.DatabaseManager;
import com.novapost.service.NovaPostSyncService;

import java.io.IOException;
import java.sql.SQLException;

import static com.novapost.config.AppConfig.getConfig;

public class Main{

	public static void main(String[] args) throws SQLException, IOException, InterruptedException{
		Pack pack = getPack(args);
		pack.dbManager().initializeTables();

		pack.syncService().syncInternetDocuments(90);
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
