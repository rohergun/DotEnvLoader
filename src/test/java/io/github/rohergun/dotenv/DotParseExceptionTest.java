package io.github.rohergun.dotenv;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DotEnvParseExceptionTest {

    @Test
    @DisplayName("stores source file name")
    void storesSourceFile() {
        DotEnvParseException ex = new DotEnvParseException(
                "my.env", 3, "badline", "missing '=' separator"
        );

        assertEquals("my.env", ex.getSourceFile());
    }

    @Test
    @DisplayName("stores line number")
    void storesLineNumber() {
        DotEnvParseException ex = new DotEnvParseException(
                "my.env", 3, "badline", "missing '=' separator"
        );

        assertEquals(3, ex.getLineNumber());
    }

    @Test
    @DisplayName("stores raw line")
    void storesRawLine() {
        DotEnvParseException ex = new DotEnvParseException(
                "my.env", 3, "badline", "missing '=' separator"
        );

        assertEquals("badline", ex.getRawLine());
    }

    @Test
    @DisplayName("message contains file name, line number, and raw line")
    void messageContainsKeyDetails() {
        DotEnvParseException ex = new DotEnvParseException(
                "my.env", 3, "badline", "missing '=' separator"
        );

        String msg = ex.getMessage();
        assertTrue(msg.contains("my.env"),   "message should contain file name");
        assertTrue(msg.contains("3"),        "message should contain line number");
        assertTrue(msg.contains("badline"),  "message should contain raw line");
    }

    @Test
    @DisplayName("is a RuntimeException so it does not need to be declared")
    void isRuntimeException() {
        DotEnvParseException ex = new DotEnvParseException(
                "my.env", 1, "bad", "reason"
        );

        assertInstanceOf(RuntimeException.class, ex);
    }
}