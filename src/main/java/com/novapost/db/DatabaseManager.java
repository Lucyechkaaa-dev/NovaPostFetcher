package com.novapost.db;

import com.novapost.model.Settlement;
import com.novapost.model.Warehouse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class DatabaseManager {
    private final DatabaseConfig databaseConfig;

    public DatabaseManager(DatabaseConfig databaseConfig) {
        this.databaseConfig = databaseConfig;
    }

    private Connection getConnection() throws SQLException {
        return databaseConfig.createConnection();
    }

    public void initializeTables() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

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
        }
    }

    public void truncateTables() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("SET FOREIGN_KEY_CHECKS = 0;");
            stmt.execute("TRUNCATE TABLE nova_post_warehouses;");
            stmt.execute("TRUNCATE TABLE nova_post_settlements;");
            stmt.execute("SET FOREIGN_KEY_CHECKS = 1;");
        }
    }

    public int insertSettlements(List<Settlement> settlements) throws SQLException {
        if (settlements == null || settlements.isEmpty()) {
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

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            for (Settlement s : settlements) {
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

    public int insertWarehouses(List<Warehouse> warehouses) throws SQLException {
        if (warehouses == null || warehouses.isEmpty()) {
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

        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            for (Warehouse w : warehouses) {
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

    public DatabaseConfig getDatabaseConfig() {
        return databaseConfig;
    }
}
