package io.github.rohergun.dotenv;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Pattern;

public class DotEnvLoader {
    private static final String DEFAULT_FILENAME = ".env";

    // Valid key : one or more uppercase letters, digits, or underscores,
    // must start with a letter or underscore.
    private static final Pattern KEY_PATTERN = Pattern.compile("^[A-Z_][A-Z0-9_]*$");

    public static void load(){
        load(Paths.get(DEFAULT_FILENAME));
    }

    public static void load(String filepath){
        load(Paths.get(filepath));
    }

    public static void load(Path path) {
        if (!Files.exists(path)) {
            return;
        }
//        Read lines from the input path
//        Parse the lines to source names
//        Todo EnvContext.putAll(parsed);
    }


}
