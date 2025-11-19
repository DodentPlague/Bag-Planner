package tech.allydoes.BudgetAppServer.handlers.POST;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.Blake3;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import tech.allydoes.Database;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class RegisterUser implements RequestHandler {
    private static final int USERNAME_CHARACTER_LIMIT = 64;
    private static final int PASSWORD_CHARACTER_LIMIT = 128;

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

        if (!parameters.containsKey("username") || parameters.get("username").isEmpty()
            || !parameters.containsKey("password") || parameters.get("password").isEmpty()) {
           return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        String username = parameters.get("username").get(0);
        String password = parameters.get("password").get(0);
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);

        // TODO(emi): return an error description? a blank "bad request" isn't very helpful
        if (username.length() > USERNAME_CHARACTER_LIMIT) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }
        if (password.length() > PASSWORD_CHARACTER_LIMIT) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        List<Object> existingUsername = Database.queryList("SELECT * FROM User WHERE username=?;", (set) -> {return new Object();}, username);
        if (existingUsername.size() > 0) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.FORBIDDEN));
        }

        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[20];
        secureRandom.nextBytes(salt); 

        byte[] saltedPassword = Arrays.copyOf(salt, salt.length + passwordBytes.length);
        System.arraycopy(passwordBytes, 0, saltedPassword, salt.length, passwordBytes.length);
        byte[] hashedPassword = hashPassword(saltedPassword);

        Database.executeUpdate("INSERT INTO User (username,password,salt,balance_dollar,balance_cent) VALUES (?,?,?,0,0);", username, hashedPassword, salt);
        return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK));
    }

    private byte[] hashPassword(byte[] saltedPassword) {
        // Taken from https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/digest/Blake3.html
        Blake3 hasher = Blake3.initHash();
        hasher.update(saltedPassword);
        byte[] hash = new byte[32];
        hasher.doFinalize(hash);
        return hash;
    }
}
