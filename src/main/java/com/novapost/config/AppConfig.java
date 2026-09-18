package com.novapost.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record AppConfig(
		String apiKey,
		String dbUrl,
		String dbUser,
		String dbPassword,
		String apiUrl,
		long pageDelayMs,
		boolean helpRequested
){

	public static final String DEFAULT_API_URL = "https://api.novaposhta.ua/v2.0/json/";
	public static final long DEFAULT_PAGE_DELAY_MS = 750;

	public static AppConfig fromArgs(String[] args){
		Map<String, String> parsed = new HashMap<>();
		boolean help = false;
		String positionalApiKey = null;

		if(args != null){
			for(int i = 0; i < args.length; i++){
				String arg = args[i];
				if(arg == null || arg.isBlank()){
					continue;
				}

				if(arg.equals("--help") || arg.equals("-h")){
					help = true;
					continue;
				}

				if(arg.startsWith("--") && arg.contains("=")){
					int eq = arg.indexOf('=');
					String key = arg.substring(2, eq).toLowerCase().trim();
					String val = arg.substring(eq + 1).trim();
					parsed.put(key, val);
				}
				else if(arg.startsWith("--") && i + 1 < args.length && !args[i + 1].startsWith("-")){
					String key = arg.substring(2).toLowerCase().trim();
					parsed.put(key, args[++i].trim());
				}
				else if(arg.startsWith("-") && i + 1 < args.length && !args[i + 1].startsWith("-")){
					String flag = arg.substring(1).toLowerCase().trim();
					switch(flag){
						case "k" -> parsed.put("api-key", args[++i].trim());
						case "u" -> parsed.put("db-user", args[++i].trim());
						case "p" -> parsed.put("db-password", args[++i].trim());
						case "d" -> parsed.put("delay", args[++i].trim());
						default -> {
						}
					}
				}
				else if(!arg.startsWith("-") && positionalApiKey == null){
					positionalApiKey = arg.trim();
				}
			}
		}

		String apiKey = parsed.getOrDefault("api-key", parsed.getOrDefault("apikey", positionalApiKey));
		String dbUrl = parsed.getOrDefault("db-url", parsed.get("dburl"));
		String dbUser = parsed.getOrDefault("db-user", parsed.get("dbuser"));
		String dbPassword = parsed.getOrDefault("db-password", parsed.get("dbpassword"));

		String apiUrl = parsed.getOrDefault("api-url", parsed.get("apiurl"));
		if(apiUrl == null || apiUrl.isBlank()){
			apiUrl = DEFAULT_API_URL;
		}

		String delayStr = parsed.getOrDefault("delay", parsed.get("page-delay"));
		long delay = DEFAULT_PAGE_DELAY_MS;
		if(delayStr != null && !delayStr.isBlank()){
			try{
				delay = Long.parseLong(delayStr);
			}
			catch(NumberFormatException ignored){
			}
		}

		return new AppConfig(apiKey, dbUrl, dbUser, dbPassword, apiUrl, delay, help);
	}

	public static AppConfig getConfig(String[] args){
		AppConfig config = AppConfig.fromArgs(args);

		if(config.helpRequested()){
			AppConfig.printUsage();
			return null;
		}

		List<String> missingArgs = new ArrayList<>();
		if(config.apiKey() == null || config.apiKey().isBlank()){
			missingArgs.add("--api-key (or -k)");
		}
		if(config.dbUrl() == null || config.dbUrl().isBlank()){
			missingArgs.add("--db-url");
		}
		if(config.dbUser() == null || config.dbUser().isBlank()){
			missingArgs.add("--db-user (or -u)");
		}
		if(config.dbPassword() == null){
			missingArgs.add("--db-password (or -p)");
		}

		if(!missingArgs.isEmpty()){
			System.err.println("Error: Missing required program arguments: " + String.join(", ", missingArgs));
			System.err.println();
			AppConfig.printUsage();
			System.exit(1);
		}
		return config;
	}

	public static void printUsage(){
		System.out.println("""
				NovaPostFetcher Usage:
				  java -jar NovaPostFetcher.jar [options]
				
				Required Options:
				  --api-key=<KEY>, -k <KEY>       Nova Post API Key
				  --db-url=<URL>                  JDBC URL (e.g. jdbc:mysql://localhost:3306/scheme)
				  --db-user=<USER>, -u <USER>     Database username
				  --db-password=<PWD>, -p <PWD>   Database password
				
				Optional:
				  --delay=<MS>, -d <MS>           Inter-page rate limit delay in ms (default: 750)
				  --api-url=<URL>                 Nova Post API URL (default: https://api.novaposhta.ua/v2.0/json/)
				  --help, -h                      Show this help message
				""");
	}
}
