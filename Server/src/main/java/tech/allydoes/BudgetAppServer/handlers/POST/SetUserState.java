package tech.allydoes.BudgetAppServer.handlers.POST;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import tech.allydoes.Database;
import tech.allydoes.UserState;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class SetUserState implements RequestHandler {
    Gson gson = new Gson();

    @Override
    public String getRequestName() {
        return "SetUserState";
    }

    @Override
    public String getRequestType() {
        return "POST";
    }

    @Override
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> parameters = queryStringDecoder.parameters();

        if (!parameters.containsKey("userId") || parameters.get("userId").isEmpty()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(
                    request.protocolVersion(),
                    HttpResponseStatus.BAD_REQUEST));
        }

        String userId = parameters.get("userId").get(0);
        String body = request.content().toString(io.netty.util.CharsetUtil.UTF_8);
        HttpResponseStatus status = HttpResponseStatus.OK;

        try {
            UserState newState = gson.fromJson(body, UserState.class);
            int rowsUpdated = Database.executeUpdate("UPDATE Accounts SET username=? WHERE id=?", newState.username, userId);
            if (rowsUpdated < 1) {
                status = HttpResponseStatus.NOT_FOUND;
            }
        } catch (JsonSyntaxException e) {
            status = HttpResponseStatus.BAD_REQUEST;
        }

        return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(
                request.protocolVersion(),
                status));
    }

}
