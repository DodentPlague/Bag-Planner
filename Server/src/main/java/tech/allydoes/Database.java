package tech.allydoes;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Database {
    private static final String URL = "jdbc:sqlite:bagplanner_db.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    // this is for stuff like inserts, updates, deletes
    public static int executeUpdate(String sqlStatement, Object... params) {
        try (Connection connection = Database.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            setParameters(preparedStatement, params);
            return preparedStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("SQL Update failed", e);
        }
    }

    // just returns whatever the query was
    public static <T> List<T> queryList(String sqlStatement, Function<ResultSet, T> callbackFunction, Object... params) {
        try (Connection connection = Database.getConnection();
                PreparedStatement preparedStatement = connection.prepareStatement(sqlStatement)) {

            setParameters(preparedStatement, params);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(callbackFunction.apply(resultSet));
                }
                return results;
            }
        } catch (SQLException e) {
            throw new RuntimeException("SQL Query failed", e);
        }
    }

    private static void setParameters(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            statement.setObject(i + 1, params[i]);
        }
    }
}