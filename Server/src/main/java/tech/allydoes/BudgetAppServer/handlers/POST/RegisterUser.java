package tech.allydoes.BudgetAppServer.handlers.POST;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.digest.Blake3;

import com.google.gson.Gson;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import tech.allydoes.Database;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class RegisterUser implements RequestHandler {
    private static final int USERNAME_CHARACTER_LIMIT = 64;
    private static final int PASSWORD_CHARACTER_LIMIT = 128;

    Gson gson = new Gson();
    
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
        RegisterRequest registerRequest;
        try {
            registerRequest = gson.fromJson(request.content().toString(StandardCharsets.UTF_8), RegisterRequest.class);
        } catch (Exception e) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        if (!registerRequest.isValidRequest()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        byte[] passwordBytes = registerRequest.password.getBytes(StandardCharsets.UTF_8);

        List<Object> existingUsername = Database.queryList("SELECT * FROM Users WHERE username=?;", (set) -> {return new Object();}, registerRequest.username);
        if (existingUsername.size() > 0) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.FORBIDDEN));
        }

        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[20];
        secureRandom.nextBytes(salt); 

        byte[] saltedPassword = Arrays.copyOf(salt, salt.length + passwordBytes.length);
        System.arraycopy(passwordBytes, 0, saltedPassword, salt.length, passwordBytes.length);
        byte[] hashedPassword = hashPassword(saltedPassword);

        Database.executeUpdate("INSERT INTO Users (username,password,salt,balance_dollar,balance_cent) VALUES (?,?,?,0,0);", registerRequest.username, hashedPassword, salt);
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

    private class RegisterRequest {
        public String username;
        public String password;

        public boolean isValidRequest() {
            if (username == null || password == null) {
                return false;
            }

            if (username.length() > USERNAME_CHARACTER_LIMIT) {
                return false;
            }
            if (password.length() > PASSWORD_CHARACTER_LIMIT) {
                return false;
            }

            return true;
        }
    }
}
