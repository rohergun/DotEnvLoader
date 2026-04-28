package io.github.rohergun.dotenv;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnvContextTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void clearStore() {
        EnvContext.clear();
    }

    @Test
    @DisplayName("returns null for unknown key")
    void returnsNullForUnknownKey() {
        assertNull(EnvContext.get("TOTALLY_UNKNOWN_KEY_XYZ"));
    }

    @Test
    @DisplayName("returns default value for unknown key")
    void returnsDefaultForUnknownKey() {
        assertEquals("8080", EnvContext.get("SERVER_PORT", "8080"));
    }

    @Test
    @DisplayName("returns loaded value when key exists in store")
    void returnsLoadedValue() {
        EnvContext.put("MY_KEY", "hello");

        assertEquals("hello", EnvContext.get("MY_KEY"));
    }

    @Test
    @DisplayName(".env value takes precedence over OS environment variable")
    void dotEnvTakesPrecedenceOverOs() throws IOException {
        // PATH is guaranteed to exist in the OS environment on any system
        Path envFile = tempDir.resolve(".env");
        Files.writeString(envFile, "PATH=/custom/path\n");
        DotEnvLoader.load(envFile);

        assertEquals("/custom/path", EnvContext.get("PATH"));
    }

    @Test
    @DisplayName("falls back to OS environment when key is not in store")
    void fallsBackToOsEnv() {
        // PATH exists in OS env and we have NOT loaded it into the store
        String result = EnvContext.get("PATH");

        // Should get the real OS value, not null
        assertNotNull(result);
    }

    @Test
    @DisplayName("clear() removes all loaded entries")
    void clearRemovesAllEntries() {
        EnvContext.put("TEMP_KEY", "value");

        EnvContext.clear();

        assertNull(EnvContext.get("TEMP_KEY"));
        assertTrue(EnvContext.all().isEmpty());
    }

    @Test
    @DisplayName("all() returns only .env store entries, not OS environment")
    void allReturnsOnlyStoreEntries() {
        EnvContext.put("ONLY_IN_STORE", "yes");

        Map<String, String> all = EnvContext.all();

        assertTrue(all.containsKey("ONLY_IN_STORE"));
        // PATH comes from OS env — should NOT appear in all()
        assertFalse(all.containsKey("PATH"));
    }

    @Test
    @DisplayName("all() returns a snapshot, not a live reference")
    void allReturnsSnapshot() {
        EnvContext.put("SNAP_KEY", "before");

        Map<String, String> snapshot = EnvContext.all();
        EnvContext.clear();

        // Snapshot captured before clear() — should still hold the value
        assertEquals("before", snapshot.get("SNAP_KEY"));
        // But the live store is now empty
        assertTrue(EnvContext.all().isEmpty());
    }

    @Test
    @DisplayName("put() overwrites an existing key")
    void putOverwritesExistingKey() {
        EnvContext.put("OVERWRITE_KEY", "first");
        EnvContext.put("OVERWRITE_KEY", "second");

        assertEquals("second", EnvContext.get("OVERWRITE_KEY"));
    }

    @Test
    @DisplayName("get() with default does not return default when key exists")
    void getWithDefaultIgnoresDefaultWhenKeyExists() {
        EnvContext.put("EXISTING_KEY", "real_value");

        assertEquals("real_value", EnvContext.get("EXISTING_KEY", "fallback"));
    }
}