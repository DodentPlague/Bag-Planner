package tech.allydoes.BudgetAppServer.handlers.GET;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import tech.allydoes.AuthenticatedUsers;
import tech.allydoes.Database;
import tech.allydoes.FixedPoint;
import tech.allydoes.BudgetAppServer.handlers.HttpServerHandler;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class GetUserState implements RequestHandler {
    Gson gson = new Gson();

    @Override
    public String getRequestName() {
        return "GetUserState";
    }

    @Override
    public String getRequestType() {
        return "GET";
    }

    @Override
    /**
     * Most of this function is boilerplate, the real guts of it is the data query at the bottom
     * That query will call the UserStateResponse constructor which will get all known data
     * then processRequest will return it
     * @see UserStateResponse
     */
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        // --- NEW: parse token from query string instead of GET body ---
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = decoder.parameters();

        String token = getQueryValue(params, "token");
        if (token == null) {
            return channelHandlerContext.writeAndFlush(
                    new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST)
            );
        }

        Integer userId = AuthenticatedUsers.idFromToken(token);
        if (userId == null) {
            return channelHandlerContext.writeAndFlush(
                    new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND)
            );
        }

        // The aforementioned guts
        // TODO(emi): differentiate between an SQLException (return 500) and non-existant id (return 404)
        List<UserStateResponse> data = Database.queryList(
                "SELECT * FROM Users WHERE id=?;",
                (resultSet) -> {
                    // We don't really use the row here, just need to confirm the user exists
                    return new UserStateResponse(userId);
                },
                userId
        );

        if (data.size() == 0 || data.get(0) == null) {
            return channelHandlerContext.writeAndFlush(
                    new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND)
            );
        }

        return HttpServerHandler.sendContent(gson.toJson(data.get(0)), request, channelHandlerContext);
    }

    // Helper for safely extracting query params
    private String getQueryValue(Map<String, List<String>> params, String key) {
        return params.containsKey(key) && !params.get(key).isEmpty()
                ? params.get(key).get(0)
                : null;
    }

    /**
     * UserStateResponse contains all known data about a user
     *
     * username: Self-explanatory
     * budgets: The budgets that the user keeps track of
     * connections: The user's contacts
     *
     * balanceDollars: The amount of dollars the user currently has in their account
     * balanceCents: The amount of cents the user currently has
     *
     * For example, if balanceDollars was 10 and balanceCents was 50, the user would have $10.50
     *
     * The balance is divided into dollars and cents to avoid floating-point rounding errors
     * @see FixedPoint
     */
    private class UserStateResponse {
        public String username;
        public List<Budget> budgets;
        public List<String> connections;
        public int balanceDollars;
        public int balanceCents;

        public UserStateResponse(int userId) {
            // The exceptions in the queries here will most likely never be hit unless the db is really messed up
            // So on functions that need something to be returned, a default value (like an empty string or empty object) is returned
            // Otherwise a stack trace is printed and that's the end of it

            // The list returned by this query will be every budget the user has
            this.budgets = Database.queryList(
                    "SELECT * FROM Budgets WHERE user_id=?",
                    (resultSet) -> {
                        Budget budget = new Budget();

                        try {
                            budget.name = resultSet.getString("name");
                            budget.allocated_dollars = resultSet.getInt("allocated_funds_dollars");
                            budget.allocated_cents = resultSet.getInt("allocated_funds_cents");
                            budget.used_dollars = resultSet.getInt("funds_used_dollars");
                            budget.used_cents = resultSet.getInt("funds_used_cents");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            // Return an "empty" budget on failure so the list still has a value
                            return new Budget();
                        }

                        return budget;
                    },
                    userId
            );

            // The list returned by this query will be every connection the user has
            this.connections = Database.queryList(
                    "SELECT * FROM Connections WHERE id1=? OR id2=?",
                    (resultSet) -> {
                        int id1;
                        int id2;

                        try {
                            id1 = resultSet.getInt("id1");
                            id2 = resultSet.getInt("id2");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return "";
                        }

                        // We always want to show the other user's username, never our own
                        if (id1 == userId) {
                            return AuthenticatedUsers.usernameFromId(id2);
                        } else {
                            return AuthenticatedUsers.usernameFromId(id1);
                        }
                    },
                    userId, userId
            );

            FixedPoint balance = AuthenticatedUsers.getBalance(userId);

            this.username = AuthenticatedUsers.usernameFromId(userId);
            this.balanceDollars = balance.getInteger();
            this.balanceCents = balance.getDecimal();
        }
    }

    public class Budget {
        public String name;
        public int allocated_dollars;
        public int allocated_cents;
        public int used_dollars;
        public int used_cents;
    }
}
