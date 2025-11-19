package tech.allydoes.BudgetAppServer.handlers.GET;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.digest.Blake3;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import tech.allydoes.AuthenticatedUsers;
import tech.allydoes.Database;
import tech.allydoes.BudgetAppServer.handlers.HttpServerHandler;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class Login implements RequestHandler{
    Gson gson = new Gson();

    @Override
    public String getRequestName() {
        return "Login";
    }

    @Override
    public String getRequestType() {
        return "GET";
    }

    @Override
    /**
     *  Check if the provided username and password matches a login
     *  If so, return a token. 
     * 
     *  @see AuthenticatedUsers for more about tokens
     */ 
    public ChannelFuture processRequest(ChannelHandlerContext channelHandlerContext, FullHttpRequest request) {
        // Standard request boilerplate
        LoginRequest loginRequest;
        try {
            loginRequest = gson.fromJson(request.content().toString(StandardCharsets.UTF_8), LoginRequest.class);
        } catch (JsonSyntaxException e) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        if (!loginRequest.isValidRequest()) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        // Convert the password to bytes to make it easier to combine with the salt later
        byte[] passwordBytes = loginRequest.password.getBytes(StandardCharsets.UTF_8);

        List<Object> loginInfoQuery = Database.queryList("SELECT * FROM Users WHERE username=?", (resultSet) -> {
            try {
                LoginInfo info = new LoginInfo();
                info.userId = resultSet.getInt("id");
                info.hashedPassword = resultSet.getBytes("password");
                info.salt = resultSet.getBytes("salt");

                return info;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        }, loginRequest.username);

        if (loginInfoQuery.size() == 0 || loginInfoQuery.get(0) == null) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND));
        }

        LoginInfo loginInfo = (LoginInfo) loginInfoQuery.get(0);

        // Combine the salt with the password
        byte[] saltedPassword = Arrays.copyOf(loginInfo.salt, loginInfo.salt.length + passwordBytes.length);
        System.arraycopy(passwordBytes, 0, saltedPassword, loginInfo.salt.length, passwordBytes.length);

        byte[] hashedPassword = hashPassword(saltedPassword);

        if (Arrays.equals(hashedPassword, loginInfo.hashedPassword)) {
            String token = AuthenticatedUsers.authenticateUser(loginInfo.userId);
            LoginResponse response = new LoginResponse(token);
            return HttpServerHandler.sendContent(gson.toJson(response), request, channelHandlerContext);
        }
        else {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.FORBIDDEN));
        }
    }

    /**
     * Hashes the password with blake3
     */
    private byte[] hashPassword(byte[] saltedPassword) {
        // Taken from https://commons.apache.org/proper/commons-codec/apidocs/org/apache/commons/codec/digest/Blake3.html
        Blake3 hasher = Blake3.initHash();
        hasher.update(saltedPassword);
        byte[] hash = new byte[32];
        hasher.doFinalize(hash);
        return hash;
    }

    private class LoginInfo {
        public int userId;
        public byte[] hashedPassword;
        public byte[] salt;
    };

    private class LoginRequest {
        public String username;
        public String password;

        public boolean isValidRequest() {
            return (username != null && password != null);
        }
    }

    private class LoginResponse {
        public String token;

        public LoginResponse(String token) {
            this.token = token;
        }
    }
}
