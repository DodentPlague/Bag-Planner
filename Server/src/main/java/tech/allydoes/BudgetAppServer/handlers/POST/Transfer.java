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
import tech.allydoes.FixedPoint;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class Transfer implements RequestHandler{
    Gson gson = new Gson();

    @Override
    public String getRequestName() {
        return "Transfer";
    }

    @Override
    public String getRequestType() {
        return "POST";
    }   
    @Override
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        TransferRequest transferRequest;
        try {
            transferRequest = gson.fromJson(request.content().toString(StandardCharsets.UTF_8), TransferRequest.class);
        } catch (Exception e) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        if (!transferRequest.isValidRequest()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        int userId = AuthenticatedUsers.idFromToken(transferRequest.token);

        FixedPoint oldBalance = AuthenticatedUsers.getBalance(userId);
        FixedPoint newBalance = oldBalance.add(new FixedPoint(transferRequest.dollars, transferRequest.cents));

        Database.executeUpdate(
            "UPDATE User SET balance_dollar=?,balance_cent=? WHERE id=?",
            newBalance.getInteger(),
            newBalance.getDecimal(),
            userId
        );
        return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK));
    }

    private class TransferRequest {
        public String token;
        public Integer accountNumber;
        public Integer dollars;
        public Integer cents;

        public boolean isValidRequest() {
            if (cents >= 100) {
                return false;
            }

            return (
                token != null &&
                accountNumber != null &&
                dollars != null &&
                cents != null
            );
        }
    }
    
}
