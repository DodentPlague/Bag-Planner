package tech.allydoes.BudgetAppServer.handlers.GET;

import java.nio.charset.StandardCharsets;
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
import tech.allydoes.AuthenticatedUsers;
import tech.allydoes.Database;
import tech.allydoes.BudgetAppServer.handlers.HttpServerHandler;
import tech.allydoes.BudgetAppServer.handlers.RequestHandler;

public class Login implements RequestHandler{
    @Override
    public String getRequestName() {
        return "Login";
    }

    @Override
    public String getRequestType() {
        return "GET";
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

        List<Object> loginInfoQuery = Database.queryList("SELECT * FROM User WHERE username=?", (resultSet) -> {
            try {
                LoginInfo info = new LoginInfo();
                info.userId = resultSet.getInt("id");
                info.hashedPassword = resultSet.getBytes("password");
                info.salt = resultSet.getBytes("salt");

                if (info.hashedPassword == null || info.salt == null) {
                    return null;
                }

                return info;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }, username);

        if (loginInfoQuery.size() == 0 || loginInfoQuery.get(0) == null) {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.NOT_FOUND));
        }

        LoginInfo loginInfo = (LoginInfo) loginInfoQuery.get(0);

        byte[] saltedPassword = Arrays.copyOf(loginInfo.salt, loginInfo.salt.length + passwordBytes.length);
        System.arraycopy(passwordBytes, 0, saltedPassword, loginInfo.salt.length, passwordBytes.length);
        byte[] hashedPassword = hashPassword(saltedPassword);

        if (Arrays.equals(hashedPassword, loginInfo.hashedPassword)) {
            String token = AuthenticatedUsers.authenticateUser(loginInfo.userId);
            return HttpServerHandler.sendContent(token, request, channelHandlerContext);
        }
        else {
            return channelHandlerContext.writeAndFlush(new DefaultFullHttpResponse(request.protocolVersion(), HttpResponseStatus.FORBIDDEN));
        }
    }

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
}
