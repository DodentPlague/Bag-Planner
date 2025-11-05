package tech.allydoes.BudgetAppServer.handlers.GET;

import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import tech.allydoes.FakeDatabase;
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
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> parameters = queryStringDecoder.parameters();
        if (!parameters.containsKey("userId") || parameters.get("userId").isEmpty()) {
           return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }
        String userId = parameters.get("userId").get(0);

        Object data = FakeDatabase.get(userId);
        if (data == null) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND));
        }

        return HttpServerHandler.sendContent(gson.toJson(data), request, channelHandlerContext);
    }
}
