package io.github.rohergun.dotenv;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DotEnvLoaderTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void clearEnvContext() {
        EnvContext.clear();
    }

    // Parsing logic — no filesystem, fast

    @Nested
    @DisplayName("parse()")
    class ParseTests {

        @Test
        @DisplayName("parses a standard KEY=value line")
        void parsesStandardLine() {
            Map<String, String> result = DotEnvLoader.parse(
                    List.of("FAV_SNACK=donuts"), "test.env"
            );

            assertEquals("donuts", result.get("FAV_SNACK"));
        }

        @Test
        @DisplayName("parses multiple lines")
        void parsesMultipleLines() {
            Map<String, String> result = DotEnvLoader.parse(
                    List.of("FAV_SNACK=donuts", "USERNAME=agentcooper"), "test.env"
            );

            assertEquals("donuts", result.get("FAV_SNACK"));
            assertEquals("agentcooper", result.get("USERNAME"));
        }

        @Test
        @DisplayName("allows empty value after equals sign")
        void allowsEmptyValue() {
            Map<String, String> result = DotEnvLoader.parse(
                    List.of("EMPTY_VAL="), "test.env"
            );

            assertEquals("", result.get("EMPTY_VAL"));
        }

        @Test
        @DisplayName("value containing equals sign is preserved")
        void valueWithEqualsSign() {
            Map<String, String> result = DotEnvLoader.parse(
                    List.of("DB_URL=jdbc:postgresql://host/db?ssl=true"), "test.env"
            );

            assertEquals("jdbc:postgresql://host/db?ssl=true", result.get("DB_URL"));
        }

        @Test
        @DisplayName("skips blank lines")
        void skipsBlankLines() {
            Map<String, String> result = DotEnvLoader.parse(
                    List.of("KEY_ONE=a", "", "   ", "KEY_TWO=b"), "test.env"
            );

            assertEquals(2, result.size());
            assertEquals("a", result.get("KEY_ONE"));
            assertEquals("b", result.get("KEY_TWO"));
        }

        @Test
        @DisplayName("returns empty map for all-blank file")
        void returnsEmptyMapForBlankFile() {
            Map<String, String> result = DotEnvLoader.parse(
                    List.of("", "   ", ""), "test.env"
            );

            assertTrue(result.isEmpty());
        }

        @Test
        @DisplayName("throws on line missing equals sign")
        void throwsOnMissingEquals() {
            DotEnvParseException ex = assertThrows(DotEnvParseException.class, () ->
                    DotEnvLoader.parse(List.of("BADLINE"), "test.env")
            );

            assertEquals(1, ex.getLineNumber());
            assertEquals("BADLINE", ex.getRawLine());
        }

        @Test
        @DisplayName("throws on empty key")
        void throwsOnEmptyKey() {
            DotEnvParseException ex = assertThrows(DotEnvParseException.class, () ->
                    DotEnvLoader.parse(List.of("=value"), "test.env")
            );

            assertEquals(1, ex.getLineNumber());
        }

        @Test
        @DisplayName("throws on lowercase key")
        void throwsOnLowercaseKey() {
            DotEnvParseException ex = assertThrows(DotEnvParseException.class, () ->
                    DotEnvLoader.parse(List.of("db_url=value"), "test.env")
            );

            assertEquals("db_url=value", ex.getRawLine());
        }

        @Test
        @DisplayName("throws on key with hyphen")
        void throwsOnKeyWithHyphen() {
            assertThrows(DotEnvParseException.class, () ->
                    DotEnvLoader.parse(List.of("MY-KEY=value"), "test.env")
            );
        }

        @Test
        @DisplayName("throws on key starting with a digit")
        void throwsOnKeyStartingWithDigit() {
            assertThrows(DotEnvParseException.class, () ->
                    DotEnvLoader.parse(List.of("1KEY=value"), "test.env")
            );
        }

        @Test
        @DisplayName("reports correct line number for malformed line in the middle of file")
        void reportsCorrectLineNumber() {
            DotEnvParseException ex = assertThrows(DotEnvParseException.class, () ->
                    DotEnvLoader.parse(
                            List.of("KEY_ONE=a", "KEY_TWO=b", "bad line", "KEY_THREE=c"),
                            "test.env"
                    )
            );

            assertEquals(3, ex.getLineNumber());
            assertEquals("bad line", ex.getRawLine());
        }

        @Test
        @DisplayName("key with underscore prefix is valid")
        void keyWithUnderscorePrefixIsValid() {
            Map<String, String> result = DotEnvLoader.parse(
                    List.of("_INTERNAL_KEY=secret"), "test.env"
            );

            assertEquals("secret", result.get("_INTERNAL_KEY"));
        }

        @Test
        @DisplayName("key with digits in the middle is valid")
        void keyWithDigitsIsValid() {
            Map<String, String> result = DotEnvLoader.parse(
                    List.of("DB_HOST_2=localhost"), "test.env"
            );

            assertEquals("localhost", result.get("DB_HOST_2"));
        }
    }

    // File loading — touches the filesystem via TempDir

    @Nested
    @DisplayName("load()")
    class LoadTests {

        @Test
        @DisplayName("loads a valid .env file and populates EnvContext")
        void loadsValidFile() throws IOException {
            Path envFile = tempDir.resolve(".env");
            Files.writeString(envFile, "FAV_SNACK=donuts\nUSERNAME=agentcooper\n");

            DotEnvLoader.load(envFile);

            assertEquals("donuts", EnvContext.get("FAV_SNACK"));
            assertEquals("agentcooper", EnvContext.get("USERNAME"));
        }

        @Test
        @DisplayName("does nothing if file does not exist")
        void doesNothingIfFileMissing() {
            Path nonExistent = tempDir.resolve("missing.env");

            assertDoesNotThrow(() -> DotEnvLoader.load(nonExistent));
            assertNull(EnvContext.get("ANY_KEY"));
        }

        @Test
        @DisplayName("loads from custom string path")
        void loadsFromStringPath() throws IOException {
            Path envFile = tempDir.resolve("custom.env");
            Files.writeString(envFile, "API_KEY=abc123\n");

            DotEnvLoader.load(envFile.toString());

            assertEquals("abc123", EnvContext.get("API_KEY"));
        }

        @Test
        @DisplayName("propagates parse exception for malformed file")
        void propagatesParseException() throws IOException {
            Path envFile = tempDir.resolve(".env");
            Files.writeString(envFile, "GOOD_KEY=ok\nbadline\n");

            assertThrows(DotEnvParseException.class, () ->
                    DotEnvLoader.load(envFile)
            );
        }

        @Test
        @DisplayName("handles file with only blank lines")
        void handlesFileWithOnlyBlankLines() throws IOException {
            Path envFile = tempDir.resolve(".env");
            Files.writeString(envFile, "\n\n\n");

            assertDoesNotThrow(() -> DotEnvLoader.load(envFile));
            assertTrue(EnvContext.all().isEmpty());
        }
    }
}