package com.novapost;

import com.novapost.config.AppConfig;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AppConfigTest {

    @Test
    void testParseNamedArguments() {
        String[] args = {
            "--api-key=test-key-123",
            "--db-url=jdbc:mysql://localhost:3306/mydb",
            "--db-user=myuser",
            "--db-password=mypass",
            "--delay=500"
        };

        AppConfig config = AppConfig.fromArgs(args);

        assertEquals("test-key-123", config.apiKey());
        assertEquals("jdbc:mysql://localhost:3306/mydb", config.dbUrl());
        assertEquals("myuser", config.dbUser());
        assertEquals("mypass", config.dbPassword());
        assertEquals(500, config.pageDelayMs());
        assertFalse(config.helpRequested());
    }

    @Test
    void testParseShortFlagsAndPositional() {
        String[] args = {
            "-k", "flag-key",
            "-u", "flag-user",
            "-p", "flag-pass",
            "-d", "1000",
            "--db-url=jdbc:mysql://localhost:9961/test"
        };

        AppConfig config = AppConfig.fromArgs(args);

        assertEquals("flag-key", config.apiKey());
        assertEquals("flag-user", config.dbUser());
        assertEquals("flag-pass", config.dbPassword());
        assertEquals("jdbc:mysql://localhost:9961/test", config.dbUrl());
        assertEquals(1000, config.pageDelayMs());
    }

    @Test
    void testPositionalApiKey() {
        String[] args = { "raw-api-key-456" };

        AppConfig config = AppConfig.fromArgs(args);

        assertEquals("raw-api-key-456", config.apiKey());
        assertNull(config.dbUrl());
        assertNull(config.dbUser());
        assertNull(config.dbPassword());
        assertEquals(AppConfig.DEFAULT_PAGE_DELAY_MS, config.pageDelayMs());
        assertEquals(AppConfig.DEFAULT_API_URL, config.apiUrl());
    }

    @Test
    void testHelpFlag() {
        String[] args = { "--help" };
        AppConfig config = AppConfig.fromArgs(args);
        assertTrue(config.helpRequested());
    }
}
