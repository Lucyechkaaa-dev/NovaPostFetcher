package com.novapost;

import com.novapost.db.DatabaseConfig;
import com.novapost.db.DatabaseManager;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerUnitTest {

    @Test
    void testEmptyListReturnsZeroWithoutDbConnection() throws SQLException {
        DatabaseConfig dummyConfig = new DatabaseConfig("jdbc:mysql://localhost:3306/dummy", "user", "pass");
        DatabaseManager manager = new DatabaseManager(dummyConfig);

        assertEquals(0, manager.insertSettlements(null));
        assertEquals(0, manager.insertSettlements(Collections.emptyList()));
        assertEquals(0, manager.insertWarehouses(null));
        assertEquals(0, manager.insertWarehouses(Collections.emptyList()));
        assertEquals(0, manager.insertInternetDocuments(null));
        assertEquals(0, manager.insertInternetDocuments(Collections.emptyList()));
    }

    @Test
    void testTruncateTableRejectsInvalidNames() {
        DatabaseConfig dummyConfig = new DatabaseConfig("jdbc:mysql://localhost:3306/dummy", "user", "pass");
        DatabaseManager manager = new DatabaseManager(dummyConfig);

        assertThrows(IllegalArgumentException.class, () -> manager.truncateTable("malicious_table_drop"));
        assertThrows(IllegalArgumentException.class, () -> manager.truncateTable("users; DROP TABLE students;--"));
    }

    @Test
    void testEmptyInputsReturnEmptyResults() throws SQLException {
        DatabaseConfig dummyConfig = new DatabaseConfig("jdbc:mysql://localhost:3306/dummy", "user", "pass");
        DatabaseManager manager = new DatabaseManager(dummyConfig);

        assertTrue(manager.findSettlementByRef(null).isEmpty());
        assertTrue(manager.findSettlementByRef("").isEmpty());
        assertTrue(manager.findWarehouseByRef(null).isEmpty());
        assertTrue(manager.findWarehouseByRef("   ").isEmpty());
        assertTrue(manager.findInternetDocumentByNumber(null).isEmpty());
        assertTrue(manager.findInternetDocumentByRef("").isEmpty());
        assertTrue(manager.searchSettlementsByName(null, 10).isEmpty());
        assertTrue(manager.searchSettlementsByName("", 10).isEmpty());
        assertTrue(manager.findWarehousesBySettlement(null).isEmpty());
        assertTrue(manager.findWarehousesByCity("").isEmpty());
        assertTrue(manager.findInternetDocumentsByDateRange(null, null).isEmpty());
    }
}
