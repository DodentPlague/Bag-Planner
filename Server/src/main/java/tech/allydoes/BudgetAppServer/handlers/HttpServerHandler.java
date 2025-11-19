package tech.allydoes.BudgetAppServer.handlers;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import tech.allydoes.BudgetAppServer.handlers.GET.GetUserState;
import tech.allydoes.BudgetAppServer.handlers.GET.Login;
import tech.allydoes.BudgetAppServer.handlers.POST.AddConnection;
import tech.allydoes.BudgetAppServer.handlers.POST.CreateBudget;
import tech.allydoes.BudgetAppServer.handlers.POST.Logout;
import tech.allydoes.BudgetAppServer.handlers.POST.PayUser;
import tech.allydoes.BudgetAppServer.handlers.POST.RegisterUser;
import tech.allydoes.BudgetAppServer.handlers.POST.Transfer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

import static io.netty.handler.codec.http.HttpHeaderNames.*;
import static io.netty.handler.codec.http.HttpHeaderValues.CLOSE;

public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger LOGGER = LogManager.getLogger(HttpServerHandler.class);
    private final HashMap<String, RequestHandler> requestHandlers = new HashMap<>();

    public HttpServerHandler() {
        LOGGER.info("Initializing HTTP Server Handler and registering request handlers");
        RequestHandler[] handlers = {
            new GetUserState(),
            new RegisterUser(),
            new Login(),
            new Logout(),
            new CreateBudget(),
            new AddConnection(),
            new PayUser(),
            new Transfer()
        };
        for (RequestHandler handler : handlers) {
            requestHandlers.put(handler.getRequestName().toLowerCase(), handler);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext channelHandlerContext) {
        channelHandlerContext.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        ChannelFuture future = processRequest(channelHandlerContext, request);

        request.headers().set(CONNECTION, CLOSE);
        future.addListener(ChannelFutureListener.CLOSE);
    }

    private ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        String path = request.uri().split("\\?", 15)[0].toLowerCase().substring(1);
        if (requestHandlers.containsKey(path) && requestHandlers.get(path).getRequestType().equalsIgnoreCase(request.method().name())) {
            return requestHandlers.get(path).processRequest(channelHandlerContext, request);
        } else {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable cause) {
        LOGGER.error("Unhandled exception in pipeline", cause);

        channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR));
        channelHandlerContext.close();
    }

    public static ChannelFuture sendContent(String content, FullHttpRequest request, ChannelHandlerContext channelHandlerContext) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(content, CharsetUtil.UTF_8);
        DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(
                request.protocolVersion(),
                HttpResponseStatus.OK,
                byteBuf
        );
        httpResponse.headers().set(CONTENT_TYPE, "application/json; charset=utf-8");
        httpResponse.headers().set(CONTENT_LENGTH, byteBuf.readableBytes());
        return channelHandlerContext.writeAndFlush(httpResponse);
    }
}
