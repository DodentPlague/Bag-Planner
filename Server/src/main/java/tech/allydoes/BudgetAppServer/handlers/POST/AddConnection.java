package tech.allydoes.BudgetAppServer.handlers.POST;

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
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class AddConnection implements RequestHandler{
    Gson gson = new Gson();
    
    @Override
    public String getRequestName() {
        return "AddConnection";
    }

    @Override
    public String getRequestType() {
        return "POST";
    }

    @Override
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        AddConnectionRequest connectionRequest;
        try {
            connectionRequest = gson.fromJson(request.content().toString(StandardCharsets.UTF_8), AddConnectionRequest.class);
        } catch (Exception e) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        if (!connectionRequest.isValidRequest()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        List<Object> userIdQuery = Database.queryList("SELECT * FROM Users where username=?", (resultSet) -> {
            try {
                return resultSet.getInt("id");
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return null;
        }, connectionRequest.connectionUsername);

        if (userIdQuery.size() == 0 || userIdQuery.get(0) == null) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND));
        }

        int userId = AuthenticatedUsers.idFromToken(connectionRequest.token);
        int connectionUserId = (int) userIdQuery.get(0);

        Database.executeUpdate("INSERT INTO Connections VALUES (?,?)", userId, connectionUserId);
        return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK));
    }

    private class AddConnectionRequest {
        public String token;
        public String connectionUsername;

        public boolean isValidRequest() {
            return (token != null && connectionUsername != null);
        }
    }
}
