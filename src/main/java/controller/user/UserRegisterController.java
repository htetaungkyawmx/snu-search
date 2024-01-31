package controller.user;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.User;
import service.DataStore;
import utility.JsonResponseHelper;
import utility.UserLogManager;

import java.io.IOException;
import java.util.Map;

import static utility.ParseQuery.parseQuery;
import static utility.SendResponse.sendResponse;
import static utility.Validator.isValidPassword;
import static utility.Validator.isValidToRegister;

public class UserRegisterController implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if ("GET".equals(exchange.getRequestMethod())) {
            Map<String, String> parameters = parseQuery(exchange.getRequestURI().getQuery());
            String userId = parameters.get("id");
            String password = parameters.get("passwd");
            boolean isValidToRegister = isValidToRegister(userId);
            boolean isValidPassword = isValidPassword(password);
            if (userId == null || password == null ) {
                sendResponse(exchange, 400, JsonResponseHelper.createResponse("Invalid ID or password.", userId));
            }else if (!isValidPassword){
                sendResponse(exchange, 400, JsonResponseHelper.createResponse("Password is too short or doesn't start with an alphabet character.", userId));
            } else {
                if (!isValidToRegister) {
                    sendResponse(exchange, 409, JsonResponseHelper.createResponse("User with the same ID already exists.", userId));
                } else {
                    // Create a new user and add it to the data store
                    User newUser = new User(userId, password);
                    DataStore.addUser(newUser);

                    UserLogManager.write(userId, exchange);

                    String jsonResponse = JsonResponseHelper.createResponse("User Register successfully.", userId);
                    sendResponse(exchange, 200, jsonResponse);

                    System.out.println(String.format("User %s Register ", userId));
                }
            }
        }
    }
}
