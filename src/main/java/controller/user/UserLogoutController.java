package controller.user;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.AuthService;
import utility.JsonResponseHelper;
import utility.UserLogManager;

import java.io.IOException;
import java.util.Map;

import static utility.ParseQuery.parseQuery;
import static utility.SendResponse.sendResponse;

public class UserLogoutController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> parameters = parseQuery(exchange.getRequestURI().getQuery());
            String userId = parameters.get("id");
            String password = parameters.get("passwd");
            if (userId == null || password == null) {
                sendResponse(exchange, 400, JsonResponseHelper.createResponse("Invalid ID or password.", userId));
            } else {
                if (!AuthService.isUserAuthenticated(userId)) {
                    sendResponse(exchange, 403, JsonResponseHelper.createResponse("User is not authenticated.", userId));
                } else {
                    AuthService.deauthenticateUser(userId);
                    String jsonResponse = JsonResponseHelper.createResponse("User Logout successfully.", userId);
                    sendResponse(exchange, 200, jsonResponse);

                    System.out.println(String.format("User %s Logout ", userId));
                }
            }
        }
    }
}
