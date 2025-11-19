package tech.allydoes.BudgetAppServer.handlers.POST;

import java.util.List;
import java.util.Map;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import tech.allydoes.AuthenticatedUsers;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class Logout implements RequestHandler{
    @Override
    public String getRequestName() {
        return "Logout";
    }

    @Override
    public String getRequestType() {
        return "POST";
    }

    @Override
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> parameters = queryStringDecoder.parameters();

        if (!parameters.containsKey("token") || parameters.get("token").isEmpty()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        String token = parameters.get("token").get(0);

        AuthenticatedUsers.Logout(token);
        return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK));
    } 
}
