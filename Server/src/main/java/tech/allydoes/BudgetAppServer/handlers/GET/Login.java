package tech.allydoes.BudgetAppServer.handlers.GET;

import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.Blake3;

import com.google.gson.Gson;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.QueryStringDecoder;
import tech.allydoes.AuthenticatedUsers;
import tech.allydoes.Database;
import tech.allydoes.BudgetAppServer.handlers.HttpServerHandler;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class Login implements RequestHandler {
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
    public ChannelFuture processRequest(ChannelHandlerContext ctx, FullHttpRequest request) {

        // --- Parse query params from URI ---
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> params = decoder.parameters();

        String username = getQueryValue(params, "username");
        String password = getQueryValue(params, "password");

        if (username == null || password == null) {
            return ctx.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.BAD_REQUEST));
        }

        // Lookup user
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
        }, username);

        if (loginInfoQuery.isEmpty() || loginInfoQuery.get(0) == null) {
            return ctx.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND));
        }

        LoginInfo loginInfo = (LoginInfo) loginInfoQuery.get(0);

        // Hash & compare
        byte[] saltedPassword = saltPassword(password, loginInfo.salt);
        byte[] hashedPassword = hashPassword(saltedPassword);

        if (Arrays.equals(hashedPassword, loginInfo.hashedPassword)) {
            String token = AuthenticatedUsers.authenticateUser(loginInfo.userId);
            return HttpServerHandler.sendContent(gson.toJson(new LoginResponse(token)), request, ctx);
        }

        return ctx.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.FORBIDDEN));
    }

    // Helpers -------------------------

    private String getQueryValue(Map<String, List<String>> params, String key) {
        return params.containsKey(key) && !params.get(key).isEmpty()
                ? params.get(key).get(0)
                : null;
    }

    private byte[] saltPassword(String password, byte[] salt) {
        byte[] passwordBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] salted = Arrays.copyOf(salt, salt.length + passwordBytes.length);
        System.arraycopy(passwordBytes, 0, salted, salt.length, passwordBytes.length);
        return salted;
    }

    private byte[] hashPassword(byte[] saltedPassword) {
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
    }

    private class LoginResponse {
        public String token;
        public LoginResponse(String token) {
            this.token = token;
        }
    }
}
