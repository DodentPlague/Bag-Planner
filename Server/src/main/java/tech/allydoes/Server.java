package tech.allydoes;

import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import tech.allydoes.BudgetAppServer.handlers.HttpServerHandler;
import io.netty.channel.socket.SocketChannel;

public class Server {
    private final int port;

    // TODO(emi): Clear tokens that are too old
    public static HashMap<String, Integer> authenticatedUsers = new HashMap<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.DEBUG))
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline channelPipeline = socketChannel.pipeline();
                            channelPipeline.addLast(new HttpResponseEncoder());
                            channelPipeline.addLast(new HttpRequestDecoder());
                            channelPipeline.addLast(new HttpObjectAggregator(1048576));
                            channelPipeline.addLast(new HttpServerHandler());
                        }
                    });
            Channel ch = serverBootstrap.bind(this.port).sync().channel();
            ch.closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Server(8080).start();
    }

    public static String authenticateUser(int userId) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] bytes = new byte[64];
        secureRandom.nextBytes(bytes);
        String token = new String(Base64.getEncoder().encode(bytes));
        authenticatedUsers.put(token, userId);
        return token;
    }
}