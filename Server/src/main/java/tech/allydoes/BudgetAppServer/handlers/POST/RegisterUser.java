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
    /**
     * Takes a username and password and registers a new user in the database
     * 
     * The password is hashed with blake3 and salted with a random 20 byte value
     * 
     * Usernames have a character limit of 64 and passwords have a character limit of 128
     */
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        // Standard request boilerplate
        RegisterRequest registerRequest;
        try {
            registerRequest = gson.fromJson(request.content().toString(StandardCharsets.UTF_8), RegisterRequest.class);
        } catch (Exception e) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        if (!registerRequest.isValidRequest()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        // Convert the password into a byte array to make it easier to combine with the salt later
        byte[] passwordBytes = registerRequest.password.getBytes(StandardCharsets.UTF_8);

        // Make sure that the username isn't a duplicate
        List<Object> existingUsername = Database.queryList("SELECT * FROM Users WHERE username=?;", (set) -> {return new Object();}, registerRequest.username);
        if (existingUsername.size() > 0) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.FORBIDDEN));
        }

        // Use a cryptographically secure PRNG to make sure our salt isn't predictable
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[20];
        secureRandom.nextBytes(salt); 

        // Combine the raw password and the salt
        byte[] saltedPassword = Arrays.copyOf(salt, salt.length + passwordBytes.length);
        System.arraycopy(passwordBytes, 0, saltedPassword, salt.length, passwordBytes.length);

        byte[] hashedPassword = hashPassword(saltedPassword);

        Database.executeUpdate("INSERT INTO Users (username,password,salt,balance_dollar,balance_cent) VALUES (?,?,?,0,0);", registerRequest.username, hashedPassword, salt);
        return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.OK));
    }

    /**
     * Hashes a password with blake3
     */
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
