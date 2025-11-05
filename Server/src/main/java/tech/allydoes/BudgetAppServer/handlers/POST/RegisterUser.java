package tech.allydoes.BudgetAppServer.handlers.POST;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import tech.allydoes.FakeDatabase;
import tech.allydoes.BudgetAppServer.handlers.HttpServerHandler;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class RegisterUser implements RequestHandler {
    @Override
    public String getRequestName() {
        return "RegisterUser";
    }

    @Override
    public String getRequestType() {
        return "POST";
    }

    @Override
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        QueryStringDecoder queryStringDecoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> parameters = queryStringDecoder.parameters();

        UUID userId = UUID.randomUUID();
        while (FakeDatabase.exists(userId.toString())) {
            userId = UUID.randomUUID();
        }

        String body = request.content().toString(io.netty.util.CharsetUtil.UTF_8);
        FakeDatabase.set(userId.toString(), body);

        return HttpServerHandler.sendContent("{\"userId\":\"" + userId.toString() + "\"}", request, channelHandlerContext);
    }
}
