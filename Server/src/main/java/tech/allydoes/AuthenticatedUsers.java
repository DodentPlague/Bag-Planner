package tech.allydoes;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;

public class AuthenticatedUsers {
    // TODO(emi): Clear tokens that are too old
    public static HashMap<String, Integer> authenticatedUsers = new HashMap<>();

    public static Integer idFromToken(String token) {
        for (Entry<String, Integer> entry : authenticatedUsers.entrySet()) {
            if (entry.getKey().equals(token)) {
                return authenticatedUsers.get(token);
            }
        }

        return null;
    }

    public static String authenticateUser(int userId) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        String token = new String(Base64.getEncoder().encode(bytes));
        authenticatedUsers.put(token, userId);
        return token;
    }

    public static void Logout(String token) {
        for (String key : authenticatedUsers.keySet()) {
            if (key.equals(token)) {
                authenticatedUsers.remove(token);
            }
        }
    }
}
