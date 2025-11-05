package tech.allydoes.BudgetAppServer.handlers;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface RequestHandler {
    ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request);
    String getRequestName();
    String getRequestType();
}
