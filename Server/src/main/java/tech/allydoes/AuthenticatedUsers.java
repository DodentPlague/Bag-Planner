package tech.allydoes;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
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

    public static String usernameFromId(int userId) {
        List<Object> usernameQuery = Database.queryList("SELECT * FROM Users WHERE id=?", (resultSet) -> {
            try {
                return resultSet.getString("username");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "";
        }, userId);
        return (String) usernameQuery.get(0);
    }
    
    public static int idFromUsername(String username) {
        List<Object> idQuery = Database.queryList("SELECT * FROM Users WHERE username=?", (resultSet) -> {
            try {
                return resultSet.getInt("id");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "";
        }, username);
        return (int) idQuery.get(0);
    }

    public static FixedPoint getBalance(int userId) {
        List<Object> idQuery = Database.queryList("SELECT * FROM Users WHERE id=?", (resultSet) -> {
            try {
                int dollars = resultSet.getInt("balance_dollar");
                int cents = resultSet.getInt("balance_cent");

                return new FixedPoint(dollars, cents);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }, userId);

        if (idQuery.size() == 0 || idQuery.get(0) == null) {
            return null;
        }

        return (FixedPoint) idQuery.get(0);
    }
}
