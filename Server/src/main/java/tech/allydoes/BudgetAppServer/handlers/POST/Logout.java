package tech.allydoes.BudgetAppServer.handlers.POST;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import tech.allydoes.AuthenticatedUsers;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class Logout implements RequestHandler{
    Gson gson = new Gson();
    
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
        LogoutRequest logoutRequest;
        try {
            logoutRequest = gson.fromJson(request.content().toString(StandardCharsets.UTF_8), LogoutRequest.class);
        } catch (Exception e) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        if (!logoutRequest.isValidRequest()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        AuthenticatedUsers.Logout(logoutRequest.token);
        return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK));
    } 

    private class LogoutRequest {
        public String token;

        public boolean isValidRequest() {
            return token != null;
        }
    }
}
