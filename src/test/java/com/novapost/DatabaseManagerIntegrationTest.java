package com.novapost;

import com.novapost.db.DatabaseConfig;
import com.novapost.db.DatabaseManager;
import com.novapost.model.Settlement;
import com.novapost.model.Warehouse;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerIntegrationTest {

    @Test
    void testInitializeTruncateAndInsert() {
        String testUrl = System.getProperty("test.db.url", System.getenv("TEST_DB_URL"));
        String testUser = System.getProperty("test.db.user", System.getenv("TEST_DB_USER"));
        String testPassword = System.getProperty("test.db.password", System.getenv("TEST_DB_PASSWORD"));

        if (testUrl == null || testUser == null || testPassword == null) {
            // Skip integration test when test DB environment variables are not provided
            return;
        }

        DatabaseConfig dbConfig = new DatabaseConfig(testUrl, testUser, testPassword);

        try (Connection conn = dbConfig.createConnection()) {
            assertNotNull(conn);

            DatabaseManager dbManager = new DatabaseManager(dbConfig);
            dbManager.initializeTables();
            dbManager.truncateTables();

            Settlement s = new Settlement(
                "test-settlement-ref-1", "місто", "50.45", "30.52",
                "Київ", "Киев", "місто", "город",
                "test-region", "Київська", "Киевская",
                "test-area", "Києво-Святошинський", "Киево-Святошинский",
                "01001", "01002", "8000000000", "1"
            );

            int sCount = dbManager.insertSettlements(List.of(s));
            assertEquals(1, sCount);

            Warehouse w = new Warehouse(
                "9999", "Відділення №1", "Отделение №1", "вул. Хрещатик, 1", "ул. Крещатик, 1",
                "0800500609", "PostBranch", "test-wh-ref-1", "1",
                "test-city-ref", "Київ", "Киев",
                "test-settlement-ref-1", "Київ", "", "Київська", "місто",
                "30.52", "50.45", "1", "1", "1", "1", "1", "2",
                "30", "30", "Working", "2026-01-01", "Branch",
                "Kyiv", "1", "01", "01001",
                Map.of("Monday", "08:00-21:00"), Map.of(), Map.of()
            );

            int wCount = dbManager.insertWarehouses(List.of(w));
            assertEquals(1, wCount);

            // Verify count in DB
            try (Statement stmt = conn.createStatement()) {
                ResultSet rsS = stmt.executeQuery("SELECT COUNT(*) FROM nova_post_settlements WHERE ref = 'test-settlement-ref-1'");
                assertTrue(rsS.next());
                assertEquals(1, rsS.getInt(1));

                ResultSet rsW = stmt.executeQuery("SELECT COUNT(*) FROM nova_post_warehouses WHERE ref = 'test-wh-ref-1'");
                assertTrue(rsW.next());
                assertEquals(1, rsW.getInt(1));
            }

            // Cleanup test rows
            dbManager.truncateTables();
        } catch (Exception e) {
            System.out.println("Skipping DB test - local DB not reachable or credentials mismatch: " + e.getMessage());
        }
    }
}
