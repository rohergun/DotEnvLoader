package io.github.rohergun.dotenv;

import java.util.HashMap;
import java.util.Map;

public final class EnvContext {
    // storing .env values
    private static final Map<String, String> store = new HashMap<>();

    // Writing to DotEnvLoader
    static void put(String key, String value){
        if (key == null){
            throw new IllegalArgumentException("Key must be defined");
        }
        if (value == null){
            throw new IllegalArgumentException("Value must be defined");
        }
        synchronized (store) {
            store.put(key, value);
        }
    }

    static void putAll(Map<String, String> envEntries){
        synchronized (store){
            store.putAll(envEntries);
        }
    }

    // Reading from DotEnvLoader
    public static String get(String key){
        // Todo
    }

    // returns all values that loaded to .env
    public static Map<String, String> all(){
        // Todo
    }

}
