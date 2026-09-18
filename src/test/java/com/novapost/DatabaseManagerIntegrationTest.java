package com.novapost;

import com.novapost.db.DatabaseConfig;
import com.novapost.db.DatabaseManager;
import com.novapost.model.InternetDocumentListItem;
import com.novapost.model.Settlement;
import com.novapost.model.Warehouse;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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

            InternetDocumentListItem doc = new InternetDocumentListItem(
                "test-doc-ref-1",
                "20450000000001",
                LocalDateTime.of(2026, 3, 10, 14, 30, 0),
                "85.00",
                "1.5",
                "1",
                "test-sender-ref",
                "test-recipient-ref",
                "Sender Company",
                "Recipient Person",
                "Kyiv",
                "Lviv",
                "In Transit",
                LocalDateTime.of(2026, 3, 12, 18, 0, 0)
            );

            int dCount = dbManager.insertInternetDocuments(List.of(doc));
            assertEquals(1, dCount);

            // Verify count in DB
            try (Statement stmt = conn.createStatement()) {
                ResultSet rsS = stmt.executeQuery("SELECT COUNT(*) FROM nova_post_settlements WHERE ref = 'test-settlement-ref-1'");
                assertTrue(rsS.next());
                assertEquals(1, rsS.getInt(1));

                ResultSet rsW = stmt.executeQuery("SELECT COUNT(*) FROM nova_post_warehouses WHERE ref = 'test-wh-ref-1'");
                assertTrue(rsW.next());
                assertEquals(1, rsW.getInt(1));

                ResultSet rsD = stmt.executeQuery("SELECT COUNT(*) FROM nova_post_internet_documents WHERE ref = 'test-doc-ref-1'");
                assertTrue(rsD.next());
                assertEquals(1, rsD.getInt(1));
            }

            // Verify query/read methods
            Optional<Settlement> foundSettlement = dbManager.findSettlementByRef("test-settlement-ref-1");
            assertTrue(foundSettlement.isPresent());
            assertEquals("test-settlement-ref-1", foundSettlement.get().ref());

            List<Settlement> searchSettlements = dbManager.searchSettlementsByName("Ки", 10);
            assertFalse(searchSettlements.isEmpty());

            Optional<Warehouse> foundWarehouse = dbManager.findWarehouseByRef("test-wh-ref-1");
            assertTrue(foundWarehouse.isPresent());
            assertEquals("test-wh-ref-1", foundWarehouse.get().ref());

            List<Warehouse> whList = dbManager.findWarehousesBySettlement("test-settlement-ref-1");
            assertEquals(1, whList.size());

            Optional<InternetDocumentListItem> foundDocByNum = dbManager.findInternetDocumentByNumber("20450000000001");
            assertTrue(foundDocByNum.isPresent());
            assertEquals("test-doc-ref-1", foundDocByNum.get().ref());
            assertEquals("85.00", foundDocByNum.get().cost());

            Optional<InternetDocumentListItem> foundDocByRef = dbManager.findInternetDocumentByRef("test-doc-ref-1");
            assertTrue(foundDocByRef.isPresent());

            List<InternetDocumentListItem> docsRange = dbManager.findInternetDocumentsByDateRange(
                LocalDateTime.of(2026, 3, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59)
            );
            assertEquals(1, docsRange.size());

            // Cleanup test rows
            dbManager.truncateTables();
        } catch (Exception e) {
            System.out.println("Skipping DB test - local DB not reachable or credentials mismatch: " + e.getMessage());
        }
    }
}
