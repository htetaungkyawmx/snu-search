package controller.user;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.User;
import service.AuthService;
import service.DataStore;
import utility.JsonResponseHelper;
import utility.UserLogManager;

import java.io.IOException;
import java.util.Map;

import static utility.ParseQuery.parseQuery;
import static utility.SendResponse.sendResponse;

public class UserLeaveController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> parameters = parseQuery(exchange.getRequestURI().getQuery());
            String userId = parameters.get("id");
            String password = parameters.get("passwd");

            if (userId == null || password == null) {
                // Respond with a 400 Bad Request error
                sendResponse(exchange, 400, JsonResponseHelper.createResponse("Invalid ID or password.", userId));
            } else {

                if (!AuthService.isUserAuthenticated(userId)) {
                    sendResponse(exchange, 403, JsonResponseHelper.createResponse("User is not authenticated.", userId));
                }

                User user = DataStore.getUser(userId);

                user.setActive(false);
                DataStore.addUser(user);

                AuthService.deauthenticateUser(userId);
                String jsonResponse = JsonResponseHelper.createResponse("User Leave successfully.", userId);
                sendResponse(exchange, 200, jsonResponse);

                System.out.println(String.format("User %s Leave ", userId));

            }
        }
    }
}
