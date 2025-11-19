package tech.allydoes.BudgetAppServer.handlers.GET;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;

import com.google.gson.Gson;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
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
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        UserStateRequest userStateRequest;
        try {
            userStateRequest = gson.fromJson(request.content().toString(StandardCharsets.UTF_8), UserStateRequest.class);
        } catch (Exception e) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        if (!userStateRequest.isValidRequest()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        Integer userId = AuthenticatedUsers.idFromToken(userStateRequest.token);
        if (userId == null) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND));
        }

        // TODO: differentiate between an SQLException (return 500) and non-existant id (return 404)
        List<UserStateResponse> data = Database.queryList("SELECT * FROM Users WHERE id=?;", (resultSet) -> {
            UserStateResponse userState = new UserStateResponse(userId);
            return userState;
        }, userId);
        
        if (data.size() == 0 || data.get(0) == null) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND));
        }

        return HttpServerHandler.sendContent(gson.toJson(data.get(0)), request, channelHandlerContext);
    }

    private class UserStateRequest {
        public String token;

        public boolean isValidRequest() {
            return token != null;
        }
    }

    private class UserStateResponse {
        public String username;
        public List<Budget> budgets;
        public List<String> connections;
        public int balanceDollars;
        public int balanceCents;

        public UserStateResponse(int userId) {
            List<Budget> budgetQuery = Database.queryList("SELECT * FROM Budgets WHERE user_id=?", (resultSet) -> {
                Budget budget = new Budget();

                try {
                    budget.name = resultSet.getString("name");
                    budget.allocated_dollars = resultSet.getInt("allocated_funds_dollars");
                    budget.allocated_cents = resultSet.getInt("allocated_funds_cents");
                    budget.used_dollars = resultSet.getInt("funds_used_dollars");
                    budget.used_cents = resultSet.getInt("funds_used_cents");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                
                return budget;
            }, userId);
            List<String> connectionQuery = Database.queryList("SELECT * FROM Connections WHERE id1=? OR id2=?", (resultSet) -> {
                int id1;
                int id2;

                try {
                    id1 = resultSet.getInt("id1");
                    id2 = resultSet.getInt("id2");
                } catch (SQLException e) {
                    return "";
                }

                if (id1 == userId) {
                    return AuthenticatedUsers.usernameFromId(id2);
                }
                else {
                    return AuthenticatedUsers.usernameFromId(id1);
                }
            }, userId, userId);

            FixedPoint balance = AuthenticatedUsers.getBalance(userId);

            this.username = AuthenticatedUsers.usernameFromId(userId);
            this.budgets = budgetQuery;
            this.connections = connectionQuery;
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
