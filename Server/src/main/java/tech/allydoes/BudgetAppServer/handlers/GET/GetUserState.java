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
        List<UserStateResponse> data = Database.queryList("SELECT * FROM User WHERE id=?;", (resultSet) -> {
            try {
                UserStateResponse userState = new UserStateResponse();
                userState.username = resultSet.getString("username");
                return userState;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
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
    }
}
