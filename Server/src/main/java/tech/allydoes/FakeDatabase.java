package tech.allydoes;

import java.util.HashMap;
import java.util.Map;

public class FakeDatabase {
    private static final Map<String, Object> storage = new HashMap<>();
    
    public static void set(String userId, Object data) {
        storage.put(userId, data);
    }

    public static Object get(String userId) {
        return storage.get(userId);
    }

    public static boolean exists(String userId) {
        return storage.containsKey(userId);
    }

    public static void delete(String userId) {
        storage.remove(userId);
    }
}
