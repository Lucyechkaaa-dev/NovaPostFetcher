package com.novapost.db;

import com.novapost.model.InternetDocumentListItem;
import com.novapost.model.Settlement;
import com.novapost.model.Warehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class DatabaseManager{

	private final DatabaseConfig databaseConfig;

	public DatabaseManager(DatabaseConfig databaseConfig){
		this.databaseConfig = databaseConfig;
	}

	private Connection getConnection() throws SQLException{
		return databaseConfig.createConnection();
	}

	public void initializeTables() throws SQLException{
		try(Connection conn = getConnection();
		    Statement stmt = conn.createStatement()){

			stmt.execute("""
					    CREATE TABLE IF NOT EXISTS nova_post_settlements (
					        ref VARCHAR(36) PRIMARY KEY,
					        settlement_type VARCHAR(64),
					        settlement_type_description VARCHAR(128),
					        description VARCHAR(255) NOT NULL,
					        description_ru VARCHAR(255),
					        region VARCHAR(36),
					        regions_description VARCHAR(128),
					        area VARCHAR(36),
					        area_description VARCHAR(128),
					        latitude VARCHAR(32),
					        longitude VARCHAR(32),
					        index1 VARCHAR(16),
					        index2 VARCHAR(16),
					        index_coatsu1 VARCHAR(32),
					        warehouse_flag VARCHAR(8),
					        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
					        INDEX idx_settlement_desc (description),
					        INDEX idx_settlement_region (regions_description),
					        INDEX idx_settlement_area (area_description)
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
					""");

			stmt.execute("""
					    CREATE TABLE IF NOT EXISTS nova_post_warehouses (
					        ref VARCHAR(36) PRIMARY KEY,
					        site_key VARCHAR(32),
					        number VARCHAR(16),
					        description VARCHAR(255) NOT NULL,
					        short_address VARCHAR(255),
					        phone VARCHAR(64),
					        type_of_warehouse VARCHAR(64),
					        city_ref VARCHAR(36),
					        city_description VARCHAR(128),
					        settlement_ref VARCHAR(36),
					        settlement_description VARCHAR(128),
					        settlement_area_description VARCHAR(128),
					        settlement_regions_description VARCHAR(128),
					        longitude VARCHAR(32),
					        latitude VARCHAR(32),
					        total_max_weight_allowed VARCHAR(16),
					        place_max_weight_allowed VARCHAR(16),
					        warehouse_status VARCHAR(64),
					        category_of_warehouse VARCHAR(64),
					        direct VARCHAR(32),
					        district_code VARCHAR(32),
					        warehouse_index VARCHAR(32),
					        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
					        INDEX idx_wh_city_ref (city_ref),
					        INDEX idx_wh_settlement_ref (settlement_ref),
					        INDEX idx_wh_number (number),
					        INDEX idx_wh_city_desc (city_description)
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
					""");

			stmt.execute("""
					    CREATE TABLE IF NOT EXISTS nova_post_internet_documents (
					        ref VARCHAR(36) PRIMARY KEY,
					        int_doc_number VARCHAR(32) NOT NULL,
					        date_time DATETIME,
					        cost VARCHAR(32),
					        weight VARCHAR(32),
					        seats_amount VARCHAR(32),
					        city_sender VARCHAR(36),
					        city_recipient VARCHAR(36),
					        sender_description VARCHAR(255),
					        recipient_description VARCHAR(255),
					        city_sender_description VARCHAR(128),
					        city_recipient_description VARCHAR(128),
					        state_name VARCHAR(128),
					        estimated_delivery_date DATETIME,
					        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
					        INDEX idx_doc_number (int_doc_number),
					        INDEX idx_doc_date_time (date_time),
					        INDEX idx_doc_state (state_name)
					    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
					""");
		}
	}

	public void truncateTables() throws SQLException{
		try(Connection conn = getConnection();
		    Statement stmt = conn.createStatement()){
			stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
			stmt.execute("TRUNCATE TABLE nova_post_warehouses;");
			stmt.execute("TRUNCATE TABLE nova_post_settlements;");
			stmt.execute("TRUNCATE TABLE nova_post_internet_documents;");
			stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
		}
	}

	public void truncateTable(String tableName) throws SQLException{
		if(tableName == null || tableName.isBlank()){
			return;
		}
		if(!List.of("nova_post_warehouses", "nova_post_settlements", "nova_post_internet_documents").contains(tableName)){
			throw new IllegalArgumentException("Unknown table name: " + tableName);
		}
		try(Connection conn = getConnection();
		    Statement stmt = conn.createStatement()){
			stmt.execute("TRUNCATE TABLE " + tableName + ";");
		}
	}

	public int insertSettlements(List<Settlement> settlements) throws SQLException{
		if(settlements == null || settlements.isEmpty()){
			return 0;
		}

		String sql = """
				    INSERT INTO nova_post_settlements (
				        ref, settlement_type, settlement_type_description, description, description_ru,
				        region, regions_description, area, area_description,
				        latitude, longitude, index1, index2, index_coatsu1, warehouse_flag
				    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				    ON DUPLICATE KEY UPDATE
				        settlement_type = VALUES(settlement_type),
				        settlement_type_description = VALUES(settlement_type_description),
				        description = VALUES(description),
				        description_ru = VALUES(description_ru),
				        region = VALUES(region),
				        regions_description = VALUES(regions_description),
				        area = VALUES(area),
				        area_description = VALUES(area_description),
				        latitude = VALUES(latitude),
				        longitude = VALUES(longitude),
				        index1 = VALUES(index1),
				        index2 = VALUES(index2),
				        index_coatsu1 = VALUES(index_coatsu1),
				        warehouse_flag = VALUES(warehouse_flag);
				""";

		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){

			conn.setAutoCommit(false);
			for(Settlement s : settlements){
				ps.setString(1, s.ref());
				ps.setString(2, s.settlementType());
				ps.setString(3, s.settlementTypeDescription());
				ps.setString(4, s.description());
				ps.setString(5, s.descriptionRu());
				ps.setString(6, s.region());
				ps.setString(7, s.regionsDescription());
				ps.setString(8, s.area());
				ps.setString(9, s.areaDescription());
				ps.setString(10, s.latitude());
				ps.setString(11, s.longitude());
				ps.setString(12, s.index1());
				ps.setString(13, s.index2());
				ps.setString(14, s.indexCOATSU1());
				ps.setString(15, s.warehouse());
				ps.addBatch();
			}

			int[] results = ps.executeBatch();
			conn.commit();
			return results.length;
		}
	}

	public int insertWarehouses(List<Warehouse> warehouses) throws SQLException{
		if(warehouses == null || warehouses.isEmpty()){
			return 0;
		}

		String sql = """
				    INSERT INTO nova_post_warehouses (
				        ref, site_key, number, description, short_address, phone, type_of_warehouse,
				        city_ref, city_description, settlement_ref, settlement_description,
				        settlement_area_description, settlement_regions_description, longitude, latitude,
				        total_max_weight_allowed, place_max_weight_allowed, warehouse_status, category_of_warehouse,
				        direct, district_code, warehouse_index
				    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				    ON DUPLICATE KEY UPDATE
				        site_key = VALUES(site_key),
				        number = VALUES(number),
				        description = VALUES(description),
				        short_address = VALUES(short_address),
				        phone = VALUES(phone),
				        type_of_warehouse = VALUES(type_of_warehouse),
				        city_ref = VALUES(city_ref),
				        city_description = VALUES(city_description),
				        settlement_ref = VALUES(settlement_ref),
				        settlement_description = VALUES(settlement_description),
				        settlement_area_description = VALUES(settlement_area_description),
				        settlement_regions_description = VALUES(settlement_regions_description),
				        longitude = VALUES(longitude),
				        latitude = VALUES(latitude),
				        total_max_weight_allowed = VALUES(total_max_weight_allowed),
				        place_max_weight_allowed = VALUES(place_max_weight_allowed),
				        warehouse_status = VALUES(warehouse_status),
				        category_of_warehouse = VALUES(category_of_warehouse),
				        direct = VALUES(direct),
				        district_code = VALUES(district_code),
				        warehouse_index = VALUES(warehouse_index);
				""";

		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){

			conn.setAutoCommit(false);
			for(Warehouse w : warehouses){
				ps.setString(1, w.ref());
				ps.setString(2, w.siteKey());
				ps.setString(3, w.number());
				ps.setString(4, w.description());
				ps.setString(5, w.shortAddress());
				ps.setString(6, w.phone());
				ps.setString(7, w.typeOfWarehouse());
				ps.setString(8, w.cityRef());
				ps.setString(9, w.cityDescription());
				ps.setString(10, w.settlementRef());
				ps.setString(11, w.settlementDescription());
				ps.setString(12, w.settlementAreaDescription());
				ps.setString(13, w.settlementRegionsDescription());
				ps.setString(14, w.longitude());
				ps.setString(15, w.latitude());
				ps.setString(16, w.totalMaxWeightAllowed());
				ps.setString(17, w.placeMaxWeightAllowed());
				ps.setString(18, w.warehouseStatus());
				ps.setString(19, w.categoryOfWarehouse());
				ps.setString(20, w.direct());
				ps.setString(21, w.districtCode());
				ps.setString(22, w.warehouseIndex());
				ps.addBatch();
			}

			int[] results = ps.executeBatch();
			conn.commit();
			return results.length;
		}
	}

	public int insertInternetDocuments(List<InternetDocumentListItem> documents) throws SQLException{
		if(documents == null || documents.isEmpty()){
			return 0;
		}

		String sql = """
				    INSERT INTO nova_post_internet_documents (
				        ref, int_doc_number, date_time, cost, weight, seats_amount,
				        city_sender, city_recipient, sender_description, recipient_description,
				        city_sender_description, city_recipient_description, state_name,
				        estimated_delivery_date
				    ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
				    ON DUPLICATE KEY UPDATE
				        int_doc_number = VALUES(int_doc_number),
				        date_time = VALUES(date_time),
				        cost = VALUES(cost),
				        weight = VALUES(weight),
				        seats_amount = VALUES(seats_amount),
				        city_sender = VALUES(city_sender),
				        city_recipient = VALUES(city_recipient),
				        sender_description = VALUES(sender_description),
				        recipient_description = VALUES(recipient_description),
				        city_sender_description = VALUES(city_sender_description),
				        city_recipient_description = VALUES(city_recipient_description),
				        state_name = VALUES(state_name),
				        estimated_delivery_date = VALUES(estimated_delivery_date);
				""";

		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){

			conn.setAutoCommit(false);
			for(InternetDocumentListItem doc : documents){
				ps.setString(1, doc.ref());
				ps.setString(2, doc.intDocNumber());
				ps.setTimestamp(3, doc.dateTime() != null ? Timestamp.valueOf(doc.dateTime()) : null);
				ps.setString(4, doc.cost());
				ps.setString(5, doc.weight());
				ps.setString(6, doc.seatsAmount());
				ps.setString(7, doc.citySender());
				ps.setString(8, doc.cityRecipient());
				ps.setString(9, doc.senderDescription());
				ps.setString(10, doc.recipientDescription());
				ps.setString(11, doc.citySenderDescription());
				ps.setString(12, doc.cityRecipientDescription());
				ps.setString(13, doc.stateName());
				ps.setTimestamp(14, doc.estimatedDeliveryDate() != null ? Timestamp.valueOf(doc.estimatedDeliveryDate()) : null);
				ps.addBatch();
			}

			int[] results = ps.executeBatch();
			conn.commit();
			return results.length;
		}
	}

	public Optional<Settlement> findSettlementByRef(String ref) throws SQLException{
		if(ref == null || ref.isBlank()){
			return Optional.empty();
		}
		String sql = "SELECT * FROM nova_post_settlements WHERE ref = ?";
		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, ref);
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()){
					return Optional.of(mapSettlement(rs));
				}
			}
		}
		return Optional.empty();
	}

	public List<Settlement> searchSettlementsByName(String namePrefix, int limit) throws SQLException{
		if(namePrefix == null || namePrefix.isBlank()){
			return List.of();
		}
		int safeLimit = Math.max(1, Math.min(limit, 1000));
		String sql = "SELECT * FROM nova_post_settlements WHERE description LIKE ? LIMIT ?";
		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, namePrefix + "%");
			ps.setInt(2, safeLimit);
			try(ResultSet rs = ps.executeQuery()){
				List<Settlement> list = new ArrayList<>();
				while(rs.next()){
					list.add(mapSettlement(rs));
				}
				return list;
			}
		}
	}

	public Optional<Warehouse> findWarehouseByRef(String ref) throws SQLException{
		if(ref == null || ref.isBlank()){
			return Optional.empty();
		}
		String sql = "SELECT * FROM nova_post_warehouses WHERE ref = ?";
		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, ref);
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()){
					return Optional.of(mapWarehouse(rs));
				}
			}
		}
		return Optional.empty();
	}

	public List<Warehouse> findWarehousesBySettlement(String settlementRef) throws SQLException{
		if(settlementRef == null || settlementRef.isBlank()){
			return List.of();
		}
		String sql = "SELECT * FROM nova_post_warehouses WHERE settlement_ref = ? ORDER BY CAST(number AS UNSIGNED), number";
		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, settlementRef);
			try(ResultSet rs = ps.executeQuery()){
				List<Warehouse> list = new ArrayList<>();
				while(rs.next()){
					list.add(mapWarehouse(rs));
				}
				return list;
			}
		}
	}

	public List<Warehouse> findWarehousesByCity(String cityRef) throws SQLException{
		if(cityRef == null || cityRef.isBlank()){
			return List.of();
		}
		String sql = "SELECT * FROM nova_post_warehouses WHERE city_ref = ? ORDER BY CAST(number AS UNSIGNED), number";
		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, cityRef);
			try(ResultSet rs = ps.executeQuery()){
				List<Warehouse> list = new ArrayList<>();
				while(rs.next()){
					list.add(mapWarehouse(rs));
				}
				return list;
			}
		}
	}

	public Optional<InternetDocumentListItem> findInternetDocumentByNumber(String intDocNumber) throws SQLException{
		if(intDocNumber == null || intDocNumber.isBlank()){
			return Optional.empty();
		}
		String sql = "SELECT * FROM nova_post_internet_documents WHERE int_doc_number = ?";
		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, intDocNumber);
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()){
					return Optional.of(mapInternetDocument(rs));
				}
			}
		}
		return Optional.empty();
	}

	public Optional<InternetDocumentListItem> findInternetDocumentByRef(String ref) throws SQLException{
		if(ref == null || ref.isBlank()){
			return Optional.empty();
		}
		String sql = "SELECT * FROM nova_post_internet_documents WHERE ref = ?";
		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setString(1, ref);
			try(ResultSet rs = ps.executeQuery()){
				if(rs.next()){
					return Optional.of(mapInternetDocument(rs));
				}
			}
		}
		return Optional.empty();
	}

	public List<InternetDocumentListItem> findInternetDocumentsByDateRange(LocalDateTime from, LocalDateTime to) throws SQLException{
		if(from == null || to == null){
			return List.of();
		}
		String sql = "SELECT * FROM nova_post_internet_documents WHERE date_time >= ? AND date_time <= ? ORDER BY date_time DESC";
		try(Connection conn = getConnection();
		    PreparedStatement ps = conn.prepareStatement(sql)){
			ps.setTimestamp(1, Timestamp.valueOf(from));
			ps.setTimestamp(2, Timestamp.valueOf(to));
			try(ResultSet rs = ps.executeQuery()){
				List<InternetDocumentListItem> list = new ArrayList<>();
				while(rs.next()){
					list.add(mapInternetDocument(rs));
				}
				return list;
			}
		}
	}

	private Settlement mapSettlement(ResultSet rs) throws SQLException{
		return new Settlement(
				rs.getString("ref"),
				rs.getString("settlement_type"),
				rs.getString("latitude"),
				rs.getString("longitude"),
				rs.getString("description"),
				rs.getString("description_ru"),
				rs.getString("settlement_type_description"),
				null,
				rs.getString("region"),
				rs.getString("regions_description"),
				null,
				rs.getString("area"),
				rs.getString("area_description"),
				null,
				rs.getString("index1"),
				rs.getString("index2"),
				rs.getString("index_coatsu1"),
				rs.getString("warehouse_flag")
		);
	}

	private Warehouse mapWarehouse(ResultSet rs) throws SQLException{
		return new Warehouse(
				rs.getString("site_key"),
				rs.getString("description"),
				null,
				rs.getString("short_address"),
				null,
				rs.getString("phone"),
				rs.getString("type_of_warehouse"),
				rs.getString("ref"),
				rs.getString("number"),
				rs.getString("city_ref"),
				rs.getString("city_description"),
				null,
				rs.getString("settlement_ref"),
				rs.getString("settlement_description"),
				rs.getString("settlement_area_description"),
				rs.getString("settlement_regions_description"),
				null,
				rs.getString("longitude"),
				rs.getString("latitude"),
				null,
				null,
				null,
				null,
				null,
				null,
				rs.getString("total_max_weight_allowed"),
				rs.getString("place_max_weight_allowed"),
				rs.getString("warehouse_status"),
				null,
				rs.getString("category_of_warehouse"),
				null,
				rs.getString("direct"),
				rs.getString("district_code"),
				rs.getString("warehouse_index"),
				null,
				null,
				null
		);
	}

	private InternetDocumentListItem mapInternetDocument(ResultSet rs) throws SQLException{
		Timestamp dt = rs.getTimestamp("date_time");
		Timestamp edd = rs.getTimestamp("estimated_delivery_date");
		return new InternetDocumentListItem(
				rs.getString("ref"),
				rs.getString("int_doc_number"),
				dt != null ? dt.toLocalDateTime() : null,
				rs.getString("cost"),
				rs.getString("weight"),
				rs.getString("seats_amount"),
				rs.getString("city_sender"),
				rs.getString("city_recipient"),
				rs.getString("sender_description"),
				rs.getString("recipient_description"),
				rs.getString("city_recipient_description"),
				rs.getString("city_sender_description"),
				rs.getString("state_name"),
				edd != null ? edd.toLocalDateTime() : null
		);
	}

	public DatabaseConfig getDatabaseConfig(){
		return databaseConfig;
	}
}
