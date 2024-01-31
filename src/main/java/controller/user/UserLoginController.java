package controller.user;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.User;
import service.DataStore;
import service.AuthService;
import utility.JsonResponseHelper;
import utility.UserLogManager;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static utility.ParseQuery.parseQuery;
import static utility.SendResponse.sendResponse;
import static utility.Validator.isValidPassword;

public class UserLoginController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> parameters = parseQuery(exchange.getRequestURI().getQuery());
            String userId = parameters.get("id");
            String password = parameters.get("passwd");
            if (userId == null || password == null ) {
                sendResponse(exchange, 400, JsonResponseHelper.createResponse("Invalid ID or password.", userId));
            } else {
                User user = DataStore.getUser(userId);
                if (user == null && user.isActive()) {
                    sendResponse(exchange, 403, JsonResponseHelper.createResponse("User is not authenticated.", userId));
                } else {
                    AuthService.authenticateUser(userId, user);

                    UserLogManager.write(userId, exchange);

                    String jsonResponse = JsonResponseHelper.createResponse("User Login successfully.", userId);
                    sendResponse(exchange, 200, jsonResponse);

                    System.out.println(String.format("User %s Login ", userId));
                }
            }
        }
    }
}
