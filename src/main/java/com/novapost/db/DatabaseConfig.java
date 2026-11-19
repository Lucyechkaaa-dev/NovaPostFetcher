package com.novapost.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public record DatabaseConfig(String url, String user, String password){

	public Connection createConnection() throws SQLException{
		if(url == null || url.isBlank()){
			throw new SQLException("Database URL is not configured");
		}
		return DriverManager.getConnection(url, user, password);
	}


	@Override
	public String toString(){
		return "DatabaseConfig{" +
				"url='" + url + '\'' +
				", user='" + user + '\'' +
				", password=[HIDDEN]" +
				'}';
	}
}
