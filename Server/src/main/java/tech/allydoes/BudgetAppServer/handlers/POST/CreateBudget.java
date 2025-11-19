package tech.allydoes.BudgetAppServer.handlers.POST;

import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import tech.allydoes.AuthenticatedUsers;
import tech.allydoes.Database;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class CreateBudget implements RequestHandler{
    Gson gson = new Gson();
    
    @Override
    public String getRequestName() {
        return "CreateBudget";
    }

    @Override
    public String getRequestType() {
        return "POST";
    }

    @Override
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        CreateBudgetRequest createRequest;
        try {
            createRequest = gson.fromJson(request.content().toString(StandardCharsets.UTF_8), CreateBudgetRequest.class);
        } catch (Exception e) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        if (!createRequest.isValidRequest()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        Integer userId = AuthenticatedUsers.idFromToken(createRequest.token);
        try {
            Database.executeUpdate(
                "INSERT INTO Budget VALUES (?,?,?,?,0,0)", 
                userId, 
                createRequest.name, 
                createRequest.dollars,
                createRequest.cents
            );
        } catch (Exception e) {
            e.printStackTrace();
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.INTERNAL_SERVER_ERROR));
        }

        return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK));
    }

    private class CreateBudgetRequest {
        public String token;
        public String name;
        public Integer dollars;
        public Integer cents;

        public boolean isValidRequest() {
            if (cents >= 100) {
                return false;
            }

            return (
                token != null &&
                name != null &&
                dollars != null &&
                cents != null
            );
        }
    }
}
