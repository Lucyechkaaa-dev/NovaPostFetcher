package com.novapost;

import com.novapost.config.AppConfig;

import static com.novapost.config.AppConfig.getConfig;

public class Main{

	public static void main(String[] args){
		init(args);

		/*
		 *  Code
		 */
	}

	private static void init(String[] args){
		AppConfig config = getConfig(args);
		if(config == null) return;

		System.out.println("Starting Nova Post Sync with configuration:");
		System.out.println(" - DB URL: " + config.dbUrl());
		System.out.println(" - DB User: " + config.dbUser());
		System.out.println(" - API URL: " + config.apiUrl());
		System.out.println(" - Page delay: " + config.pageDelayMs() + "ms");
	}
}
