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

public class PayUser implements RequestHandler{
    Gson gson = new Gson();

    @Override
    public String getRequestName() {
        return "PayUser";
    }

    @Override
    public String getRequestType() {
        return "POST";
    }
    
    @Override
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        PayUserRequest payRequest;
        try {
            payRequest = gson.fromJson(request.content().toString(StandardCharsets.UTF_8), PayUserRequest.class);
        } catch (Exception e) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        if (!payRequest.isValidRequest()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        FixedPoint transferAmount = new FixedPoint(payRequest.dollars, payRequest.cents);

        int senderId = AuthenticatedUsers.idFromToken(payRequest.token);
        int recipientId = AuthenticatedUsers.idFromUsername(payRequest.recipientUsername);

        FixedPoint senderBalance = AuthenticatedUsers.getBalance(senderId);
        FixedPoint recipientBalance = AuthenticatedUsers.getBalance(recipientId);

        FixedPoint newSenderBalance = senderBalance.subtract(transferAmount);
        FixedPoint newRecipientBalance = recipientBalance.add(transferAmount);

        Database.executeUpdate(
            "UPDATE Users SET balance_dollar=?, balance_cent=? WHERE id=?", 
            newSenderBalance.getInteger(),
            newSenderBalance.getDecimal(),
            senderId
        );
        Database.executeUpdate(
            "UPDATE Users SET balance_dollar=?, balance_cent=? WHERE id=?", 
            newRecipientBalance.getInteger(),
            newRecipientBalance.getDecimal(),
            recipientId
        );

        return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK));
    }

    private class PayUserRequest {
        public String token;
        public String recipientUsername;
        public Integer dollars;
        public Integer cents;

        public boolean isValidRequest() {
            if (cents >= 100) {
                return false;
            }

            return (
                token != null && 
                recipientUsername != null && 
                dollars != null && 
                cents != null
            );
        }
    }
}
