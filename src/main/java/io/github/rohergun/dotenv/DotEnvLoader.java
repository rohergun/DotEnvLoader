package io.github.rohergun.dotenv;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
//        Todo Read lines from the input path
//        Todo Parse the lines to source names
//        Todo EnvContext.putAll(parsed);
    }

    static Map<String, String> parse(List<String> lines){
        Map<String, String> parsedLines = new HashMap<>();

        for (int i=0; i<lines.size(); i++){
            String raw = lines.get(i);

            if (raw.isBlank()) {
                continue;
            }

            int equalsIndex = raw.indexOf('=');
            if (equalsIndex < 0){
                // Todo add custom exception
                System.err.println("Missing '=' separator between key=value");
            }

            String key = raw.substring(0, equalsIndex);
            String val = raw.substring(equalsIndex + 1);
            if (key.isEmpty()){
                // Todo add custom exception
                System.err.println("Key is missing");
            }
            if (!KEY_PATTERN.matcher(key).matches()){
                // Todo add custom exception
                System.err.println("Key is not valid pattern");
            }
            parsedLines.put(key, val);
        }
        return Collections.unmodifiableMap(parsedLines);
    }


    private static List<String> readLines(Path path) {
        try{
            return Files.readAllLines(path);
        }catch (IOException e) {
            // Todo add custom error handling
            e.printStackTrace();
        }
        return null;
    }

}
